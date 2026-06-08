package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 외부 클라이언트(예: Postman, 프론트엔드 서버)의 요청을 처리하는 컨트롤러입니다.
 * OAuth 2.0 테스트를 위한 간단한 API 엔드포인트를 제공합니다.
 */
@RestController // 이 클래스 내의 모든 메서드 응답은 데이터(JSON, 일반 텍스트 등)로 직접 클라이언트에게 반환됩니다.
public class HomeController {

    /**
     * (선택사항) 만약 정적 리소스(index.html) 대신 직접 만든 문구를 띄우고 싶다면 이 메서드를 사용합니다.
     * 현재는 브라우저가 http://localhost:8080/ 으로 접근할 때 static/index.html 파일이 열리도록 주석처리/또는 무시되는 상태일 수 있습니다.
     */
    // @GetMapping("/")
    // public String root() {
    //     return "Hello, Spring Boot!";
    // }

    /**
     * 클라이언트가 토큰을 발급받은 뒤 호출하여 접근 권한(인가)을 테스트할 수 있는 '보호된 API'입니다.
     * 
     * @return 성공적으로 인증된 경우 환영 인사 메시지를 반환합니다.
     */
    @GetMapping("/api/hello")
    public String home() {
        return "Hello, Spring Boot!";
    }

    @GetMapping("/test")
    public String test() {
        return "Hello, Spring Boot!";
    }
}
