package com.sistema.gestion.Auth.Configs;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import com.sistema.gestion.Auth.CustomReactiveAuthenticationManager;
import com.sistema.gestion.Auth.Filters.JwtAuthenticationWebFilter;

@EnableWebFluxSecurity
@Configuration
public class SpringSecurityConfig {
	@Bean
	public CustomReactiveAuthenticationManager customReactiveAuthenticationManager(
			ReactiveUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
		return new CustomReactiveAuthenticationManager(userDetailsService, passwordEncoder);
	}

	@Bean
	SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, JwtAuthenticationWebFilter jwtAuthenticationFilter) {

		return http
				.cors(cors -> cors.configurationSource(exchange -> {
					CorsConfiguration config = new CorsConfiguration();
					config.setAllowedOriginPatterns(List.of("*"));
					config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
					config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
					config.setAllowCredentials(true);
					return config;
				}))
				.csrf(ServerHttpSecurity.CsrfSpec::disable)
				.authorizeExchange(exchanges -> {

					// ? ENDPOINTS PÚBLICOS
					exchanges.pathMatchers(HttpMethod.POST, "/api/auth/login").permitAll();
					exchanges.pathMatchers("/webjars/**", "/swagger-ui.html", "/swagger-ui/**",
							"/v3/api-docs/**", "/swagger-resources/**").permitAll();

					// ? ENDPOINTS RESTRINGIDOS POR ROL
					exchanges.pathMatchers(HttpMethod.POST, "/api/auth/registrar")
					.permitAll();
							// .hasAnyAuthority("ROLE_ADMIN", "ROLE_DEV");
					exchanges.pathMatchers(HttpMethod.PUT, "/api/auth/editar/**")
							.hasAnyAuthority("ROLE_ADMIN", "ROLE_DEV");

					// ? MÉTODOS AUTENTICADOS
					// authenticateEndpoints(exchanges, "/api/caja/**", "/api/facturas/**", "/api/pagos/**",
					// 		"/api/proveedores/**", "/api/cursos/**", "/api/asistencias/**", "/api/estudiantes/**");
					exchanges.pathMatchers("/api/caja/**", "/api/facturas/**", "/api/pagos/**",
					"/api/proveedores/**", "/api/cursos/**", "/api/asistencias/**", "/api/estudiantes/**")
					.permitAll();

					// ? METODOS de ARCA
					exchanges.pathMatchers(HttpMethod.GET, "/api/afip/**").denyAll();

					// ? MÉTODOS CampusVirtual
					exchanges.pathMatchers(HttpMethod.POST, "/api/files/**").permitAll();

					// ? MÉTODOS VirtualCampus
					exchanges.pathMatchers("/api/course-sections/**").permitAll();
					exchanges.pathMatchers("/api/course-subsections/**").permitAll();

					// ? MÉTODOS ADMIN & DEV
					restrictEndpoints(exchanges, HttpMethod.DELETE, "/api/caja/**", "/api/facturas/**", "/api/pagos/**",
							"/api/proveedores/**", "/api/cursos/**", "/api/asistencias/**", "/api/estudiantes/**");

					// ? MÉTODOS DE DESARROLLO
					devsEndpoints(exchanges, "/api/errors/**");

					exchanges.anyExchange().authenticated();
				})
				.addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
				.httpBasic(Customizer.withDefaults())
				.build();
	}

	// **Método auxiliar para endpoints autenticados
	private void authenticateEndpoints(ServerHttpSecurity.AuthorizeExchangeSpec exchanges, String... paths) {
		for (String path : paths) {
			exchanges.pathMatchers(HttpMethod.GET, path).authenticated();
			exchanges.pathMatchers(HttpMethod.POST, path).authenticated();
			exchanges.pathMatchers(HttpMethod.PUT, path).authenticated();
		}
	}

	// **Método auxiliar para restricciones de ADMIN & DEV
	private void restrictEndpoints(ServerHttpSecurity.AuthorizeExchangeSpec exchanges, HttpMethod method,
	                               String... paths) {
		for (String path : paths) {
			exchanges.pathMatchers(method, path).hasAnyAuthority("ROLE_ADMIN", "ROLE_DEV");
		}
	}

	// **Método auxiliar para logs de desarrollo
	private void devsEndpoints(ServerHttpSecurity.AuthorizeExchangeSpec exchanges, String... paths) {
		for (String path : paths) {
			exchanges.pathMatchers(HttpMethod.GET, path).hasAnyAuthority("ROLE_DEV");
			exchanges.pathMatchers(HttpMethod.POST, path).hasAnyAuthority("ROLE_DEV");
			exchanges.pathMatchers(HttpMethod.PUT, path).hasAnyAuthority("ROLE_DEV");
		}
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	@Primary
	public MapReactiveUserDetailsService userDetailsService() {
		UserDetails user = User
				.withUsername("user")
				.password(passwordEncoder().encode("password"))
				.roles("USER")
				.build();
		return new MapReactiveUserDetailsService(user);
	}
}