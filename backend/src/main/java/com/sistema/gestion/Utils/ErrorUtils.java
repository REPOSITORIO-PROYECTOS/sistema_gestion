package com.sistema.gestion.Utils;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ErrorUtils {

    /**
     * Devuelve un Mono que representa un error con el estado y mensaje
     * proporcionados.
     *
     * @param status  el estado HTTP del error
     * @param message el mensaje de error
     * @param <T>     el tipo genérico del Mono
     * @return un Mono con un error
     */
    public static <T> Mono<T> monoError(HttpStatus status, String message) {
        return Mono.error(new ResponseStatusException(status, message));
    }

    /**
     * Devuelve un Flux que representa un error con el estado y mensaje
     * proporcionados.
     *
     * @param status  el estado HTTP del error
     * @param message el mensaje de error
     * @param <T>     el tipo genérico del Flux
     * @return un Flux con un error
     */
    public static <T> Flux<T> fluxError(HttpStatus status, String message) {
        return Flux.error(new ResponseStatusException(status, message));
    }
}
