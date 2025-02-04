package com.sistema.gestion.Repositories.Admin.Finance;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.sistema.gestion.Models.Admin.Finance.CashRegister;

import reactor.core.publisher.Mono;

@Repository
public interface CashRegisterRepository extends ReactiveMongoRepository<CashRegister, String> {
    Mono<CashRegister> findFirstByIsClosedFalse();
}