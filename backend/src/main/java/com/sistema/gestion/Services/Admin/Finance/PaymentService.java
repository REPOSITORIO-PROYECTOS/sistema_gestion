package com.sistema.gestion.Services.Admin.Finance;

import static com.sistema.gestion.Utils.ErrorUtils.monoError;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.sistema.gestion.DTO.PaymentWithStudentDTO;
import com.sistema.gestion.Models.Admin.Finance.Payment;
import com.sistema.gestion.Models.Admin.Management.Course;
import com.sistema.gestion.Models.Profiles.Student;
import com.sistema.gestion.Repositories.Admin.Finance.CashRegisterRepository;
import com.sistema.gestion.Repositories.Admin.Finance.PaymentRepository;
import com.sistema.gestion.Repositories.Admin.Management.CourseRepository;
import com.sistema.gestion.Repositories.Admin.Management.StudentRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PaymentService {
        @Autowired
        private final PaymentRepository paymentRepo;

        @Autowired
        private final StudentRepository studentRepo;

        @Autowired
        private final CourseRepository courseRepo;

        @Autowired
        private final CashRegisterRepository cashRegisterRepo;

        public PaymentService(PaymentRepository paymentRepo, StudentRepository studentRepo, CourseRepository courseRepo,
                        CashRegisterRepository cashRegisterRepo) {
                this.paymentRepo = paymentRepo;
                this.studentRepo = studentRepo;
                this.courseRepo = courseRepo;
                this.cashRegisterRepo = cashRegisterRepo;
        }

        public Flux<Payment> getAllPayments() {
                return paymentRepo.findAll();
        }

        public Flux<PaymentWithStudentDTO> getAllPaymentsDetails() {
                return paymentRepo.findAll()
                                .flatMap(payment -> {
                                        Mono<Student> studentMono = studentRepo.findById(payment.getStudentId());
                                        Mono<Course> courseMono = courseRepo.findById(payment.getCourseId());
                                        return Mono.zip(studentMono, courseMono)
                                                        .map(tuple -> {
                                                                Student student = tuple.getT1();
                                                                Course course = tuple.getT2();
                                                                PaymentWithStudentDTO dto = new PaymentWithStudentDTO();
                                                                mappingFromPaymentToPaymentWithStudentDTO(dto, payment,
                                                                                student, course);
                                                                return dto;
                                                        });
                                });
        }

        public Mono<PaymentWithStudentDTO> getPaymentWithDetailsById(String paymentId) {
                return paymentRepo.findById(paymentId)
                                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "No se encontró el pago con el ID: " + paymentId)))
                                .flatMap(payment -> {
                                        Mono<Student> studentMono = studentRepo.findById(payment.getStudentId())
                                                        .switchIfEmpty(Mono.error(new ResponseStatusException(
                                                                        HttpStatus.NOT_FOUND,
                                                                        "No se encontró el estudiante con el ID: "
                                                                                        + payment.getStudentId())));
                                        Mono<Course> courseMono = courseRepo.findById(payment.getCourseId())
                                                        .switchIfEmpty(Mono.error(new ResponseStatusException(
                                                                        HttpStatus.NOT_FOUND,
                                                                        "No se encontró el curso con el ID: "
                                                                                        + payment.getCourseId())));
                                        return Mono.zip(studentMono, courseMono)
                                                        .map(tuple -> {
                                                                Student student = tuple.getT1();
                                                                Course course = tuple.getT2();
                                                                PaymentWithStudentDTO dto = new PaymentWithStudentDTO();
                                                                mappingFromPaymentToPaymentWithStudentDTO(dto, payment,
                                                                                student, course);
                                                                return dto;
                                                        });
                                });
        }

        public Flux<PaymentWithStudentDTO> getPaymentsHasDebt(Boolean hasDebt) {
                return paymentRepo.findAllByHasDebt(hasDebt)
                                .flatMap(payment -> {
                                        Mono<Student> studentMono = studentRepo.findById(payment.getStudentId())
                                                        .switchIfEmpty(Mono.error(new ResponseStatusException(
                                                                        HttpStatus.NOT_FOUND,
                                                                        "No se encontró el estudiante con el ID: "
                                                                                        + payment.getStudentId())));
                                        Mono<Course> courseMono = courseRepo.findById(payment.getCourseId())
                                                        .switchIfEmpty(Mono.error(new ResponseStatusException(
                                                                        HttpStatus.NOT_FOUND,
                                                                        "No se encontró el curso con ID: "
                                                                                        + payment.getCourseId())));
                                        return Mono.zip(studentMono, courseMono)
                                                        .map(tuple -> {
                                                                Student student = tuple.getT1();
                                                                Course course = tuple.getT2();

                                                                PaymentWithStudentDTO dto = new PaymentWithStudentDTO();
                                                                mappingFromPaymentToPaymentWithStudentDTO(dto, payment,
                                                                                student, course);
                                                                return dto;
                                                        });
                                });
        }

        public Flux<PaymentWithStudentDTO> getPaymentsHasDebtByMonth(Integer year, Integer month) {
                LocalDate startOfMonth = YearMonth.of(year, month).atDay(1);
                LocalDate endOfMonth = startOfMonth.plusMonths(1);

                return paymentRepo.findAllWithDebtInMonth(startOfMonth, endOfMonth)
                                .flatMap(payment -> {
                                        Mono<Student> studentMono = studentRepo.findById(payment.getStudentId())
                                                        .switchIfEmpty(Mono.error(new ResponseStatusException(
                                                                        HttpStatus.NOT_FOUND,
                                                                        "No se encontró el estudiante con el ID: "
                                                                                        + payment.getStudentId())));
                                        Mono<Course> courseMono = courseRepo.findById(payment.getCourseId())
                                                        .switchIfEmpty(Mono.error(new ResponseStatusException(
                                                                        HttpStatus.NOT_FOUND,
                                                                        "No se encontró el curso con ID: "
                                                                                        + payment.getCourseId())));
                                        return Mono.zip(studentMono, courseMono)
                                                        .map(tuple -> {
                                                                Student student = tuple.getT1();
                                                                Course course = tuple.getT2();
                                                                PaymentWithStudentDTO dto = new PaymentWithStudentDTO();
                                                                mappingFromPaymentToPaymentWithStudentDTO(dto, payment,
                                                                                student, course);
                                                                return dto;
                                                        });
                                });
        }

        public Flux<PaymentWithStudentDTO> getAllPaymentsByStudentId(String studentId) {
                return paymentRepo.findAllByStudentId(studentId)
                                .flatMap(payment -> {
                                        Mono<Student> studentMono = studentRepo.findById(payment.getStudentId())
                                                        .switchIfEmpty(Mono.error(new ResponseStatusException(
                                                                        HttpStatus.NOT_FOUND,
                                                                        "No se encontró el estudiante con el ID: "
                                                                                        + studentId)));
                                        Mono<Course> courseMono = courseRepo.findById(payment.getCourseId())
                                                        .switchIfEmpty(Mono.error(new ResponseStatusException(
                                                                        HttpStatus.NOT_FOUND,
                                                                        "No se encontró el curso con el ID: "
                                                                                        + payment.getCourseId())));
                                        return Mono.zip(studentMono, courseMono)
                                                        .map(tuple -> {
                                                                Student student = tuple.getT1();
                                                                Course course = tuple.getT2();
                                                                PaymentWithStudentDTO dto = new PaymentWithStudentDTO();
                                                                mappingFromPaymentToPaymentWithStudentDTO(dto, payment,
                                                                                student, course);
                                                                return dto;
                                                        });
                                });
        }

        public Mono<Payment> registerPayment(Payment payment, String user) {
                if (payment.getStudentId() == null || payment.getStudentId().isEmpty()) {
                        return monoError(HttpStatus.BAD_REQUEST, "El ID del estudiante esta vacio o nulo");
                }

                if (payment.getId() != null && !payment.getId().isEmpty()) {
                        return monoError(HttpStatus.BAD_REQUEST, "El pago ya tiene un ID resgistrado,"
                                        + " no se puede almacenar un nuevo pago con ID ya registrado. Modifique o realice un pago parcial en el ya existente");
                }

                payment.setCreatedAt(LocalDateTime.now());
                payment.setCreatedBy(user);
                payment.setHasDebt(payment.getPaidAmount() < payment.getPaymentAmount());
                payment.setIsPaid(payment.getPaidAmount() >= payment.getPaymentAmount());
                payment.setLastPaymentDate(LocalDateTime.now());
                return paymentRepo.save(payment);
        }

        public Mono<Payment> doPayment(String paymentId, Payment payment, String user) {
                if (!payment.getId().equals(paymentId)) {
                        return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                        "Los IDs del Pago a efectuar " +
                                                        "en la base de datos con el del cuerpo de la solicitud no coinciden."
                                                        +
                                                        "ID solicitud: " + payment.getId() +
                                                        "\nID base de datos: " + paymentId));
                }
                return cashRegisterRepo.findFirstByIsClosedFalse()
                                .hasElement() // Verifica si hay elementos
                                .flatMap(hasOpenRegister -> {
                                        if (hasOpenRegister) {
                                                return paymentRepo.findById(payment.getId())
                                                                .flatMap(existingPayment -> {
                                                                        if (existingPayment
                                                                                        .getPaymentAmount() < (existingPayment
                                                                                                        .getPaidAmount()
                                                                                                        + payment.getPaidAmount())) {
                                                                                return Mono.error(
                                                                                                new ResponseStatusException(
                                                                                                                HttpStatus.BAD_REQUEST,
                                                                                                                "El pago a realizar exedera el saldo total."));
                                                                        }
                                                                        existingPayment
                                                                                        .setPaidAmount(existingPayment
                                                                                                        .getPaidAmount()
                                                                                                        + payment.getPaidAmount());
                                                                        existingPayment.setHasDebt(
                                                                                        existingPayment.getPaidAmount() < existingPayment
                                                                                                        .getPaymentAmount());
                                                                        existingPayment.setLastPaymentDate(
                                                                                        LocalDateTime.now());
                                                                        existingPayment.setUpdatedAt(
                                                                                        LocalDateTime.now());
                                                                        existingPayment.setModifiedBy(user);

                                                                        existingPayment.setIsPaid(
                                                                                        existingPayment.getPaidAmount() >= existingPayment
                                                                                                        .getPaymentAmount());
                                                                        return paymentRepo.save(existingPayment);
                                                                });
                                        }
                                        return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                                        "No existe una caja abierta, para guarar un pago necesita abrir la caja primero."));
                                });
        }

        public Mono<Payment> updatePaymentInfo(String paymentId, Payment payment, String user) {
                if (!payment.getId().equals(paymentId)) {
                        return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                        "Los IDs del Pago a actualizar " +
                                                        "en la base de datos con el del cuerpo de la solicitud no coinciden."
                                                        +
                                                        "ID solicitud: " + payment.getId() +
                                                        "\nID base de datos: " + paymentId));
                }
                return paymentRepo.findById(paymentId)
                                .flatMap(existingPayment -> {
                                        existingPayment.setUpdatedAt(LocalDateTime.now());
                                        existingPayment.setModifiedBy(user);
                                        existingPayment.setPaymentAmount(payment.getPaymentAmount());
                                        return paymentRepo.save(existingPayment);
                                });
        }

        public Mono<Long> countPaymentHasDebt() {
                return paymentRepo.countAllByHasDebt(true);
        }

        /** Metodos Locales */
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
}
