package com.sistema.gestion.Controllers.Admin.Management;

import static com.sistema.gestion.Utils.ErrorUtils.monoError;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.sistema.gestion.Models.Admin.Management.Course;
import com.sistema.gestion.Services.Admin.Management.CourseService;

import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Controlador REST para gestionar cursos.
 */
@RestController
@RequestMapping("/cursos")
public class CourseController {
    @Autowired
    private final CourseService courseService;

    /**
     * Constructor del controlador de cursos.
     * 
     * @param courseService Servicio para la gestión de cursos.
     */
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    /**
     * Obtiene todos los cursos.
     * 
     * @return Flux de objetos Course. Devuelve un Flux vacío si no hay cursos
     *         disponibles.
     */
    @GetMapping
    public Flux<Course> getAllCourses() {
        return courseService.findAllCourses();
    }

    /**
     * Obtiene un curso por su ID.
     * 
     * @param id ID del curso a buscar.
     * @return Mono de Course. Devuelve un error 404 si no se encuentra el curso.
     */
    @GetMapping("/{id}")
    public Mono<Course> getCourseById(@PathVariable String id) {
        return courseService.findCourseById(id)
                .switchIfEmpty(monoError(HttpStatus.NOT_FOUND, "Curso no encontrado con el ID: " + id));
    }

    /**
     * Obtiene un curso por palabra clave en titulo o descripcion.
     * 
     * @param keyword palabra clave del curso a buscar.
     * @return Flux de objetos Course. Devuelve un Flux vacío si no hay cursos con
     *         la palabra clave
     */
    @GetMapping("/buscar")
    public Flux<Course> searchCourses(@RequestParam String keyword) {
        return courseService.searchCourses(keyword);
    }

    /**
     * Crea un nuevo curso.
     * 
     * @param course Objeto Course con los datos del curso a crear.
     * @return Mono de Course con el curso creado. Devuelve un error 400 si hay
     *         problemas de validación.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Course> createCourse(@RequestBody @Valid Course course) {
        String user = "admin"; // Ejemplo, se debería obtener el usuario actual
        return courseService.saveCourse(course, user)
                .onErrorMap(e -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al crear el curso", e));
    }

    /**
     * Actualiza un curso existente.
     * 
     * @param id     ID del curso a actualizar.
     * @param course Objeto Course con los nuevos datos del curso.
     * @return Mono de Course con el curso actualizado. Devuelve un error 400 si hay
     *         problemas de validación.
     */
    @PutMapping("/{id}")
    public Mono<Course> updateCourse(@PathVariable String id, @RequestBody @Valid Course course) {
        String user = "admin"; // Ejemplo, se debería obtener el usuario actual
        return courseService.saveCourse(course, user)
                .onErrorMap(e -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al crear el curso", e));
    }

    /**
     * Elimina un curso por su ID.
     * 
     * @param id ID del curso.
     * @return Mono<Void> con HTTP 204 si la eliminación fue exitosa, o 404 si no se
     *         encontró el curso.
     */
    @DeleteMapping("/{id}")
    public Mono<Void> deleteCourse(@PathVariable String id) {
        return courseService.findCourseById(id)
                .switchIfEmpty(monoError(HttpStatus.NOT_FOUND, "No hay curso con el ID: " + id))
                .flatMap(course -> courseService.deleteCourse(id))
                .then();
    }

}
