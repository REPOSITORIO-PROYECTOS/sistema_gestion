package com.sistema.gestion.Services.Profiles;

import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.sistema.gestion.DTO.PagedResponse;
import com.sistema.gestion.Models.Profiles.Teacher;
import com.sistema.gestion.Repositories.Admin.Management.CourseRepository;
import com.sistema.gestion.Repositories.Profiles.TeacherRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TeacherService {

	private final TeacherRepository teacherRepository;
	private final CourseRepository courseRepository;
	private final UserService userService;

	// ? ==================== MÉTODOS PÚBLICOS ====================

	public Mono<PagedResponse<Teacher>> findAll(int page, int size, String keyword) {
		PageRequest pageRequest = PageRequest.of(page, size);

		if (keyword != null && !keyword.isEmpty()) {
			return searchTeachers(keyword, pageRequest);
		}

		return getAllTeachersPaged(pageRequest);
	}

	public Mono<Teacher> findById(String id) {
		return teacherRepository.findById(id)
				.switchIfEmpty(Mono.error(new ResponseStatusException(
						HttpStatus.NOT_FOUND, "No se encontró el profesor con ID: " + id)))
				.onErrorResume(e -> Mono.error(new ResponseStatusException(
						HttpStatus.INTERNAL_SERVER_ERROR, "Error al buscar el profesor", e)));
	}

	public Mono<Teacher> create(Teacher teacher, String username) {
		return userService.getFullName(username)
				.flatMap(fullName -> {
					teacher.setCreatedBy(fullName);
					teacher.setCreatedAt(LocalDateTime.now());
					return teacherRepository.save(teacher)
							.flatMap(savedTeacher -> assignTeacherToCourses(savedTeacher));
				})
				.onErrorResume(e -> Mono.error(new ResponseStatusException(
						HttpStatus.INTERNAL_SERVER_ERROR, "Error al crear el profesor", e)));
	}

	public Mono<Teacher> update(String id, Teacher teacher, String username) {
		return userService.getFullName(username)
				.flatMap(fullName -> teacherRepository.findById(id)
						.switchIfEmpty(Mono.error(new ResponseStatusException(
								HttpStatus.NOT_FOUND, "No se encontró el profesor con ID: " + id)))
						.flatMap(existingTeacher -> {
							existingTeacher.setName(teacher.getName());
							existingTeacher.setSurname(teacher.getSurname());
							existingTeacher.setEmail(teacher.getEmail());
							existingTeacher.setPhone(teacher.getPhone());
							existingTeacher.setUpdatedAt(LocalDateTime.now());
							existingTeacher.setModifiedBy(fullName);
							return teacherRepository.save(existingTeacher);
						}))
				.onErrorResume(e -> Mono.error(new ResponseStatusException(
						HttpStatus.INTERNAL_SERVER_ERROR, "Error al actualizar el profesor", e)));
	}

	public Mono<Void> delete(String id) {
		return teacherRepository.findById(id)
				.switchIfEmpty(Mono.error(new ResponseStatusException(
						HttpStatus.NOT_FOUND, "No se encontró el profesor con ID: " + id)))
				.flatMap(existingTeacher -> teacherRepository.deleteById(id))
				.onErrorResume(e -> Mono.error(new ResponseStatusException(
						HttpStatus.INTERNAL_SERVER_ERROR, "Error al eliminar el profesor", e)));
	}

	// ? ==================== MÉTODOS PRIVADOS ====================

	private Mono<PagedResponse<Teacher>> getAllTeachersPaged(PageRequest pageRequest) {
		Mono<Long> totalElementsMono = teacherRepository.countAll();
		Flux<Teacher> teachersFlux = teacherRepository.findAllBy(pageRequest);

		return Mono.zip(totalElementsMono, teachersFlux.collectList())
				.map(tuple -> new PagedResponse<>(
						tuple.getT2(), // Lista de profesores
						tuple.getT1(), // Total de registros
						pageRequest.getPageNumber(),
						pageRequest.getPageSize()));
	}

	private Mono<PagedResponse<Teacher>> searchTeachers(String query, PageRequest pageRequest) {
		Mono<Long> totalElementsMono = teacherRepository.countByDniOrSurname(query);
		Flux<Teacher> teachersFlux = teacherRepository.findByDniOrSurname(query, pageRequest);

		return Mono.zip(totalElementsMono, teachersFlux.collectList())
				.map(tuple -> new PagedResponse<>(
						tuple.getT2(), // Lista de profesores encontrados
						tuple.getT1(), // Total de registros encontrados
						pageRequest.getPageNumber(),
						pageRequest.getPageSize()));
	}


	private Mono<Teacher> assignTeacherToCourses(Teacher teacher) {
		Set<String> courseIds = teacher.getCoursesIds();

		if (courseIds == null || courseIds.isEmpty()) {
			return Mono.just(teacher); // Si no hay cursos, devolver el profesor sin cambios
		}

		return Flux.fromIterable(courseIds)
				.flatMap(courseId -> courseRepository.findById(courseId)
						.switchIfEmpty(Mono.error(new ResponseStatusException(
								HttpStatus.NOT_FOUND, "Curso no encontrado: " + courseId)))
						.flatMap(course -> {
							course.setTeacherId(teacher.getId()); // Asignar el profesor al curso
							return courseRepository.save(course); // Guardar el curso actualizado
						}))
				.then(Mono.just(teacher)); // Devolver el profesor guardado
	}
}