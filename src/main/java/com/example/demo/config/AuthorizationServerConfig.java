package com.example.demo.config;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.web.SecurityFilterChain;

/**
 * OAuth 2.0 인가 서버(Authorization Server)를 설정하는 클래스입니다.
 * 이 서버는 클라이언트의 자격 증명을 확인하고, 유효한 클라이언트에게 JWT 형태의 Access Token을 발급합니다.
 */
@Configuration
public class AuthorizationServerConfig {

    /**
     * 인가 서버 전용 보안 필터 체인을 설정합니다.
     * @Order(1)을 통해 이 필터 체인이 일반 API 필터 체인보다 먼저 실행되도록(최우선 순위) 지정합니다.
     */
    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http)
            throws Exception {
        
        // OAuth 2.0 인가 서버 설정을 위한 Configurer 객체 생성
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
                new OAuth2AuthorizationServerConfigurer();
        
        http
            // 1. 보안 매처 설정: 이 필터 체인은 인가 서버 관련 엔드포인트(예: /oauth2/token)에만 작동하도록 제한합니다.
            .securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
            // 2. 인가 서버 기본 설정 적용 (기본 필터들 자동 등록)
            .with(authorizationServerConfigurer, Customizer.withDefaults())
            // 3. 해당 엔드포인트에 들어오는 모든 요청은 인증을 요구하도록 설정
            .authorizeHttpRequests((authorize) -> authorize
                .anyRequest().authenticated()
            );

        return http.build();
    }

    /**
     * 인가 서버에 접근할 수 있는 '클라이언트(외부 애플리케이션)' 정보를 등록하고 관리하는 저장소를 설정합니다.
     * 데이터베이스 대신 메모리(In-Memory)에 등록합니다.
     */
    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("my-client") // 클라이언트 아이디 (예: 외부 파트너사 ID)
                .clientSecret("{noop}my-secret") // 클라이언트 비밀번호 ({noop}은 암호화하지 않음을 의미)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC) // Basic Auth 방식으로 ID/PW를 받음
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS) // 서버 간(S2S) 통신에 사용하는 Grant Type 설정
                .scope("read")  // 발급할 토큰의 권한 스코프 지정 (읽기)
                .scope("write") // 발급할 토큰의 권한 스코프 지정 (쓰기)
                .build();

        // 등록된 클라이언트를 인메모리 저장소에 담아 반환합니다.
        return new InMemoryRegisteredClientRepository(registeredClient);
    }

    /**
     * 발급되는 JWT 토큰에 디지털 서명을 하기 위한 비대칭 키(RSA Key Pair) 소스를 설정합니다.
     * 이 키가 있어야 Resource Server가 토큰이 조작되지 않았음을 검증할 수 있습니다.
     */
    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        // 1. RSA 키 쌍 생성 (Private Key, Public Key)
        KeyPair keyPair = generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        
        // 2. JWK (JSON Web Key) 객체로 변환
        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString()) // 키 고유 식별자 부여
                .build();
        
        // 3. JWKSet 객체에 담아서 반환
        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }

    /**
     * 실제로 2048비트 크기의 RSA 키 쌍(비밀키, 공개키)을 생성해주는 헬퍼 메서드입니다.
     */
    private static KeyPair generateRsaKey() {
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
        }
        catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        return keyPair;
    }
}
