package com.sistema.gestion.Repositories.Admin.Finance;

import java.time.LocalDate;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.sistema.gestion.Models.Admin.Finance.Payment;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface PaymentRepository extends ReactiveMongoRepository<Payment, String> {

  Flux<Payment> findAllByStudentId(String studentId);

  Flux<Payment> findAllByHasDebt(Boolean hasDebt);

  Mono<Long> countAllByHasDebt(Boolean hasDebt);

  @Query("{ 'hasDebt': true, 'paymentDueDate': { $gte: ?0, $lt: ?1 } }")
  Flux<Payment> findAllWithDebtInMonth(LocalDate startOfMonth, LocalDate endOfMonth);

}
