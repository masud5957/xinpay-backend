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
            .csrf(csrf -> csrf.disable()) // ❌ Disable CSRF for API use
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/auth/**",                  // ✅ Public auth endpoints
                    "/ping",                     // ✅ Health check
                    "/error",                    // ✅ Spring error handler
                    "/",                         // ✅ Homepage
                    "/test/**",                  // ✅ Testing endpoints
                    "/uploads/**",               // ✅ Serve uploaded screenshots
                    "/api/upload",               // ✅ Upload endpoint
                    "/api/deposit/status/**",    // ✅ Deposit status check
                    "/api/inr-deposits/**",      // ✅ INR admin & user endpoints
                    "/api/usdt-deposits/**",     // ✅ ✅ USDT admin & user endpoints — newly added
                    "/api/balance/**"            // ✅ Balance fetch
                ).permitAll()
                .anyRequest().authenticated()    // 🔐 Everything else requires auth
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
