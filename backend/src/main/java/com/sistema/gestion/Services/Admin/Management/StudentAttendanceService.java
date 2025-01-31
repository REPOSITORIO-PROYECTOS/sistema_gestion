package com.sistema.gestion.Services.Admin.Management;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.sistema.gestion.Models.Admin.Management.StudentAttendance;
import com.sistema.gestion.Repositories.Admin.Management.CourseRepository;
import com.sistema.gestion.Repositories.Admin.Management.StudentAttendanceRepository;
import com.sistema.gestion.Repositories.Profiles.StudentRepository;
import com.sistema.gestion.Services.Profiles.UserService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

@Service
public class StudentAttendanceService {

  @Autowired
  private final StudentAttendanceRepository studentAttendanceRepository;

  @Autowired
  private final CourseRepository courseRepo;

  @Autowired
  private final StudentRepository studentRepo;

  @Autowired
  private final UserService userService;

  public StudentAttendanceService(StudentAttendanceRepository studentAttendanceRepository,
      CourseRepository courseRepo, StudentRepository studentRepo,
      UserService userService) {
    this.studentAttendanceRepository = studentAttendanceRepository;
    this.courseRepo = courseRepo;
    this.studentRepo = studentRepo;
    this.userService = userService;
  }

  public Flux<StudentAttendance> findByMonth(Integer month, Integer year) {
    Tuple2<LocalDate, LocalDate> dateRange;
    try {
      dateRange = validateAndGetMonthRange(month, year);
    } catch (ResponseStatusException e) {
      return Flux.error(e);
    }
    return studentAttendanceRepository.findByMonth(dateRange.getT1(), dateRange.getT2());
  }

  public Flux<StudentAttendance> findByMonthAndCourseId(Integer month, Integer year, String courseId) {
    if (courseId == null || courseId.isEmpty()) {
      return Flux.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "El curso no puede ser vacío."));
    }
    Tuple2<LocalDate, LocalDate> dateRange;
    try {
      dateRange = validateAndGetMonthRange(month, year);
    } catch (ResponseStatusException e) {
      return Flux.error(e);
    }
    return courseRepo.findById(courseId)
        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "El curso no existe.")))
        .flatMapMany(course -> studentAttendanceRepository.findByMonthAndCourseId(dateRange.getT1(), dateRange.getT2(),
            courseId));
  }

  public Mono<StudentAttendance> takeAttendance(StudentAttendance studentAttendance, String user) {
    return userService.getFullName(user)
        .flatMap(name -> {
          studentAttendance.setCreatedBy(name);
          studentAttendance.setCreatedAt(LocalDateTime.now());
          return courseRepo.findById(studentAttendance.getCourseId())
              .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                  "No se encontró el curso con ID: " + studentAttendance.getCourseId())))
              .flatMap(course -> {
                return validateStudents(studentAttendance.getStudentsIds())
                    .then(studentAttendanceRepository.save(studentAttendance)); // Guardar asistencia
              });
        });
  }

  public Mono<StudentAttendance> updateAttendance(StudentAttendance studentAttendance, String user) {
    return userService.getFullName(user)
        .flatMap(name -> {
          studentAttendance.setModifiedBy(name);
          studentAttendance.setUpdatedAt(LocalDateTime.now());
          return studentAttendanceRepository.findById(studentAttendance.getId())
              .flatMap(existingAttendance -> {
                existingAttendance.setStudentsIds(studentAttendance.getStudentsIds());
                existingAttendance.setAttendanceStatus(studentAttendance.getAttendanceStatus());
                return studentAttendanceRepository.save(existingAttendance);
              });
        });
  }

  // Metodos Auxiliares locales:
  private Mono<Void> validateStudents(Set<String> studentIds) {
    return Flux.fromIterable(studentIds)
        .flatMap(studentId -> studentRepo.findById(studentId) // Verificar cada estudiante en la base de datos
            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "El estudiante con ID: " + studentId + " no existe"))))
        .then();
  }

  private Tuple2<LocalDate, LocalDate> validateAndGetMonthRange(Integer month, Integer year) {
    if (month == null || year == null || month < 1 || month > 12 || year < 1900) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mes o año ingresado inválido.");
    }
    LocalDate startMonth = YearMonth.of(year, month).atDay(1);
    LocalDate endMonth = YearMonth.of(year, month).atEndOfMonth();
    if (startMonth.isAfter(LocalDate.now())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El mes ingresado no puede ser futuro.");
    }
    return Tuples.of(startMonth, endMonth);
  }

}
