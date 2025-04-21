package com.sistema.gestion.Repositories.Admin.Finance;

import com.sistema.gestion.Models.Admin.Finance.CashMovement;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CashMovementRepository extends ReactiveMongoRepository<CashMovement, String> {
}
