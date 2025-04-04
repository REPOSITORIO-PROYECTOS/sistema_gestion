package com.sistema.gestion.Config.DynamicDBConection;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class InstitutionFilter implements WebFilter {

    private DynamicMongoConfig dynamicMongoConfig;

    public InstitutionFilter(DynamicMongoConfig dynamicMongoConfig) {
        this.dynamicMongoConfig = dynamicMongoConfig;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return dynamicMongoConfig.getTemplateFromRequest(exchange)
                .flatMap(template -> {
                    exchange.getAttributes().put("mongoTemplate", template);
                    return chain.filter(exchange);
                });
    }
}

