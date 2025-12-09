package com.jchat.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Value("${frontend.url}")
//    private String frontendUrl;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CORS 활성화
                .cors(Customizer.withDefaults())

                // CSRF 비활성화 (REST API용)
                .csrf(csrf -> csrf.disable())

                // 세션 비활성화 (JWT 사용 시)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );
//
//                // URL별 권한 설정
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/mem/registerUser", "/mem/login").permitAll()
//                        .requestMatchers("/api/public/**").permitAll()
//                        .anyRequest().authenticated()
//                );

        return http.build();
    }
}
