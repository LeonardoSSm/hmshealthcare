package com.medicore.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medicore.presentation.shared.ApiErrorResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;
    private final RequestTraceFilter requestTraceFilter;
    private final UserDetailsService userDetailsService;
    private final String allowedOriginPatternsProperty;
    private final ObjectMapper objectMapper;

    public SecurityConfig(
        JwtAuthFilter jwtAuthFilter,
        RequestTraceFilter requestTraceFilter,
        UserDetailsService userDetailsService,
        @Value("${app.security.cors.allowed-origin-patterns}") String allowedOriginPatternsProperty,
        ObjectMapper objectMapper
    ) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.requestTraceFilter = requestTraceFilter;
        this.userDetailsService = userDetailsService;
        this.allowedOriginPatternsProperty = allowedOriginPatternsProperty;
        this.objectMapper = objectMapper;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, "/api/auth/login", "/api/auth/refresh", "/api/auth/logout").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/panel/queue").permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/actuator/health/**", "/actuator/info").permitAll()
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) ->
                    writeSecurityError(
                        request.getAttribute(RequestTraceFilter.TRACE_ID_ATTRIBUTE),
                        response,
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "AUTHENTICATION_REQUIRED",
                        "Authentication is required"
                    )
                )
                .accessDeniedHandler((request, response, accessDeniedException) ->
                    writeSecurityError(
                        request.getAttribute(RequestTraceFilter.TRACE_ID_ATTRIBUTE),
                        response,
                        HttpServletResponse.SC_FORBIDDEN,
                        "ACCESS_DENIED",
                        "You do not have permission to access this resource"
                    )
                )
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(requestTraceFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(resolveAllowedOriginPatterns());
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With", "X-Request-Id"));
        config.setExposedHeaders(List.of("Authorization", "X-Request-Id"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    private List<String> resolveAllowedOriginPatterns() {
        List<String> resolved = Arrays.stream(allowedOriginPatternsProperty.split(","))
            .map(String::trim)
            .filter(value -> !value.isBlank())
            .toList();
        if (resolved.isEmpty()) {
            throw new IllegalStateException("app.security.cors.allowed-origin-patterns cannot be empty");
        }
        return resolved;
    }

    private void writeSecurityError(
        Object traceId,
        HttpServletResponse response,
        int status,
        String code,
        String message
    ) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(
            objectMapper.writeValueAsString(
                ApiErrorResponse.of(status, code, message, traceId == null ? null : traceId.toString(), null)
            )
        );
    }
}
