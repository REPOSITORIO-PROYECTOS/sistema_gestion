package com.sistema.gestion.Services.Admin.Management.VirtualCampus;

import com.sistema.gestion.Models.Admin.Management.VirtualCampus.News;
import com.sistema.gestion.Repositories.Admin.Management.VirtualCampus.NewsRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class NewsService {

    private final NewsRepository newsRepository;

    public NewsService(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    public Mono<News> create(News news) {
        return newsRepository.save(news);
    }

    public Mono<News> update(String id, News news) {
        news.setId(id);
        return newsRepository.save(news);
    }

    public Mono<Void> delete(String id) {
        return newsRepository.deleteById(id);
    }

    public Mono<News> findById(String id) {
        return newsRepository.findById(id);
    }

    public Flux<News> findByCategoryOrdered(String category) {
        return newsRepository.findByCategory(category)
                .sort((n1, n2) -> Boolean.compare(n2.isImportant(), n1.isImportant()));
    }
}
