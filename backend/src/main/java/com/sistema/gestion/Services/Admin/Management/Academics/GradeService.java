package com.sistema.gestion.Services.Admin.Management.Academics;

import com.sistema.gestion.Config.DynamicDBConection.DynamicMongoConfig;
import com.sistema.gestion.Models.Admin.Management.Grade;

import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class GradeService {

    private final DynamicMongoConfig dynamicMongoConfig;

    public GradeService(DynamicMongoConfig dynamicMongoConfig) {
        this.dynamicMongoConfig = dynamicMongoConfig;
    }

    private Mono<ReactiveMongoTemplate> getTemplate(ServerWebExchange exchange) {
        return dynamicMongoConfig.getTemplateFromRequest(exchange);
    }

    public Mono<Grade> createGrade(ServerWebExchange exchange, Grade grade) {
        return getTemplate(exchange)
                .flatMap(template -> template.save(grade));
    }

    public Mono<Grade> getGradeById(ServerWebExchange exchange, String id) {
        return getTemplate(exchange)
                .flatMap(template -> template.findById(id, Grade.class));
    }

    public Flux<Grade> getGradesByStudentAndCourse(ServerWebExchange exchange, String studentId, String courseId) {
        return getTemplate(exchange)
                .flatMapMany(template -> {
                    Query query = new Query();
                    query.addCriteria(Criteria.where("studentId").is(studentId).and("courseId").is(courseId));
                    return template.find(query, Grade.class);
                });
    }

    public Flux<Grade> getGradesByInstitution(ServerWebExchange exchange, String institution) {
        return getTemplate(exchange)
                .flatMapMany(template -> {
                    Query query = new Query();
                    query.addCriteria(Criteria.where("institution").is(institution));
                    return template.find(query, Grade.class);
                });
    }

    public Mono<Grade> updateGrade(ServerWebExchange exchange, String id, Grade updatedGrade) {
        return getTemplate(exchange)
                .flatMap(template -> template.findById(id, Grade.class)
                        .flatMap(existingGrade -> {
                            existingGrade.setStudentId(updatedGrade.getStudentId());
                            existingGrade.setCourseId(updatedGrade.getCourseId());
                            existingGrade.setGrade(updatedGrade.getGrade());
                            existingGrade.setEvaluationDate(updatedGrade.getEvaluationDate());
                            existingGrade.setComments(updatedGrade.getComments());
                            return template.save(existingGrade);
                        }));
    }

    public Mono<Void> deleteGrade(ServerWebExchange exchange, String id) {
        return getTemplate(exchange)
                .flatMap(template -> template.remove(Query.query(Criteria.where("id").is(id)), Grade.class))
                .then();
    }
}
