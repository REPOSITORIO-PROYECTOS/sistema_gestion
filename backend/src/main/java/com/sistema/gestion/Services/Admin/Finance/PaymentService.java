package com.sistema.gestion.Services.Admin.Finance;

import static com.sistema.gestion.Utils.ErrorUtils.monoError;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

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
import com.sistema.gestion.Repositories.Profiles.StudentRepository;
import com.sistema.gestion.Utils.CourseStatus;
import com.sistema.gestion.Utils.PaymentType;

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
                  HttpStatus.NOT_FOUND, "No se encontró el estudiante con el ID: " + payment.getStudentId())));
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
                  "No se encontró el estudiante con el ID: " + payment.getStudentId())));
          Mono<Course> courseMono = courseRepo.findById(payment.getCourseId())
              .switchIfEmpty(Mono.error(new ResponseStatusException(
                  HttpStatus.NOT_FOUND,
                  "No se encontró el curso con ID: " + payment.getCourseId())));
          return Mono.zip(studentMono, courseMono)
              .map(tuple -> {
                Student student = tuple.getT1();
                Course course = tuple.getT2();
                PaymentWithStudentDTO dto = new PaymentWithStudentDTO();
                mappingFromPaymentToPaymentWithStudentDTO(dto, payment, student, course);
                return dto;
              });
        });
  }

  public Flux<PaymentWithStudentDTO> getAllPaymentsByStudentId(String studentId) {
    return paymentRepo.findAllByStudentId(studentId)
        .flatMap(payment -> {
          Mono<Student> studentMono = studentRepo.findById(payment.getStudentId())
              .switchIfEmpty(Mono
                  .error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                      "No se encontró el estudiante con el ID: " + studentId)));
          Mono<Course> courseMono = courseRepo.findById(payment.getCourseId())
              .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                  "No se encontró el curso con el ID: " + payment.getCourseId())));
          return Mono.zip(studentMono, courseMono)
              .map(tuple -> {
                Student student = tuple.getT1();
                Course course = tuple.getT2();
                PaymentWithStudentDTO dto = new PaymentWithStudentDTO();
                mappingFromPaymentToPaymentWithStudentDTO(dto, payment, student, course);
                return dto;
              });
        });
  }

  public Mono<Payment> registerPayment(Payment payment, String user) {
    YearMonth currentMonth = YearMonth.now();
    LocalDate startOfMonth = currentMonth.atDay(1);
    LocalDate endOfMonth = currentMonth.atEndOfMonth();
    if (payment.getStudentId() == null || payment.getStudentId().isEmpty()) {
      return monoError(HttpStatus.BAD_REQUEST, "El ID del estudiante esta vacio o nulo");
    }

    if (payment.getId() != null && !payment.getId().isEmpty()) {
      return monoError(HttpStatus.BAD_REQUEST, "El pago ya tiene un ID resgistrado,"
          + " no se puede almacenar un nuevo pago con ID ya registrado. Modifique o realice un pago parcial en el ya existente");
    }

    if (payment.getPaymentType() == PaymentType.CUOTE) {
      return paymentRepo.findByPaymentDueDateBetween(startOfMonth, endOfMonth)
          .filter(existingPayment -> existingPayment.getPaymentType() == PaymentType.CUOTE &&
              existingPayment.getStudentId().equals(payment.getStudentId()) &&
              existingPayment.getCourseId().equals(payment.getCourseId()))
          .hasElements()
          .flatMap(doesExist -> {
            if (doesExist) {
              return monoError(HttpStatus.CONFLICT,
                  "Ya existe una cuota registrada para este estudiante y curso en el mes actual.");
            }
            return savePayment(payment, startOfMonth, user);
          });
    }
    return savePayment(payment, startOfMonth, user);
  }

  public Flux<Payment> registerMonthlyPayments(String user) {
    YearMonth currentMonth = YearMonth.now();
    LocalDate startOfMonth = currentMonth.atDay(1);
    LocalDate endOfMonth = currentMonth.atEndOfMonth();

    return paymentRepo.findByPaymentDueDateBetween(startOfMonth, endOfMonth)
        .filter(payment -> payment.getPaymentType() == PaymentType.CUOTE)
        .collectList()
        .flatMapMany(existingPayments -> {
          return studentRepo.findAll()
              .filter(student -> "Activo".equalsIgnoreCase(student.getStatus()))
              .collectList()
              .flatMapMany(students -> {
                return courseRepo.findAll()
                    .filter(course -> course.getStatus() == CourseStatus.ACTIVE)
                    .collectList()
                    .flatMapMany(courses -> {
                      List<Payment> paymentsToSave = new ArrayList<>();

                      for (Course course : courses) {
                        for (Student student : students) {
                          boolean paymentExists = existingPayments.stream()
                              .anyMatch(payment -> payment.getStudentId().equals(student.getId()) &&
                                  payment.getCourseId().equals(course.getId()));

                          if (!paymentExists && student.getCoursesIds().contains(course.getId())) {
                            Payment newPayment = mappingPaymentToMontlyPayment(
                                course, student, startOfMonth.atStartOfDay(), user);
                            newPayment.setPaymentType(PaymentType.CUOTE);
                            paymentsToSave.add(newPayment);
                          }
                        }
                      }
                      return Flux.fromIterable(paymentsToSave)
                          .flatMap(paymentRepo::save);
                    });
              });
        });
  }

  public Mono<Payment> doPayment(String paymentId, Payment payment, String user) {
    if (!payment.getId().equals(paymentId)) {
      return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Los IDs del Pago a efectuar " +
              "en la base de datos con el del cuerpo de la solicitud no coinciden." +
              "ID solicitud: " + payment.getId() + "\nID base de datos: " + paymentId));
    }
    return cashRegisterRepo.findFirstByIsClosedFalse()
        .hasElement() // Verifica si hay elementos
        .flatMap(hasOpenRegister -> {
          if (hasOpenRegister) {
            return paymentRepo.findById(payment.getId())
                .flatMap(existingPayment -> {
                  if (existingPayment
                      .getPaymentAmount() < (existingPayment.getPaidAmount() + payment.getPaidAmount())) {
                    return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "El pago a realizar exedera el saldo total."));
                  }
                  return paymentRepo.save(mappingPaymentToDoPayment(existingPayment, payment, user));
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

  /** Metodos Locales */
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

}
