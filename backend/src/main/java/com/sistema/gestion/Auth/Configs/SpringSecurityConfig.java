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
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

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
	public CorsWebFilter corsWebFilter() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOrigins(List.of("https://institutosanpablo.netlify.app"));
		config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
		config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
		config.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);

		return new CorsWebFilter(source);
	}

	@Bean
	SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, JwtAuthenticationWebFilter jwtAuthenticationFilter) {

		return http
				.addFilterAt(corsWebFilter(), SecurityWebFiltersOrder.CORS)
				.csrf(ServerHttpSecurity.CsrfSpec::disable)
				.authorizeExchange(exchanges -> {

					// ? ENDPOINTS PÚBLICOS
					exchanges.pathMatchers(HttpMethod.POST, "/api/auth/login").permitAll();
					exchanges.pathMatchers("/webjars/**", "/swagger-ui.html", "/swagger-ui/**",
							"/v3/api-docs/**", "/swagger-resources/**").permitAll();

					//exchanges.pathMatchers(HttpMethod.GET, "/api/**").permitAll();
					
					// ? ENDPOINTS RESTRINGIDOS POR ROL
					exchanges.pathMatchers(HttpMethod.POST, "/api/auth/registrar")
							.hasAnyAuthority("ROLE_ADMIN", "ROLE_DEV", "ROLE_ADMIN_USERS");
					exchanges.pathMatchers(HttpMethod.PUT, "/api/auth/editar/**", "/api/auth/editar")
							.authenticated();

					// ? MÉTODOS AUTENTICADOS
					authenticateEndpoints(exchanges, "/api/caja/**", "/api/facturas/**", "/api/pagos/**",
							"/api/proveedores/**", "/api/cursos/**", "/api/asistencias/**", "/api/estudiantes/**",
							"/api/course-sections/**", "/api/course-subsections/**", "/api/grades/**", "/api/files/**");

					// ? METODOS de ARCA
					exchanges.pathMatchers(HttpMethod.GET, "/api/afip/**").denyAll();

					// ? MÉTODOS de Archivos
					exchanges.pathMatchers(HttpMethod.POST, "/api/files/subir").hasAnyAuthority("ROLE_ADMIN", "ROLE_TEACHER");
					exchanges.pathMatchers(HttpMethod.PUT, "/api/files/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_TEACHER");
					exchanges.pathMatchers(HttpMethod.DELETE, "/api/files/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_TEACHER");
					exchanges.pathMatchers(HttpMethod.GET, "/api/files/**").permitAll();

					// ? MÉTODOS VirtualCampus
					exchanges.pathMatchers(HttpMethod.GET, "/api/course-sections/**").authenticated();
					exchanges.pathMatchers(HttpMethod.GET, "/api/course-subsections/**").authenticated();
					exchanges.pathMatchers(HttpMethod.POST, "/api/course-sections/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_TEACHER");
					exchanges.pathMatchers(HttpMethod.POST, "/api/course-subsections/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_TEACHER");
					exchanges.pathMatchers(HttpMethod.PUT, "/api/course-sections/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_TEACHER");
					exchanges.pathMatchers(HttpMethod.PUT, "/api/course-subsections/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_TEACHER");
					exchanges.pathMatchers(HttpMethod.DELETE, "/api/course-sections/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_TEACHER");
					exchanges.pathMatchers(HttpMethod.DELETE, "/api/course-subsections/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_TEACHER");

					// ? MÉTODOS de Notas de evaluaciones
					exchanges.pathMatchers(HttpMethod.GET, "/api/grades/**").authenticated();
					exchanges.pathMatchers(HttpMethod.POST, "/api/grades/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_TEACHER");
					exchanges.pathMatchers(HttpMethod.PUT, "/api/grades/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_TEACHER");
					exchanges.pathMatchers(HttpMethod.DELETE, "/api/grades/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_TEACHER");

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