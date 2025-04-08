package com.sistema.gestion.Controllers.Admin.Management.VirtualCampus;

import com.sistema.gestion.Models.Admin.Management.VirtualCampus.News;
import com.sistema.gestion.Services.Admin.Management.VirtualCampus.NewsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<News> createNews(@Valid @RequestBody News news) {
        return newsService.create(news);
    }

    @PutMapping("/{id}")
    public Mono<News> updateNews(@PathVariable String id, @Valid @RequestBody News news) {
        return newsService.update(id, news);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteNews(@PathVariable String id) {
        return newsService.delete(id);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<News>> getNewsById(@PathVariable String id) {
        return newsService.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/category/{category}")
    public Flux<News> getNewsByCategory(@PathVariable String category) {
        return newsService.findByCategoryOrdered(category);
    }
}
