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

  /*
   * @Bean
   * SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
   * JwtAuthenticationWebFilter jwtAuthenticationFilter) {
   * return http
   * .cors(cors -> cors.configurationSource(exchange -> {
   * CorsConfiguration config = new CorsConfiguration();
   * config.setAllowedOriginPatterns(List.of("*")); // Permitir cualquier origen
   * config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS",
   * "PATCH"));
   * config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
   * config.setAllowCredentials(true); // Permitir cookies o encabezados de
   * autenticación
   * return config;
   * }))
   * .csrf(ServerHttpSecurity.CsrfSpec::disable)
   * .authorizeExchange(exchanges -> exchanges
   * .pathMatchers(HttpMethod.POST, "/auth/login").permitAll()
   * .pathMatchers(HttpMethod.POST, "/auth/registrar").hasAuthority("ROLE_ADMIN")
   * .pathMatchers(HttpMethod.POST, "/auth/registrar").hasAuthority("ROLE_DEV")
   * .pathMatchers(HttpMethod.PUT, "/auth/editar/**").hasAuthority("ROLE_ADMIN")
   * .pathMatchers(HttpMethod.PUT, "/auth/editar/**").hasAuthority("ROLE_DEV")
   * // ENDPOINTS CAJA:
   * .pathMatchers(HttpMethod.GET, "/caja/estado").authenticated()
   * .pathMatchers(HttpMethod.GET, "/caja/balance-mensual").authenticated()
   * .pathMatchers(HttpMethod.POST, "/caja/abrir").authenticated()
   * .pathMatchers(HttpMethod.POST, "/caja/cerrar").authenticated()
   * // TODO: reapertura de caja por admin
   * .pathMatchers(HttpMethod.PUT, "/caja/**").authenticated()
   * .pathMatchers(HttpMethod.DELETE, "/caja/**").hasAuthority("ROLE_ADMIN")
   * .pathMatchers(HttpMethod.DELETE, "/caja/**").hasAuthority("ROLE_DEV")
   * // ENDPOINTS FACTURAS:
   * .pathMatchers(HttpMethod.GET, "/facturas").authenticated()
   * .pathMatchers(HttpMethod.GET, "/facturas/detalles").authenticated()
   * .pathMatchers(HttpMethod.GET, "/facturas/detalles/**").authenticated()
   * .pathMatchers(HttpMethod.POST, "/facturas").authenticated()
   * .pathMatchers(HttpMethod.PUT, "/facturas/**").authenticated()
   * .pathMatchers(HttpMethod.PUT, "/facturas/pagar/**").authenticated()
   * .pathMatchers(HttpMethod.DELETE, "/facturas/**").hasAuthority("ROLE_ADMIN")
   * .pathMatchers(HttpMethod.DELETE, "/facturas/**").hasAuthority("ROLE_DEV")
   * // ENDPOINTS PAGOS:
   * .pathMatchers(HttpMethod.GET, "/pagos/todos").authenticated()
   * .pathMatchers(HttpMethod.GET, "/pagos/todos-con-detalle").authenticated()
   * .pathMatchers(HttpMethod.GET, "/pagos/cuotas-adeudadas").authenticated()
   * .pathMatchers(HttpMethod.GET, "/pagos/con-deuda/**").authenticated()
   * .pathMatchers(HttpMethod.GET, "/pagos/estudiante/**").authenticated()
   * .pathMatchers(HttpMethod.GET, "/pagos/**").authenticated()
   * .pathMatchers(HttpMethod.POST, "/pagos").authenticated()
   * .pathMatchers(HttpMethod.POST, "/pagos/generar-cuotas").authenticated()
   * .pathMatchers(HttpMethod.PUT, "/realizar/**").authenticated()
   * .pathMatchers(HttpMethod.PUT, "/editar/**").authenticated()
   * .pathMatchers(HttpMethod.DELETE, "/pagos/**").hasAuthority("ROLE_ADMIN")
   * .pathMatchers(HttpMethod.DELETE, "/pagos/**").hasAuthority("ROLE_DEV")
   * 
   * // ENDPOINTS PROVEEDORES
   * .pathMatchers(HttpMethod.GET, "/proveedores").authenticated()
   * .pathMatchers(HttpMethod.GET, "/proveedores/**").authenticated()
   * .pathMatchers(HttpMethod.POST, "/proveedores").authenticated()
   * .pathMatchers(HttpMethod.PUT, "/proveedores/**").authenticated()
   * .pathMatchers(HttpMethod.DELETE,
   * "/proveedores/**").hasAuthority("ROLE_ADMIN")
   * .pathMatchers(HttpMethod.DELETE, "/proveedores/**").hasAuthority("ROLE_DEV")
   * // ENDPOINTS CURSOS
   * .pathMatchers(HttpMethod.GET, "/cursos").authenticated()
   * .pathMatchers(HttpMethod.GET, "/cursos/**").authenticated()
   * .pathMatchers(HttpMethod.GET, "/cursos/buscar").authenticated()
   * .pathMatchers(HttpMethod.POST, "/cursos").authenticated()
   * .pathMatchers(HttpMethod.PUT, "/cursos/**").authenticated()
   * .pathMatchers(HttpMethod.PUT, "/cursos/inscripcion/**").authenticated()
   * .pathMatchers(HttpMethod.PUT, "/cursos/desinscripcion/**").authenticated()
   * .pathMatchers(HttpMethod.DELETE, "/cursos/**").hasAuthority("ROLE_ADMIN")
   * .pathMatchers(HttpMethod.DELETE, "/cursos/**").hasAuthority("ROLE_DEV")
   * // ENDPOINTS ASISTENCIA
   * .pathMatchers(HttpMethod.GET, "/api/asistencias").authenticated()
   * .pathMatchers(HttpMethod.GET, "/api/asistencias/**").authenticated()
   * .pathMatchers(HttpMethod.POST, "/api/asistencias").authenticated()
   * .pathMatchers(HttpMethod.PUT,
   * "/api/asistencias/modificar-asistencia").authenticated()
   * .pathMatchers(HttpMethod.DELETE,
   * "/api/asistencias/**").hasAuthority("ROLE_ADMIN")
   * .pathMatchers(HttpMethod.DELETE,
   * "/api/asistencias/**").hasAuthority("ROLE_DEV")
   * // ENDPOINTS ESTUDIANTES
   * .pathMatchers(HttpMethod.GET, "/api/estudiantes/todos").authenticated()
   * .pathMatchers(HttpMethod.GET, "/api/estudiantes/**").authenticated()
   * .pathMatchers(HttpMethod.POST, "/api/estudiantes/crear").authenticated()
   * .pathMatchers(HttpMethod.PUT,
   * "/api/estudiantes/actualizar/**").authenticated()
   * .pathMatchers(HttpMethod.DELETE,
   * "/api/estudiantes/eliminar/**").hasAuthority("ROLE_ADMIN")
   * .pathMatchers(HttpMethod.DELETE,
   * "/api/estudiantes/eliminar/**").hasAuthority("ROLE_DEV")
   * .anyExchange().authenticated())
   * .addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
   * .httpBasic(Customizer.withDefaults())
   * .build();
   * }
   */

  @Bean
  SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
      JwtAuthenticationWebFilter jwtAuthenticationFilter) {
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

          // ENDPOINTS PÚBLICOS
          exchanges.pathMatchers(HttpMethod.POST, "/auth/login").permitAll();

          // ENDPOINTS RESTRINGIDOS POR ROL
          exchanges.pathMatchers(HttpMethod.POST, "/auth/registrar").hasAnyAuthority("ROLE_ADMIN", "ROLE_DEV");
          exchanges.pathMatchers(HttpMethod.PUT, "/auth/editar/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_DEV");

          // MÉTODOS AUTENTICADOS
          authenticateEndpoints(exchanges, "/caja/**", "/facturas/**", "/pagos/**",
              "/proveedores/**", "/cursos/**", "/api/asistencias/**", "/api/estudiantes/**");

          // MÉTODOS ADMIN & DEV
          restrictEndpoints(exchanges, HttpMethod.DELETE, "/caja/**", "/facturas/**", "/pagos/**",
              "/proveedores/**", "/cursos/**", "/api/asistencias/**", "/api/estudiantes/**");

          // MÉTODOS DE DESARROLLO
          devsEndpoints(exchanges, "/errors/**");

          exchanges.anyExchange().authenticated();
        })
        .addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
        .httpBasic(Customizer.withDefaults())
        .build();
  }

  // **Método auxiliar para endpoints autenticados**
  private void authenticateEndpoints(ServerHttpSecurity.AuthorizeExchangeSpec exchanges, String... paths) {
    for (String path : paths) {
      exchanges.pathMatchers(HttpMethod.GET, path).authenticated();
      exchanges.pathMatchers(HttpMethod.POST, path).authenticated();
      exchanges.pathMatchers(HttpMethod.PUT, path).authenticated();
    }
  }

  // **Método auxiliar para restricciones de ADMIN & DEV**
  private void restrictEndpoints(ServerHttpSecurity.AuthorizeExchangeSpec exchanges, HttpMethod method,
      String... paths) {
    for (String path : paths) {
      exchanges.pathMatchers(method, path).hasAnyAuthority("ROLE_ADMIN", "ROLE_DEV");
    }
  }

  // **Método auxiliar para logs de desarrollo**
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
