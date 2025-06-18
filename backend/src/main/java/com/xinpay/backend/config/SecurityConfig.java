package com.xinpay.backend.config;

import com.xinpay.backend.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // ❌ CSRF disabled for REST APIs
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/auth/**",      // ✅ Allow login/signup
                    "/ping",         // ✅ Health/ping check
                    "/error",        // ✅ Default error handler
                    "/",             // ✅ Default root
                    "/test/**"       // ✅ Optional testing endpoint
                ).permitAll()
                .anyRequest().authenticated() // 🔒 Everything else secured
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ✅ Expose AuthenticationManager (only needed if you plan to use it directly)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
