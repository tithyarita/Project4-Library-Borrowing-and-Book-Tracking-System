package com.library.library_borrow_and_book_tracking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    // Password encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Security filter chain
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // Disable CSRF for simplicity
            .csrf(csrf -> csrf.disable())

            // Public pages
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/login",
                    "/api/auth/register",
                    "/api/auth/login",
                    "/css/**",
                    "/js/**",
                    "/images/**"
                ).permitAll()
                // Any other request requires authentication
                .anyRequest().authenticated()
            )

            // Form login
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/api/auth/login")
                .usernameParameter("email")
                .passwordParameter("password")
                // ALWAYS redirect to /home after login
                .successHandler((request, response, authentication) -> {
                    response.sendRedirect("/home");
                })
                .failureUrl("/login?error")
                .permitAll()
            )

            // Logout config
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }
}
