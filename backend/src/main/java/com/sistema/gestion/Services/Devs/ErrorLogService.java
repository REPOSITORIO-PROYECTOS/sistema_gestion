package com.sistema.gestion.Services.Devs;

import com.sistema.gestion.DTO.PagedResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.sistema.gestion.Models.Devs.ErrorLog;
import com.sistema.gestion.Repositories.Devs.ErrorLogRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ErrorLogService {

	private final ErrorLogRepository errorLogRepo;

	// ? ==================== MÉTODOS PÚBLICOS ====================

	public Mono<PagedResponse<ErrorLog>> getErrorLogsPaged(int page, int size, String keyword, LocalDateTime startDate, LocalDateTime endDate) {
		PageRequest pageRequest = PageRequest.of(page, size);

		if (keyword != null && !keyword.isEmpty()) {
			return getErrorLogsByKeywordOrDateRange(keyword, startDate, endDate, pageRequest);
		}

		return getAllErrorLogsPaged(pageRequest);
	}

	private Mono<PagedResponse<ErrorLog>> getAllErrorLogsPaged(PageRequest pageRequest) {
		return errorLogRepo.findAllBy(pageRequest)
				.collectList()
				.zipWith(errorLogRepo.countAll())
				.map(tuple -> new PagedResponse<>(
						tuple.getT1(), // Lista de errores
						tuple.getT2(), // Total de registros
						pageRequest.getPageNumber(),
						pageRequest.getPageSize()
				));
	}

	public Mono<ErrorLog> getErrorLogById(String id) {
		return errorLogRepo.findById(id)
				.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Error log not found")));
	}

	public Mono<Void> deleteAllErrorLogs() {
		return errorLogRepo.deleteAll()
				.onErrorMap(ex -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete error logs", ex));
	}

	// ? ==================== MÉTODOS PRIVADOS ====================

	private Mono<PagedResponse<ErrorLog>> getErrorLogsByKeywordOrDateRange(String keyword, LocalDateTime startDate, LocalDateTime endDate, PageRequest pageRequest) {
		return errorLogRepo.findByKeywordOrDateRange(keyword, startDate, endDate, pageRequest)
				.collectList()
				.zipWith(errorLogRepo.countByKeywordOrDateRange(keyword, startDate, endDate))
				.map(tuple -> new PagedResponse<>(
						tuple.getT1(), // Lista de errores filtrados
						tuple.getT2(), // Total de registros filtrados
						pageRequest.getPageNumber(),
						pageRequest.getPageSize()
				));
	}
}
