package com.vidya.studyapp.config;

import com.vidya.studyapp.security.CustomAccessDeniedHandler;
import com.vidya.studyapp.security.CustomUserDetailsService;
import com.vidya.studyapp.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        System.out.println("🔐 Configuring Security Filter Chain");

        http
            .authenticationProvider(daoAuthenticationProvider())
            .cors().and()
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth

                //  Auth endpoints (Login & Signup)
                .requestMatchers("/api/auth/**").permitAll()

                // Admin section
                .requestMatchers("/api/admin/**").hasRole("ADMIN")

                // Material upload/view/delete
                .requestMatchers("/api/material/upload").hasAnyRole("TEACHER", "STUDENT")
                .requestMatchers("/api/material/my-materials").hasAnyRole("TEACHER", "STUDENT")
                .requestMatchers("/api/material/student/materials").hasRole("STUDENT")
                .requestMatchers("/api/material/all").hasRole("ADMIN")
                .requestMatchers("/api/material/delete/**").hasAnyRole("TEACHER", "STUDENT", "ADMIN")

                //  Summary generation, view, delete
                .requestMatchers("/api/summary/**").hasRole("STUDENT")

                //  Feedback
                .requestMatchers("/api/feedback/submit").hasAnyRole("TEACHER", "STUDENT")
                .requestMatchers("/api/feedback/my").hasAnyRole("TEACHER", "STUDENT")
                .requestMatchers("/api/feedback/all").hasRole("ADMIN")
                .requestMatchers("/api/feedback/user/**").hasRole("ADMIN")
                .requestMatchers("/api/feedback/**").authenticated()

                //  Tags (GET only - visible to all, but write access is not needed anymore)
                .requestMatchers("/api/tags/all").hasAnyRole("TEACHER", "STUDENT")

                //  Any other request
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex.accessDeniedHandler(customAccessDeniedHandler));

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("http://localhost:3000")); // React frontend
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList(
            "Authorization", 
            "Content-Type", 
            "Accept", 
            "Cache-Control", 
            "X-Requested-With",
            "Origin",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers"
        ));
        config.setExposedHeaders(Arrays.asList("Authorization"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
