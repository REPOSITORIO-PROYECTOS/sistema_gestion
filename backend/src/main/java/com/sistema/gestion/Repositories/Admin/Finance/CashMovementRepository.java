package com.sistema.gestion.Repositories.Admin.Finance;

import com.sistema.gestion.Models.Admin.Finance.CashMovement;

import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CashMovementRepository extends ReactiveMongoRepository<CashMovement, String> {
    Flux<CashMovement> findByTitleContainingIgnoreCaseAndDateBetween(
        String keyword, LocalDateTime from, LocalDateTime to);
}
