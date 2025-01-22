package com.sistema.gestion.Services.Admin.Finance;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.sistema.gestion.Models.Admin.Finance.Provider;
import com.sistema.gestion.Repositories.Admin.Finance.ProviderRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProviderService {
  @Autowired
  private ProviderRepository providerRepo;

  public ProviderService(ProviderRepository providerRepo) {
    this.providerRepo = providerRepo;
  }

  public Flux<Provider> getAllProviders() {
    return providerRepo.findAll();
  }

  public Mono<Provider> getProviderById(String providerId) {
    return providerRepo.findById(providerId)
        .switchIfEmpty(Mono.error(
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Proveedor no encontrado con el ID: " + providerId)));
  }

  public Mono<Provider> saveProvider(Provider provider, String user) {
    if (provider.getId() != null && !provider.getId().isEmpty()) {
      return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "El proveedor ya tiene un ID registrado" +
          " No se puede almacenar un proveedor con Id ya registrado."));
    }
    provider.setCreatedAt(LocalDateTime.now());
    provider.setCreatedBy(user);
    return providerRepo.save(provider);
  }

  public Mono<Provider> updateProvider(Provider provider, String providerId, String user) {
    if (!provider.getId().equals(providerId)) {
      return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Los IDs del proveedor a actualizar " +
          "en la base de datos con el del cuerpo de la solicitud no coinciden."));
    }
    return providerRepo.findById(providerId)
        .flatMap(existingProvider -> {
          return providerRepo.save(mappingProviderToUpdate(existingProvider, provider, user));
        });
  }

  public Mono<Void> deleteProvider(String providerId) {
    return providerRepo.findById(providerId)
        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
            "No se encontró el proveedor para eliminar con ID: " + providerId)))
        .flatMap(providerRepo::delete);
  }

  /** Métodos locales */
  private Provider mappingProviderToUpdate(Provider existingProvider, Provider provider, String user) {
    existingProvider.setName(provider.getName());
    existingProvider.setAddress(provider.getAddress());
    existingProvider.setPhone(provider.getPhone());
    existingProvider.setCuilCuit(provider.getCuilCuit());
    existingProvider.setModifiedBy(user);
    existingProvider.setDescription(provider.getDescription());
    existingProvider.setUpdatedAt(LocalDateTime.now());
    existingProvider.setIsActive(provider.getIsActive());
    return existingProvider;
  }
}
