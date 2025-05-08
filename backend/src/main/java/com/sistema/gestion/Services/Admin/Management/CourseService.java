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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper objectMapper; 

	// ? ==================== MÉTODOS PÚBLICOS ====================

	public Mono<PagedResponse<Course>> getCoursesPaged(int page, int size, String keyword) {
		PageRequest pageRequest = PageRequest.of(page, size);

		if (keyword != null && !keyword.isEmpty()) {
			return getCoursesByKeyword(keyword, pageRequest);
		}

		return getAllCoursesPaged(pageRequest);
	}

    public Flux<Course> getCoursesByProfessorId(String professorId) {
        return courseRepo.findByTeacherIdsContaining(professorId);
    }

	public Mono<Course> saveCourse(Course course, String username) {
		if (course.getId() != null && !course.getId().isEmpty()) {
			return monoError(HttpStatus.BAD_REQUEST,
					"El curso ya tiene un ID registrado, no se puede almacenar un nuevo curso con ID ya registrado.");
		}

		return userService.getFullName(username)
				.flatMap(fullName -> {
					course.setCreatedAt(LocalDateTime.now());
					course.setCreatedBy(fullName.getName() + " " + fullName.getSurname());
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
							Course updatedCourse = mappingCourseToUpdate(existingCourse, course, fullName.getName() + " " + fullName.getSurname());
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

        System.out.println("Iniciando getCourseContent para courseId: " + courseId); // Log inicial

        return courseRepo.findById(courseId)
                .doOnError(e -> {
                    System.err.println("Error al buscar curso con ID: " + courseId); // Log después de findById
                    e.printStackTrace(); // Imprime la traza del error
                })
                .switchIfEmpty(monoError(HttpStatus.NOT_FOUND, "No se encontró el curso con ID: " + courseId))
                .flatMap(course -> {
                    System.out.println("Curso encontrado: " + course.getId());
                    return sectionRepo.findByCourseId(courseId) // Flux<CourseSection>
                            .doOnError(e -> {
                                System.err.println("Error al buscar secciones para curso ID: " + courseId); // Log después de findByCourseId
                                e.printStackTrace();
                            })
                            .flatMap(section -> {
                                // System.out.println("Procesando sección ID: " + section.getId()); // Puede ser muy verboso
                                Mono<List<SubSectionDTO>> subSectionDTOsMono = subSectionRepo.findBySectionId(section.getId()) // Flux<CourseSubSection>
                                        .doOnError(e -> {
                                            System.err.println("Error al buscar subsecciones para sección ID: " + section.getId()); // Log después de findBySectionId
                                            e.printStackTrace();
                                        })
                                        .flatMap(subSection -> {
                                            // Log de inicio
                                            System.out.println("Procesando subsección ID: " + subSection.getId());
                                        
                                            // Obtener archivos por los IDs de los archivos en la subsección
                                            Mono<List<File>> filesMono = fileRepo.findByIdIn(subSection.getFilesIds()) // Flux<File>
                                                .doOnError(e -> {
                                                    // Log si ocurre un error al buscar los archivos
                                                    System.err.println("Error al buscar archivos para subsección ID: " + subSection.getId());
                                                    e.printStackTrace();
                                                })
                                                .collectList() // Convertir Flux<File> en Mono<List<File>>
                                                .doOnSuccess(files -> {
                                                    if (files.isEmpty()) {
                                                        // Log si no se encuentran archivos
                                                        System.out.println("No se encontraron archivos para subsección ID: " + subSection.getId());
                                                    }
                                                });
                                        
                                            // Crear el SubSectionDTO cuando los archivos estén listos
                                            return filesMono.map(files -> {
                                                SubSectionDTO subSectionDTO = new SubSectionDTO();
                                                subSectionDTO.setId(subSection.getId());
                                                subSectionDTO.setTitle(subSection.getTitle());
                                                subSectionDTO.setBody(subSection.getBody());
                                                subSectionDTO.setFiles(files); // Establecer los archivos obtenidos
                                                return subSectionDTO;
                                            })
                                            .doOnError(e -> {
                                                // Log de error cuando ocurre al mapear archivos a SubSectionDTO
                                                System.err.println("Error mapeando archivos a SubSectionDTO para subsección ID: " + subSection.getId());
                                                e.printStackTrace();
                                            });
                                        })
                                         // Flux<SubSectionDTO>
                                        .collectList() // Mono<List<SubSectionDTO>>
                                        .doOnSuccess(subs -> {
                                            // System.out.println("Subsecciones recolectadas (" + subs.size() + ") para sección ID: " + section.getId());
                                        }); // Log éxito collectList subsecciones


                                // Crear el SectionDTO cuando las subsecciones estén listas
                                return subSectionDTOsMono.map(subSectionDTOs -> {
                                    SectionDTO sectionDTO = new SectionDTO();
                                    sectionDTO.setId(section.getId());
                                    sectionDTO.setTitle(section.getName());
                                    sectionDTO.setTeacherId(section.getTeacherId());
                                    sectionDTO.setDescription(section.getDescription());
                                    sectionDTO.setSubSections(subSectionDTOs);
                                    // System.out.println("SectionDTO creado para ID: " + section.getId());
                                    return sectionDTO;
                                })
                                .doOnError(e -> {
                                     System.err.println("Error mapeando subsecciones a SectionDTO para sección ID: " + section.getId()); // Log en mapeo
                                     e.printStackTrace();
                                 }); // Mono<SectionDTO>
                            }) // Flux<SectionDTO>
                            .collectList() // Mono<List<SectionDTO>>
                            .doOnSuccess(secs -> System.out.println("Secciones recolectadas (" + secs.size() + ") para curso ID: " + courseId)) // Log éxito collectList secciones
                            .flatMap(sectionDTOs -> { // Usar flatMap para manejar posible error en construcción DTO
                                try {
                                    // --- Inicio: Verificación Opcional de Serialización ---
                                    AllContentCourseDTO tempDtoForCheck = new AllContentCourseDTO();
                                    tempDtoForCheck.setSections(sectionDTOs);
                                    try {
                                        String json = objectMapper.writeValueAsString(tempDtoForCheck);
                                        // Imprime solo una parte para no llenar la consola si es muy grande
                                        System.out.println("DTO construido y parece serializable para curso ID " + courseId + ": " + json.substring(0, Math.min(json.length(), 300)) + "...");
                                    } catch (JsonProcessingException jsonEx) {
                                        System.err.println("¡Error de SERIALIZACIÓN detectado para curso ID " + courseId + "!");
                                        jsonEx.printStackTrace();
                                        // Decide si quieres propagar este error específico
                                        // return Mono.error(jsonEx);
                                    }
                                    // --- Fin: Verificación Opcional de Serialización ---

                                    // Construcción final
                                    AllContentCourseDTO allContentCourseDTO = new AllContentCourseDTO();
                                    allContentCourseDTO.setSections(sectionDTOs);
                                    System.out.println("AllContentCourseDTO creado exitosamente para curso ID: " + courseId);
                                    return Mono.just(allContentCourseDTO); // Devolver el DTO final

                                } catch (Exception ex) {
                                    System.err.println("Error CRÍTICO al construir/verificar AllContentCourseDTO para curso ID: " + courseId);
                                    ex.printStackTrace();
                                    return Mono.error(ex); // Propagar el error si la construcción o verificación falla
                                }
                            })
                            .doOnError(e -> {
                                System.err.println("Error finalizando la construcción de AllContentCourseDTO para curso ID: " + courseId); // Log antes del final
                                e.printStackTrace(); // No necesitas imprimir aquí si ya lo hiciste en los .doOnError anteriores, pero no hace daño
                            });
                })
                .doFinally(signalType -> System.out.println("Finalizado getCourseContent para courseId: " + courseId + " con señal: " + signalType)); // Log al finalizar (éxito o error)
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