package com.sistema.gestion.Services.Admin.Finance;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import com.sistema.gestion.Services.Profiles.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.sistema.gestion.DTO.PagedResponse;
import com.sistema.gestion.DTO.PaymentWithStudentDTO;
import com.sistema.gestion.Models.Admin.Finance.Payment;
import com.sistema.gestion.Models.Admin.Management.Course;
import com.sistema.gestion.Models.Profiles.Student;
import com.sistema.gestion.Repositories.Admin.Finance.CashRegisterRepository;
import com.sistema.gestion.Repositories.Admin.Finance.PaymentRepository;
import com.sistema.gestion.Repositories.Admin.Management.CourseRepository;
import com.sistema.gestion.Repositories.Profiles.StudentRepository;
import com.sistema.gestion.Utils.CourseStatus;
import com.sistema.gestion.Utils.PaymentType;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PaymentService {

	private final PaymentRepository paymentRepo;
	private final StudentRepository studentRepo;
	private final CourseRepository courseRepo;
	private final CashRegisterRepository cashRegisterRepo;
	private final UserService userService;

	// ? ==================== MÉTODOS PÚBLICOS ====================

	// ? Consultas
	public Mono<PagedResponse<Payment>> getPaymentsPaged(int page, int size) {
		PageRequest pageRequest = PageRequest.of(page, size);
		Mono<Long> totalElementsMono = paymentRepo.count();
		Flux<Payment> paymenteFlux = paymentRepo.findPaymentsPaged(pageRequest);

		return Mono.zip(totalElementsMono, paymenteFlux.collectList())
				.map(tuple -> new PagedResponse<>(
						tuple.getT2(), // Lista de facturas
						tuple.getT1(), // Total de registros
						page,
						size));
	}

	public Mono<PaymentWithStudentDTO> getPaymentWithDetailsById(String paymentId) {
		return paymentRepo.findById(paymentId)
				.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
						"No se encontró el pago con el ID: " + paymentId)))
				.flatMap(this::mapPaymentToDTO);
	}

	public Mono<PagedResponse<PaymentWithStudentDTO>> getPaymentsHasDebt(Boolean hasDebt, int page, int size) {
		PageRequest pageRequest = PageRequest.of(page, size);
		Mono<Long> totalElementsMono = paymentRepo.countByHasDebt(hasDebt);
		Flux<PaymentWithStudentDTO> paymentsFlux = paymentRepo.findAllByHasDebt(hasDebt, pageRequest)
				.flatMap(this::mapPaymentToDTO);

		return Mono.zip(totalElementsMono, paymentsFlux.collectList())
				.map(tuple -> new PagedResponse<>(
						tuple.getT2(), // Lista de pagos con deuda
						tuple.getT1(), // Total de registros
						page,
						size));
	}

	public Mono<PagedResponse<PaymentWithStudentDTO>> getPaymentsHasDebtByMonth(Integer year, Integer month, int page, int size) {
		LocalDate startOfMonth = YearMonth.of(year, month).atDay(1);
		LocalDate endOfMonth = startOfMonth.plusMonths(1);
		PageRequest pageRequest = PageRequest.of(page, size);

		Mono<Long> totalElementsMono = paymentRepo.countWithDebtInMonth(startOfMonth, endOfMonth);
		Flux<PaymentWithStudentDTO> paymentsFlux = paymentRepo.findAllWithDebtInMonth(startOfMonth, endOfMonth, pageRequest)
				.flatMap(this::mapPaymentToDTO);

		return Mono.zip(totalElementsMono, paymentsFlux.collectList())
				.map(tuple -> new PagedResponse<>(
						tuple.getT2(), // Lista de pagos con deuda
						tuple.getT1(), // Total de registros
						page,
						size));
	}

	public Mono<PagedResponse<PaymentWithStudentDTO>> getAllPaymentsByStudentId(String studentId, int page, int size) {
		PageRequest pageRequest = PageRequest.of(page, size);
		Mono<Long> totalElementsMono = paymentRepo.countByStudentId(studentId);
		Flux<PaymentWithStudentDTO> paymentsFlux = paymentRepo.findAllByStudentId(studentId, pageRequest)
				.flatMap(this::mapPaymentToDTO);

		return Mono.zip(totalElementsMono, paymentsFlux.collectList())
				.map(tuple -> new PagedResponse<>(
						tuple.getT2(), // Lista de pagos con información de estudiante y curso
						tuple.getT1(), // Total de registros
						page,
						size));
	}

	// ? Operaciones de escritura
	public Mono<Payment> registerPayment(Payment payment, String username) {
		YearMonth currentMonth = YearMonth.now();
		LocalDate startOfMonth = currentMonth.atDay(1);
		LocalDate endOfMonth = currentMonth.atEndOfMonth();

		if (payment.getStudentId() == null || payment.getStudentId().isEmpty()) {
			return monoError(HttpStatus.BAD_REQUEST, "El ID del estudiante está vacío o nulo");
		}

		if (payment.getId() != null && !payment.getId().isEmpty()) {
			return monoError(HttpStatus.BAD_REQUEST, "El pago ya tiene un ID registrado, no se puede almacenar un nuevo pago con ID ya registrado.");
		}

		return userService.getFullName(username)
				.flatMap(name -> {
					if (payment.getPaymentType() == PaymentType.CUOTE) {
						return paymentRepo.findByPaymentDueDateBetween(startOfMonth, endOfMonth)
								.filter(existingPayment -> existingPayment.getPaymentType() == PaymentType.CUOTE
										&& existingPayment.getStudentId().equals(payment.getStudentId())
										&& existingPayment.getCourseId().equals(payment.getCourseId()))
								.hasElements()
								.flatMap(doesExist -> {
									if (doesExist) {
										return monoError(HttpStatus.CONFLICT,
												"Ya existe una cuota registrada para este estudiante y curso en el mes actual.");
									}
									return savePayment(payment, startOfMonth, name.getName() + " " + name.getSurname());
								});
					}
					return savePayment(payment, startOfMonth, name.getName() + " " + name.getSurname());
				});
	}

	public Mono<List<PaymentWithStudentDTO>> getPaymentsByDay(String username, Integer year, Integer month, Integer day) {
		LocalDate startOfDay = LocalDate.of(year, month, day);
		LocalDate endOfDay = startOfDay.plusDays(1);
		return userService.getFullName(username)
				.flatMap(name -> paymentRepo.findByPaymentDueDateBetween(startOfDay, endOfDay)
						.collectList()
						.flatMap(payments -> Flux.fromIterable(payments)
								.flatMap(this::mapPaymentToDTO)
								.collectList()));
	}

	public Flux<Payment> registerMonthlyPayments(String username) {
		YearMonth currentMonth = YearMonth.now();
		LocalDate startOfMonth = currentMonth.atDay(1);
		LocalDate endOfMonth = currentMonth.atEndOfMonth();

		return userService.getFullName(username)
				.flatMapMany(name -> paymentRepo.findByPaymentDueDateBetween(startOfMonth, endOfMonth)
						.filter(payment -> payment.getPaymentType() == PaymentType.CUOTE)
						.collectList()
						.flatMapMany(existingPayments -> generatePaymentsForActiveStudents(existingPayments, startOfMonth, name.getName() + " " + name.getSurname())));
	}

	public Mono<Payment> doPayment(String paymentId, Payment payment, String username) {
		if (!payment.getId().equals(paymentId)) {
			return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Los IDs del Pago a efectuar en la base de datos con el del cuerpo de la solicitud no coinciden."));
		}

		return userService.getFullName(username)
				.flatMap(name -> validateAndProcessPayment(payment, name.getName() + " " + name.getSurname()))
				.onErrorResume(ResponseStatusException.class, Mono::error);
	}

	public Mono<Payment> updatePaymentInfo(String paymentId, Payment payment, String user) {
		if (!payment.getId().equals(paymentId)) {
			return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Los IDs del Pago a actualizar no coinciden."));
		}
		return paymentRepo.findById(paymentId)
				.flatMap(existingPayment -> {
					existingPayment.setPaymentType(payment.getPaymentType());
					existingPayment.setUpdatedAt(LocalDateTime.now());
					existingPayment.setModifiedBy(user);
					existingPayment.setPaidAmount(payment.getPaidAmount());
					existingPayment.setPaymentAmount(payment.getPaymentAmount());
					return paymentRepo.save(existingPayment);
				});
	}

	public Mono<Long> countPaymentHasDebt() {
		return paymentRepo.countAllByHasDebt(true);
	}

	public Mono<Void> deletePayment(String id) {
		return paymentRepo.deleteById(id);
	}

	public Mono<Void> deleteAllPayment() {
		return paymentRepo.deleteAll();
	}

	// ? ==================== MÉTODOS PRIVADOS ====================

	private Mono<Payment> savePayment(Payment payment, LocalDate startOfMonth, String user) {
		return courseRepo.findById(payment.getCourseId())
				.flatMap(course -> {
					payment.setCreatedAt(LocalDateTime.now());
					payment.setPaymentDueDate(startOfMonth);
					payment.setCreatedBy(user);
					payment.setPaymentAmount(course.getMonthlyPrice());
					payment.setPaidAmount(0.0);
					payment.setHasDebt(payment.getPaidAmount() < payment.getPaymentAmount());
					payment.setIsPaid(payment.getPaidAmount() >= payment.getPaymentAmount());
					payment.setLastPaymentDate(LocalDateTime.now());
					return paymentRepo.save(payment);
				}).switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
						"No se encontró el curso con ID: " + payment.getCourseId())));
	}

	private Mono<PaymentWithStudentDTO> mapPaymentToDTO(Payment payment) {
		Mono<Student> studentMono = studentRepo.findById(payment.getStudentId())
				.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
						"No se encontró el estudiante con ID: " + payment.getStudentId())));
		Mono<Course> courseMono = courseRepo.findById(payment.getCourseId())
				.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
						"No se encontró el curso con ID: " + payment.getCourseId())));

		return Mono.zip(studentMono, courseMono)
				.map(tuple -> {
					Student student = tuple.getT1();
					Course course = tuple.getT2();
					PaymentWithStudentDTO dto = new PaymentWithStudentDTO();
					mappingFromPaymentToPaymentWithStudentDTO(dto, payment, student, course);
					return dto;
				});
	}

	private PaymentWithStudentDTO mappingFromPaymentToPaymentWithStudentDTO(PaymentWithStudentDTO dto,
	                                                                        Payment payment,
	                                                                        Student student, Course course) {
		dto.setId(payment.getId());
		dto.setStudentId(payment.getStudentId());
		dto.setStudentName(student.getName() + " " + student.getSurname());
		dto.setCurseId(payment.getCourseId());
		dto.setCurseName(course.getTitle());
		dto.setPaymentAmount(payment.getPaymentAmount());
		dto.setPaidAmount(payment.getPaidAmount());
		dto.setPaymentType(payment.getPaymentType());
		dto.setHasDebt(payment.getHasDebt());
		dto.setIsPaid(payment.getIsPaid());
		dto.setPaymentDueDate(payment.getPaymentDueDate().atStartOfDay());
		dto.setLastPaymentDate(payment.getLastPaymentDate());
		dto.setCreatedBy(payment.getCreatedBy());
		dto.setModifiedBy(payment.getModifiedBy());
		dto.setCreatedAt(payment.getCreatedAt());
		dto.setUpdatedAt(payment.getUpdatedAt());
		return dto;
	}

	private Payment mappingPaymentToMontlyPayment(Course course, Student student, LocalDateTime startOfMonth,
	                                              String user) {
		Payment payment = new Payment();
		payment.setStudentId(student.getId());
		payment.setCourseId(course.getId());
		payment.setPaymentAmount(course.getMonthlyPrice());
		payment.setPaidAmount(0.0);
		payment.setHasDebt(true);
		payment.setIsPaid(false);
		payment.setPaymentDueDate(startOfMonth.toLocalDate());
		payment.setCreatedAt(LocalDateTime.now());
		payment.setCreatedBy(user);
		return payment;
	}

	private Payment mappingPaymentToDoPayment(Payment existingPayment, Payment payment, String user) {
		existingPayment.setPaidAmount(existingPayment.getPaidAmount() + payment.getPaidAmount());
		existingPayment.setHasDebt(existingPayment.getPaidAmount() < existingPayment.getPaymentAmount());
		existingPayment.setLastPaymentDate(LocalDateTime.now());
		existingPayment.setUpdatedAt(LocalDateTime.now());
		existingPayment.setModifiedBy(user);
		existingPayment.setPaymentType(payment.getPaymentType());
		existingPayment.setIsPaid(existingPayment.getPaidAmount() >= existingPayment.getPaymentAmount());
		return existingPayment;
	}

	private Flux<Payment> generatePaymentsForActiveStudents(List<Payment> existingPayments, LocalDate startOfMonth, String user) {
		return studentRepo.findAll()
				.filter(student -> "Activo".equalsIgnoreCase(student.getStatus()))
				.collectList()
				.flatMapMany(students -> generatePaymentsForActiveCourses(existingPayments, students, startOfMonth, user));
	}

	private Flux<Payment> generatePaymentsForActiveCourses(List<Payment> existingPayments, List<Student> students, LocalDate startOfMonth, String user) {
		return courseRepo.findAll()
				.filter(course -> course.getStatus() == CourseStatus.ACTIVE)
				.collectList()
				.flatMapMany(courses -> createAndSavePayments(existingPayments, students, courses, startOfMonth, user));
	}

	private Flux<Payment> createAndSavePayments(List<Payment> existingPayments, List<Student> students, List<Course> courses, LocalDate startOfMonth, String user) {
		return Flux.fromIterable(courses)
				.flatMap(course -> generatePaymentsForCourse(existingPayments, students, course, startOfMonth, user));
	}

	private Flux<Payment> generatePaymentsForCourse(List<Payment> existingPayments, List<Student> students, Course course, LocalDate startOfMonth, String user) {
		return Flux.fromIterable(students)
				.filter(student -> !hasExistingPayment(existingPayments, student, course) && isStudentEnrolledInCourse(student, course))
				.map(student -> createMonthlyPayment(course, student, startOfMonth, user))
				.flatMap(paymentRepo::save);
	}

	private boolean hasExistingPayment(List<Payment> existingPayments, Student student, Course course) {
		return existingPayments.stream()
				.anyMatch(payment -> payment.getStudentId().equals(student.getId()) && payment.getCourseId().equals(course.getId()));
	}

	private boolean isStudentEnrolledInCourse(Student student, Course course) {
		return student.getCursesIds() != null && !student.getCursesIds().isEmpty() && student.getCursesIds().contains(course.getId());
	}

	private Payment createMonthlyPayment(Course course, Student student, LocalDate startOfMonth, String user) {
		Payment newPayment = mappingPaymentToMontlyPayment(course, student, startOfMonth.atStartOfDay(), user);
		newPayment.setPaymentType(PaymentType.CUOTE);
		return newPayment;
	}

	private Mono<Payment> validateAndProcessPayment(Payment payment, String name) {
		return cashRegisterRepo.findFirstByIsClosedFalse()
				.hasElement()
				.flatMap(hasOpenRegister -> {
					if (!hasOpenRegister) {
						return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
								"No existe una caja abierta, para guardar un pago necesita abrir la caja primero."));
					}
					return processPayment(payment, name);
				});
	}

	private Mono<Payment> processPayment(Payment payment, String name) {
		return paymentRepo.findById(payment.getId())
				.flatMap(existingPayment -> validatePaymentAmount(existingPayment, payment))
				.flatMap(existingPayment -> paymentRepo.save(mappingPaymentToDoPayment(existingPayment, payment, name)));
	}

	private Mono<Payment> validatePaymentAmount(Payment existingPayment, Payment newPayment) {
		if (existingPayment.getPaymentAmount() < (existingPayment.getPaidAmount() + newPayment.getPaidAmount())) {
			return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"El pago a realizar excederá el saldo total."));
		}
		return Mono.just(existingPayment);
	}

	private <T> Mono<T> monoError(HttpStatus status, String message) {
		return Mono.error(new ResponseStatusException(status, message));
	}
}