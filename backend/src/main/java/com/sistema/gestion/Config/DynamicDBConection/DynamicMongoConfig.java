package com.sistema.gestion.Config.DynamicDBConection;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.SimpleReactiveMongoDatabaseFactory;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
public class DynamicMongoConfig {

    @Bean
    public ReactiveMongoTemplate reactiveMongoTemplate() {
        // Conexión por defecto
        return getTemplateForInstitution("default");
    }

    public ReactiveMongoTemplate getTemplateForInstitution(String institutionId) {
        institutionId = "test";
        String uri = "mongodb+srv://juanexequielmorales:Juan%40xd%401810@test.of8ut.mongodb.net/" + institutionId + "?retryWrites=true&w=majority";
        MongoClient mongoClient = MongoClients.create(uri);
        ReactiveMongoDatabaseFactory factory = new SimpleReactiveMongoDatabaseFactory(mongoClient, institutionId);
        return new ReactiveMongoTemplate(factory);
    }

    // Obtiene el ID de institución desde el encabezado o contexto
    public Mono<ReactiveMongoTemplate> getTemplateFromRequest(ServerWebExchange exchange) {
        String institutionId = exchange.getRequest().getHeaders().getFirst("X-Institution-Id");
        //System.out.println(exchange.getRequest().getHeaders().getFirst("X-Institution-Id"));
        if (institutionId == null) {
            institutionId = "default"; // Por defecto
        }
        return Mono.just(getTemplateForInstitution(institutionId));
    }
}
