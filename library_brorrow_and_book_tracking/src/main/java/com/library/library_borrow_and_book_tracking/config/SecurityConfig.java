package com.library.library_borrow_and_book_tracking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable()) 
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/auth/**", "/css/**", "/js/**").permitAll() // Allow everyone to see login/register
            .anyRequest().authenticated()
        )
        .formLogin(form -> form
            .loginPage("/api/auth/login")            // Points to your @GetMapping
            .loginProcessingUrl("/api/auth/login")   // The URL the form POSTs to
            .defaultSuccessUrl("/dashboard", true)   // Where to go after login
            .permitAll()
        )
        .logout(logout -> logout
            .logoutUrl("/api/auth/logout")
            .logoutSuccessUrl("/api/auth/login?logout")
            .permitAll()
        );

    return http.build();
}
}