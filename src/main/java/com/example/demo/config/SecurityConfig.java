package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 전반적인 웹 애플리케이션 보안(일반 API 및 정적 리소스 보호)을 담당하는 클래스입니다.
 * OAuth 2.0 리소스 서버(Resource Server) 역할도 이곳에서 함께 설정됩니다.
 */
@Configuration
@EnableWebSecurity // 스프링 시큐리티 필터 체인을 활성화합니다.
public class SecurityConfig {

    /**
     * 일반 API 요청에 적용될 기본 보안 필터 체인을 설정합니다.
     * 인가 서버 체인(Order=1)보다 우선순위가 낮으므로, 인증 서버의 엔드포인트가 아닌 요청들이 이곳을 거칩니다.
     */
    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http)
            throws Exception {
        http
            // 1. 요청 주소별 권한 설정
            .authorizeHttpRequests((authorize) -> authorize
                // 루트(/), index.html, 파비콘, 에러 페이지 등 정적 리소스는 누구나 인증 없이 접근 가능하도록 허용(permitAll)합니다.
                .requestMatchers("/", "/index.html", "/favicon.ico", "/error").permitAll()
                // 그 외의 모든 요청(예: /api/hello)은 올바른 인증을 거쳐야만 접근 가능합니다.
                .anyRequest().authenticated()
            )
            // 2. OAuth 2.0 리소스 서버 설정
            // 클라이언트가 헤더에 JWT 토큰을 담아서 보내면, 이 설정을 통해 스프링 시큐리티가 자동으로 토큰의 유효성(서명, 만료여부 등)을 검증합니다.
            .oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults()));

        return http.build();
    }
}
