package com.sistema.gestion.Utils;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ErrorUtils {

    public static <T> Mono<T> monoError(HttpStatus status, String message) {
        return Mono.error(new ResponseStatusException(status, message));
    }

    public static <T> Flux<T> fluxError(HttpStatus status, String message) {
        return Flux.error(new ResponseStatusException(status, message));
    }
}
