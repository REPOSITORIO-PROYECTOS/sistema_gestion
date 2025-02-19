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

	public Mono<PagedResponse<Teacher>> findAll(int page, int size, String keyword) {
		PageRequest pageRequest = PageRequest.of(page, size);

		if (keyword != null && !keyword.isEmpty()) {
			Mono<Long> totalElementsMono = teacherRepository.countByDniOrSurname(keyword);
			Flux<Teacher> teacherFlux = teacherRepository.findByDniOrSurname(keyword, pageRequest);

			return Mono.zip(totalElementsMono, teacherFlux.collectList())
					.map(tuple -> new PagedResponse<>(
							tuple.getT2(), // Lista de maestros filtrados
							tuple.getT1(), // Total de registros filtrados
							page,
							size));
		}

		Mono<Long> totalElementsMono = teacherRepository.countAll();
		Flux<Teacher> studentsFlux = teacherRepository.findAllBy(pageRequest);

		return Mono.zip(totalElementsMono, studentsFlux.collectList())
				.map(tuple -> new PagedResponse<>(
						tuple.getT2(), // Lista de maestros
						tuple.getT1(), // Total de registros
						page,
						size));
	}

	public Mono<Teacher> findById(String id) {
		return teacherRepository.findById(id)
				.switchIfEmpty(Mono.error(new ResponseStatusException(
						HttpStatus.NOT_FOUND, "No se encontró el profesor con ID: " + id)))
				.onErrorResume(e -> Mono.error(new ResponseStatusException(
						HttpStatus.INTERNAL_SERVER_ERROR, "Error al buscar el profesor", e)));
	}

	public Mono<Teacher> create(Teacher teacher, String user) {
		teacher.setCreatedBy(user);

		return teacherRepository.save(teacher)
				.flatMap(savedTeacher -> {
					Set<String> courseIds = savedTeacher.getCoursesIds();
					if (courseIds == null || courseIds.isEmpty()) {
						return Mono.just(savedTeacher); // Si no tiene cursos devolvemos el profesor sin cambios
					}

					return Flux.fromIterable(courseIds)
							.flatMap(courseId -> courseRepository.findById(courseId) // Buscamos el curso por ID
									.switchIfEmpty(
											Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso no encontrado: " + courseId)))
									.flatMap(course -> {
										course.setTeacherId(savedTeacher.getId());
										return courseRepository.save(course); // Guardamos el curso actualizado
									}))
							.then(Mono.just(savedTeacher)); // Devolvemos el profesor guardado
				})
				.onErrorResume(e -> Mono.error(new ResponseStatusException(
						HttpStatus.INTERNAL_SERVER_ERROR, "Error al crear el profesor", e)));
	}

	public Mono<Teacher> update(String id, Teacher teacher, String user) {
		return teacherRepository.findById(id)
				.switchIfEmpty(Mono.error(new ResponseStatusException(
						HttpStatus.NOT_FOUND, "No se encontró el profesor con ID: " + id)))
				.flatMap(existingTeacher -> {
					existingTeacher.setName(teacher.getName());
					existingTeacher.setSurname(teacher.getSurname());
					existingTeacher.setUpdatedAt(LocalDateTime.now());
					existingTeacher.setModifiedBy(user);
					existingTeacher.setEmail(teacher.getEmail());
					existingTeacher.setPhone(teacher.getPhone());
					return teacherRepository.save(existingTeacher);
				})
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
}
