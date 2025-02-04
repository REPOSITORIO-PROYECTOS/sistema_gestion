package com.sistema.gestion.Repositories.Admin.Finance;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.sistema.gestion.Models.Admin.Finance.Invoice;

import reactor.core.publisher.Flux;

@Repository
public interface InvoiceRepository extends ReactiveMongoRepository<Invoice, String> {
  @Query("{}")
  Flux<Invoice> findInvoicesPaged(PageRequest pageRequest);
}