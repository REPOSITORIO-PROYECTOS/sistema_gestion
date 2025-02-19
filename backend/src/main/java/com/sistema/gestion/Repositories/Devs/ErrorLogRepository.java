package com.sistema.gestion.Repositories.Devs;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.sistema.gestion.Models.Devs.ErrorLog;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Repository
public interface ErrorLogRepository extends ReactiveMongoRepository<ErrorLog, String> {

	// Busqueda con paginación
	@Query("{}")
	Flux<ErrorLog> findAllBy(PageRequest pageRequest);

	// Conteo total de registros
	@Query(value = "{}", count = true)
	Mono<Long> countAll();

	// Busqueda por palabra clave o rango de fechas con paginación
	@Query("""
        {
            '$or': [
                { 'message': { '$regex': ?0, '$options': 'i' } },
                { 'exceptionType': { '$regex': ?0, '$options': 'i' } },
                { 'customMessage': { '$regex': ?0, '$options': 'i' } }
            ],
            'timestamp': { '$gte': ?1, '$lte': ?2 }
        }
    """)
	Flux<ErrorLog> findByKeywordOrDateRange(String keyword, LocalDateTime startDate, LocalDateTime endDate, PageRequest pageRequest);

	// Conteo de registros por palabra clave o rango de fechas
	@Query(value = """
        {
            '$or': [
                { 'message': { '$regex': ?0, '$options': 'i' } },
                { 'exceptionType': { '$regex': ?0, '$options': 'i' } },
                { 'customMessage': { '$regex': ?0, '$options': 'i' } }
            ],
            'timestamp': { '$gte': ?1, '$lte': ?2 }
        }
    """, count = true)
	Mono<Long> countByKeywordOrDateRange(String keyword, LocalDateTime startDate, LocalDateTime endDate);
}
