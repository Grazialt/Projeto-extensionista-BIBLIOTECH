package com.bibliotech;

import com.bibliotech.Security.JwtAuthenticationFilter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter
    ) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())

            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

           .authorizeHttpRequests(auth -> auth

           // Páginas públicas
                      .requestMatchers(
                "/login.html",
                        "/cadastro.html",
                      "/dashboard.html"
                           )
                     .permitAll()

            // Rotas públicas da API
            .requestMatchers(
              "/api/login",
              "/api/register"
                ).permitAll()

               // Listagem de usuários
              .requestMatchers(HttpMethod.GET, "/api/usuarios")
             .hasAnyRole("ADMIN", "BIBLIOTECARIO")

               // Cadastro de usuários
             .requestMatchers(HttpMethod.POST, "/api/usuarios")
             .hasAnyRole("ADMIN", "BIBLIOTECARIO")

             // Edição e exclusão de usuários
                .requestMatchers(
                    HttpMethod.PUT, "/api/usuarios/**"
                ).hasAnyRole("ADMIN", "BIBLIOTECARIO")

                .requestMatchers(
                    HttpMethod.DELETE, "/api/usuarios/**"
                ).hasAnyRole("ADMIN", "BIBLIOTECARIO")

                // Consulta de usuário por e-mail
                .requestMatchers(
                 HttpMethod.GET, "/api/usuario/email/**"
                ).hasAnyRole("ADMIN", "BIBLIOTECARIO")

                // Demais rotas exigem autenticação
                .anyRequest().authenticated()
            )

            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter>
            jwtFilterRegistration(JwtAuthenticationFilter filter) {

        FilterRegistrationBean<JwtAuthenticationFilter> registration =
                new FilterRegistrationBean<>(filter);

        registration.setEnabled(false);

        return registration;
    }
}
