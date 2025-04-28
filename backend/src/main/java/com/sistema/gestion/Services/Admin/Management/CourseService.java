package com.sistema.gestion.Services.Admin.Management;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.sistema.gestion.Services.Profiles.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.google.api.client.util.PemReader.Section;
import com.sistema.gestion.DTO.AllContentCourseDTO;
import com.sistema.gestion.DTO.PagedResponse;
import com.sistema.gestion.DTO.SectionDTO;
import com.sistema.gestion.DTO.SubSectionDTO;
import com.sistema.gestion.Models.Admin.Management.Course;
import com.sistema.gestion.Models.Admin.Management.VirtualCampus.CourseSection;
import com.sistema.gestion.Models.Admin.Management.VirtualCampus.CourseSubSection;
import com.sistema.gestion.Models.Admin.Management.VirtualCampus.File;
import com.sistema.gestion.Models.Profiles.Teacher;
import com.sistema.gestion.Repositories.Admin.Management.CourseRepository;
import com.sistema.gestion.Repositories.Admin.Management.VirtualCampus.CourseSectionRepository;
import com.sistema.gestion.Repositories.Admin.Management.VirtualCampus.CourseSubSectionRepository;
import com.sistema.gestion.Repositories.Admin.Management.VirtualCampus.FileRepository;
import com.sistema.gestion.Repositories.Profiles.StudentRepository;
import com.sistema.gestion.Repositories.Profiles.TeacherRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CourseService {

	private final CourseRepository courseRepo;
	private final StudentRepository studentRepo;
	private final TeacherRepository teacherRepo;
	private final UserService userService;
	private final CourseSectionRepository sectionRepo;
	private final CourseSubSectionRepository subSectionRepo;
	private final FileRepository fileRepo;

	// ? ==================== MÉTODOS PÚBLICOS ====================

	public Mono<PagedResponse<Course>> getCoursesPaged(int page, int size, String keyword) {
		PageRequest pageRequest = PageRequest.of(page, size);

		if (keyword != null && !keyword.isEmpty()) {
			return getCoursesByKeyword(keyword, pageRequest);
		}

		return getAllCoursesPaged(pageRequest);
	}

	public Mono<Course> saveCourse(Course course, String username) {
		if (course.getId() != null && !course.getId().isEmpty()) {
			return monoError(HttpStatus.BAD_REQUEST,
					"El curso ya tiene un ID registrado, no se puede almacenar un nuevo curso con ID ya registrado.");
		}

		return userService.getFullName(username)
				.flatMap(fullName -> {
					course.setCreatedAt(LocalDateTime.now());
					course.setCreatedBy(fullName);
					return courseRepo.save(course)
							.flatMap(savedCourse -> updateTeacherCourses(savedCourse));
				});
	}

	public Mono<Course> updateCourse(Course course, String courseId, String username) {
		if (!course.getId().equals(courseId)) {
			return monoError(HttpStatus.BAD_REQUEST,
					"Los IDs del curso a actualizar no coinciden.");
		}

		return courseRepo.findById(courseId)
				.switchIfEmpty(monoError(HttpStatus.NOT_FOUND, "No se encontró el curso con ID: " + courseId))
				.flatMap(existingCourse -> userService.getFullName(username)
						.flatMap(fullName -> {
							Course updatedCourse = mappingCourseToUpdate(existingCourse, course, fullName);
							return courseRepo.save(updatedCourse);
						}));
	}

	public Mono<Course> registerStudentInCourse(String courseId, String studentId) {
		return courseRepo.findById(courseId)
				.switchIfEmpty(monoError(HttpStatus.NOT_FOUND, "No se encontró el curso con ID: " + courseId))
				.flatMap(existingCourse -> studentRepo.findById(studentId)
						.switchIfEmpty(monoError(HttpStatus.NOT_FOUND, "No se encontró el estudiante con ID: " + studentId))
						.flatMap(student -> {
							Set<String> enrolledStudents = existingCourse.getStudentsIds();
							if (enrolledStudents == null) {
								enrolledStudents = new HashSet<>();
								existingCourse.setStudentsIds(enrolledStudents);
							}
							enrolledStudents.add(studentId);

							Set<String> studentCourses = student.getCoursesIds();
							if (studentCourses == null) {
								studentCourses = new HashSet<>();
								student.setCoursesIds(studentCourses);
							}
							studentCourses.add(courseId);

							return studentRepo.save(student)
									.then(courseRepo.save(existingCourse));
						}));
	}


	public Mono<Course> removeStudentFromCourse(String courseId, String studentId) {
		return courseRepo.findById(courseId)
				.switchIfEmpty(monoError(HttpStatus.NOT_FOUND, "No se encontró el curso con ID: " + courseId))
				.flatMap(existingCourse -> {
					Set<String> enrolledStudents = existingCourse.getStudentsIds();
					if (enrolledStudents == null || !enrolledStudents.contains(studentId)) {
						return monoError(HttpStatus.NOT_FOUND, "El estudiante no está inscrito en el curso.");
					}

					enrolledStudents.remove(studentId);
					return studentRepo.findById(studentId)
							.flatMap(student -> {
								Set<String> studentCourses = student.getCoursesIds();
								if (studentCourses != null && studentCourses.contains(courseId)) {
									studentCourses.remove(courseId);
									return studentRepo.save(student);
								}
								return Mono.empty();
							})
							.then(courseRepo.save(existingCourse));
				});
	}

	public Mono<AllContentCourseDTO> getCourseContent(String courseId) {
    // Paso 1: Obtener el curso
    return courseRepo.findById(courseId)
            .switchIfEmpty(monoError(HttpStatus.NOT_FOUND, "No se encontró el curso con ID: " + courseId))
            .flatMap(course -> {
                // Paso 2: Obtener las secciones del curso
                return sectionRepo.findByCourseId(courseId)
                        .collectList() // Trae todas las secciones del curso
                        .flatMap(sections -> {
                            // Paso 3: Para cada sección, obtener las subsecciones
                            List<SectionDTO> sectionDTOs = new ArrayList<>();
                            for (CourseSection section : sections) {
                                SectionDTO sectionDTO = new SectionDTO();
                                sectionDTO.setId(section.getId());
                                sectionDTO.setTitle(section.getName());
                                sectionDTO.setDescription(section.getDescription());

                                // Paso 4: Obtener las subsecciones de cada sección
                                subSectionRepo.findBySectionId(section.getId())
                                        .collectList()
                                        .doOnNext(subSections -> {
                                            List<SubSectionDTO> subSectionDTOs = new ArrayList<>();
                                            for (CourseSubSection subSection : subSections) {
                                                SubSectionDTO subSectionDTO = new SubSectionDTO();
                                                subSectionDTO.setId(subSection.getId());
                                                subSectionDTO.setBody(subSection.getBody());

                                                // Paso 5: Obtener los archivos de cada subsección
                                                fileRepo.findBySubSectionId(subSection.getId())
                                                        .collectList()
                                                        .doOnNext(files -> {
                                                            List<File> fileDTOs = new ArrayList<>();
                                                            for (File file : files) {
                                                                fileDTOs.add(file);
                                                            }
                                                            subSectionDTO.setFiles(fileDTOs);
                                                        })
                                                        .subscribe();
                                                
                                                subSectionDTOs.add(subSectionDTO);
                                            }
                                            sectionDTO.setSubSections(subSectionDTOs);
                                        })
                                        .subscribe();
                                sectionDTOs.add(sectionDTO);
                            }

                            // Paso 6: Construir la respuesta final
                            AllContentCourseDTO allContentCourseDTO = new AllContentCourseDTO();
                            allContentCourseDTO.setSections(sectionDTOs);
                            return Mono.just(allContentCourseDTO);
                        });
            });
}


	public Mono<Void> deleteCourse(String courseId) {
		return courseRepo.findById(courseId)
				.switchIfEmpty(monoError(HttpStatus.NOT_FOUND, "No se encontró el curso con ID: " + courseId))
				.flatMap(courseRepo::delete);
	}

	// ? ==================== MÉTODOS PRIVADOS ====================

	private Mono<PagedResponse<Course>> getCoursesByKeyword(String keyword, PageRequest pageRequest) {
		Mono<Long> totalElementsMono = courseRepo.countByKeyword(keyword);
		Flux<Course> coursesFlux = courseRepo.findByKeywordPaged(keyword, pageRequest);

		return Mono.zip(totalElementsMono, coursesFlux.collectList())
				.map(tuple -> new PagedResponse<>(
						tuple.getT2(), // Lista de cursos filtrados
						tuple.getT1(), // Total de registros filtrados
						pageRequest.getPageNumber(),
						pageRequest.getPageSize()));
	}


	private Mono<PagedResponse<Course>> getAllCoursesPaged(PageRequest pageRequest) {
		Mono<Long> totalElementsMono = courseRepo.count();
		Flux<Course> coursesFlux = courseRepo.findCoursesPaged(pageRequest);

		return Mono.zip(totalElementsMono, coursesFlux.collectList())
				.map(tuple -> new PagedResponse<>(
						tuple.getT2(), // Lista de cursos
						tuple.getT1(), // Total de registros
						pageRequest.getPageNumber(),
						pageRequest.getPageSize()));
	}

	private Mono<Course> updateTeacherCourses(Course course) {
    if (course.getTeacherIds() == null || course.getTeacherIds().isEmpty()) {
        return Mono.just(course); // Si no hay profesor, devolver el curso sin cambios
    }

    return teacherRepo.findAllById(course.getTeacherIds())
            .collectList() // Convierte el Flux en una lista
            .flatMap(teachers -> {
                if (teachers.isEmpty()) {
                    return monoError(HttpStatus.NOT_FOUND, "Profesor no encontrado: " + course.getTeacherIds());
                }

                // Actualizar los cursos de los profesores
                List<Mono<Teacher>> saveMonos = teachers.stream()
                        .map(teacher -> {
                            Set<String> courses = teacher.getCoursesIds();
                            if (courses == null) {
                                courses = new HashSet<>();
                                teacher.setCoursesIds(courses);
                            }
                            courses.add(course.getId());
                            return teacherRepo.save(teacher);
                        })
                        .collect(Collectors.toList());

                // Espera a que todos los profesores se guarden y luego retorna el curso
                return Mono.when(saveMonos)
                        .thenReturn(course); // Retornar el curso guardado
            });
}


	private Course mappingCourseToUpdate(Course existingCourse, Course course, String fullName) {
		existingCourse.setUpdatedAt(LocalDateTime.now());
		existingCourse.setModifiedBy(fullName);
		existingCourse.setTitle(course.getTitle());
		existingCourse.setDescription(course.getDescription());
		existingCourse.setStatus(course.getStatus());
		existingCourse.setMonthlyPrice(course.getMonthlyPrice());
		existingCourse.setTeacherIds(course.getTeacherIds());
		return existingCourse;
	}

	private <T> Mono<T> monoError(HttpStatus status, String message) {
		return Mono.error(new ResponseStatusException(status, message));
	}
}