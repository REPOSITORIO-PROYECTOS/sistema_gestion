package com.sistema.gestion.Repositories.Admin.Finance;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.sistema.gestion.Models.Admin.Finance.Invoice;

@Repository
public interface InvoiceRepository extends ReactiveMongoRepository<Invoice, String> {

}