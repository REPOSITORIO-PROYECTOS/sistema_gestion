package com.sistema.gestion.Services.Admin.Management.Academics;

import com.sistema.gestion.Models.Admin.Management.Grade;
import com.sistema.gestion.Repositories.Admin.Management.GradeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class GradeService {

    private final GradeRepository gradeRepository;

    public GradeService(GradeRepository gradeRepository) {
        this.gradeRepository = gradeRepository;
    }

    public Mono<Grade> createGrade(Grade grade) {
        return gradeRepository.save(grade);
    }

    public Mono<Grade> getGradeById(String id) {
        return gradeRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Nota no encontrada con ID: " + id)));
    }

    public Flux<Grade> getGradesByStudentAndCourse(String studentId, String courseId) {
        return gradeRepository.findAllByStudentIdAndCourseId(studentId, courseId)
                .switchIfEmpty(Flux.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No se encontraron notas para el estudiante y curso especificado")));
    }

    public Flux<Grade> getGradesByStudentId(String studentId) {
        return gradeRepository.findAllByStudentId(studentId)
                .switchIfEmpty(Flux.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No se encontraron notas para el estudiante con ID: " + studentId)))
                .onErrorMap(e -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Error al obtener las notas del estudiante", e));
    }

    public Mono<Grade> updateGrade(String id, Grade updatedGrade) {
        return gradeRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Nota no encontrada con ID: " + id)))
                .flatMap(existingGrade -> {
                    existingGrade.setStudentId(updatedGrade.getStudentId());
                    existingGrade.setCourseId(updatedGrade.getCourseId());
                    existingGrade.setGrade(updatedGrade.getGrade());
                    existingGrade.setEvaluationDate(updatedGrade.getEvaluationDate());
                    existingGrade.setComments(updatedGrade.getComments());
                    return gradeRepository.save(existingGrade);
                });
    }

    public Mono<Void> deleteGrade(String id) {
        return gradeRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Nota no encontrada con ID: " + id)))
                .flatMap(existingGrade -> gradeRepository.deleteById(id));
    }
}
