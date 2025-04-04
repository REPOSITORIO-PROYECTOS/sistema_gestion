package com.sistema.gestion.Services.Profiles;

import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.sistema.gestion.DTO.PagedResponse;
import com.sistema.gestion.Models.Profiles.Student;
import com.sistema.gestion.Repositories.Admin.Management.CourseRepository;
import com.sistema.gestion.Repositories.Profiles.StudentRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class StudentService {

	private final StudentRepository studentRepository;
	private final CourseRepository courseRepository;
	private final UserService userService;

	// ? ==================== MÉTODOS PÚBLICOS ====================

	public Mono<PagedResponse<Student>> findAll(int page, int size, String keyword) {
		PageRequest pageRequest = PageRequest.of(page, size);

		if (keyword != null && !keyword.isEmpty()) {
			return searchStudents(keyword, pageRequest);
		}

		return getAllStudentsPaged(pageRequest);
	}

	public Mono<Long> findAllCount() {
		return studentRepository.count();
	}

	public Mono<Student> createStudentWithCourses(Student student, String username) {
		return userService.getFullName(username)
				.flatMap(fullName -> {
					student.setCreatedBy(fullName);
					student.setCreatedAt(LocalDateTime.now());
					return studentRepository.save(student)
							.flatMap(savedStudent -> enrollStudentInCourses(savedStudent));
				})
				.onErrorMap(e -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
						"Error al inscribir al estudiante en los cursos", e));
	}

	public Mono<Student> findById(String id) {
		return studentRepository.findById(id)
				.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
						"No se encontró el estudiante con ID: " + id)))
				.onErrorMap(e -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
						"Error al buscar el estudiante", e));
	}

	public Mono<Student> updateStudent(String id, Student student, String username) {
		return userService.getFullName(username)
				.flatMap(fullName -> studentRepository.findById(id)
						.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
								"No se encontró el estudiante con ID: " + id)))
						.flatMap(existingStudent -> {
							existingStudent.setName(student.getName());
							existingStudent.setSurname(student.getSurname());
							existingStudent.setEmail(student.getEmail());
							existingStudent.setPhone(student.getPhone());
							existingStudent.setUpdatedAt(LocalDateTime.now());
							existingStudent.setModifiedBy(fullName);
							return studentRepository.save(existingStudent);
						}))
				.onErrorMap(e -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
						"Error al actualizar el estudiante", e));
	}

	public Mono<Void> deleteStudent(String id) {
		return studentRepository.findById(id)
				.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
						"No se encontró el estudiante con ID: " + id)))
				.flatMap(student -> studentRepository.deleteById(id))
				.onErrorMap(e -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
						"Error al eliminar el estudiante", e));
	}

	// ==================== MÉTODOS PRIVADOS ====================

	private Mono<PagedResponse<Student>> getAllStudentsPaged(PageRequest pageRequest) {
		Mono<Long> totalElementsMono = studentRepository.countAll();
		Flux<Student> studentsFlux = studentRepository.findAllBy(pageRequest);

		return Mono.zip(totalElementsMono, studentsFlux.collectList())
				.map(tuple -> new PagedResponse<>(
						tuple.getT2(), // Lista de estudiantes
						tuple.getT1(), // Total de registros
						pageRequest.getPageNumber(),
						pageRequest.getPageSize()));
	}

	private Mono<PagedResponse<Student>> searchStudents(String query, PageRequest pageRequest) {
		Mono<Long> totalElementsMono = studentRepository.countByDniOrSurname(query);
		Flux<Student> studentsFlux = studentRepository.findByDniOrSurname(query, pageRequest);

		return Mono.zip(totalElementsMono, studentsFlux.collectList())
				.map(tuple -> new PagedResponse<>(
						tuple.getT2(), // Lista de estudiantes encontrados
						tuple.getT1(), // Total de registros encontrados
						pageRequest.getPageNumber(),
						pageRequest.getPageSize()));
	}

	private Mono<Student> enrollStudentInCourses(Student student) {
		Set<String> courseIds = student.getCoursesIds();

		if (courseIds == null || courseIds.isEmpty()) {
			return Mono.just(student); // Si no hay cursos, devolver el estudiante sin cambios
		}

		return Flux.fromIterable(courseIds)
				.flatMap(courseId -> courseRepository.findById(courseId)
						.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
								"Curso no encontrado: " + courseId)))
						.flatMap(course -> {
							course.getStudentsIds().add(student.getId()); // Agregar el estudiante al curso
							return courseRepository.save(course); // Guardar el curso actualizado
						}))
				.then(Mono.just(student)); // Devolver el estudiante guardado
	}
}