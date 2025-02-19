package com.sistema.gestion.Repositories.Admin.Finance;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.sistema.gestion.Models.Admin.Finance.Provider;

import reactor.core.publisher.Flux;

@Repository
public interface ProviderRepository extends ReactiveMongoRepository<Provider, String> {
	@Query("{}")
	Flux<Provider> findProvidersPaged(PageRequest pageRequest);
}