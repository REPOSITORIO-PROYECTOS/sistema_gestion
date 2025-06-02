package com.sistema.gestion.Services.Profiles;

import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.sistema.gestion.DTO.PagedResponse;
import com.sistema.gestion.Models.Profiles.Parent;
import com.sistema.gestion.Models.Profiles.Student;
import com.sistema.gestion.Models.Profiles.Teacher;
import com.sistema.gestion.Repositories.Admin.Management.CourseRepository;
import com.sistema.gestion.Repositories.Profiles.ParentsRepository;
import com.sistema.gestion.Repositories.Profiles.StudentRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class ParentService {

	private final ParentsRepository parentsRepository;
    private final StudentRepository studentRepository;
	private final UserService userService;

	// ? ==================== MÉTODOS PÚBLICOS ====================

	public Mono<PagedResponse<Parent>> findAll(int page, int size, String keyword) {
		PageRequest pageRequest = PageRequest.of(page, size);

		if (keyword != null && !keyword.isEmpty()) {
			return searchParents(keyword, pageRequest);
		}

		return getAllParentsPaged(pageRequest);
	}

	public Mono<Long> findAllCount() {
		return parentsRepository.count();
	}

    public Mono<Parent> createParent(Parent parent, String username) {
        return userService.getFullName(username)
                .flatMap(fullName -> {
                    parent.setCreatedBy(fullName.getName() + " " + fullName.getSurname());
                    parent.setCreatedAt(LocalDateTime.now());
                    return parentsRepository.save(parent);
                })
                .onErrorMap(e -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Error al inscribir al padre en los cursos", e));
    }

    public Flux<Student> getChildren(Flux<String> childrenIds) {
        return childrenIds
                .flatMap(id -> studentRepository.findById(id)
                        .switchIfEmpty(Mono.error(new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "No se encontró el estudiante con ID: " + id)))
                );
    }
    

	public Mono<Parent> findById(String id) {
		return parentsRepository.findById(id)
				.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
						"No se encontró el estudiante con ID: " + id)))
				.onErrorMap(e -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
						"Error al buscar el estudiante", e));
	}

	public Mono<Parent> updateParent(String id, Parent parent, String username) {
		return userService.getFullName(username)
				.flatMap(fullName -> parentsRepository.findById(id)
						.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
								"No se encontró el padre con ID: " + id)))
						.flatMap(existingParent -> {
							existingParent.setName(parent.getName());
							existingParent.setSurname(parent.getSurname());
							existingParent.setEmail(parent.getEmail());
							existingParent.setPhone(parent.getPhone());
							existingParent.setUpdatedAt(LocalDateTime.now());
							existingParent.setModifiedBy(fullName.getName() + " " + fullName.getSurname());
							return parentsRepository.save(existingParent);
						}))
				.onErrorMap(e -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
						"Error al actualizar el estudiante", e));
	}

	public Mono<Void> deleteParent(String id) {
		return parentsRepository.findById(id)
				.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
						"No se encontró el estudiante con ID: " + id)))
				.flatMap(parent -> parentsRepository.deleteById(id))
				.onErrorMap(e -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
						"Error al eliminar el estudiante", e));
	}

	// ==================== MÉTODOS PRIVADOS ====================

	private Mono<PagedResponse<Parent>> getAllParentsPaged(PageRequest pageRequest) {
		Mono<Long> totalElementsMono = parentsRepository.countAll();
		Flux<Parent> parentsFlux = parentsRepository.findAllBy(pageRequest);

		return Mono.zip(totalElementsMono, parentsFlux.collectList())
				.map(tuple -> new PagedResponse<>(
						tuple.getT2(), // Lista de estudiantes
						tuple.getT1(), // Total de registros
						pageRequest.getPageNumber(),
						pageRequest.getPageSize()));
	}

	private Mono<PagedResponse<Parent>> searchParents(String query, PageRequest pageRequest) {
		Mono<Long> totalElementsMono = parentsRepository.countByKeyword(query);
		Flux<Parent> parentsFlux = parentsRepository.findByKeywordPaged(query, pageRequest);

		return Mono.zip(totalElementsMono, parentsFlux.collectList())
				.map(tuple -> new PagedResponse<>(
						tuple.getT2(), // Lista de estudiantes encontrados
						tuple.getT1(), // Total de registros encontrados
						pageRequest.getPageNumber(),
						pageRequest.getPageSize()));
	}
}
