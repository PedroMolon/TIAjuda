package com.projeto.tiajuda.configuration.security;

import com.projeto.tiajuda.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final SecurityFilter securityFilter;
    private final UserDetailsServiceImpl userDetailsService;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHanldler accessDeniedHandler;

    public SecurityConfig(SecurityFilter securityFilter, UserDetailsServiceImpl userDetailsService, @Qualifier("handlerExceptionResolver")HandlerExceptionResolver handlerExceptionResolver) {
        this.securityFilter = securityFilter;
        this.userDetailsService = userDetailsService;
        this.authenticationEntryPoint = new CustomAuthenticationEntryPoint(handlerExceptionResolver);
        this.accessDeniedHandler = new CustomAccessDeniedHanldler(handlerExceptionResolver);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .authorizeHttpRequests(authorize -> authorize
                        // Acesso público aos endpoins de autenticação
                        .requestMatchers("/tiajuda/auth/**").permitAll()

                        // Acesso autenticado para criar um serviço
                        .requestMatchers(HttpMethod.POST, "/tiajuda/service-request").authenticated()

                        // Acesso autenticado para acessar os serviços criados
                        .requestMatchers(HttpMethod.GET, "/tiajuda/service-request").authenticated()
                        .requestMatchers(HttpMethod.GET, "/tiajuda/service-request/{id}").authenticated()

                        // Listar todas propostas (ADMIN)
                        .requestMatchers(HttpMethod.GET, "/tiajuda/proposals").hasRole("ADMIN")
                        // Lista propostas por serviço (CLIENTE ou TECNICO)
                        .requestMatchers(HttpMethod.GET, "/tiajuda/proposals/service/{serviceId}").authenticated()
                        // Listar minhas propostas (TECNICOS)
                        .requestMatchers(HttpMethod.GET, "/tiajuda/proposals/my").hasRole("TECHNICIAN")
                        // Listar uma proposta especifica (CLIENTE, TECNICO)
                        .requestMatchers(HttpMethod.GET, "/tiajuda/proposals/{id}").authenticated()

                        // Criar uma proposta (TECNICO)
                        .requestMatchers(HttpMethod.POST, "/tiajuda/proposals").hasRole("TECHNICIAN")

                        // Atualizar proposta (TECNICO)
                        .requestMatchers(HttpMethod.PUT, "/tiajuda/proposals/{id}").hasRole("TECHNICIAN")

                        // Deletar proposta (TECNICO ou ADMIN)
                        .requestMatchers(HttpMethod.DELETE, "/tiajuda/proposals/{id}").hasAnyRole("TECHNICIAN", "ADMIN")

                        // Aceitar e rejeitar propostas
                        .requestMatchers(HttpMethod.PUT, "/tiajuda/proposals/{id}/accept").hasRole("CLIENT")
                        .requestMatchers(HttpMethod.PUT, "/tiajuda/proposals/{id}/reject").hasRole("CLIENT")

                        // Criar uma avaliação (CLIENTE)
                        .requestMatchers(HttpMethod.POST, "/tiajuda/ratings").hasRole("CLIENT")

                        // Endpoints que exigem autenticação
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
