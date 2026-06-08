package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 스프링 부트 애플리케이션의 시작점(Entry Point)이 되는 메인 클래스입니다.
 */
@SpringBootApplication // 스프링 부트의 자동 설정(Auto-Configuration), 빈(Bean) 스캔 등을 모두 활성화하는 핵심 어노테이션입니다.
public class DemoApplication {

    /**
     * 자바 애플리케이션이 처음 실행될 때 가장 먼저 호출되는 메인 메서드입니다.
     * @param args 실행 시 전달되는 커맨드라인 인자(Arguments) 배열입니다.
     */
    public static void main(String[] args) {
        // 내장 톰캣(Tomcat) 웹 서버를 기동시키고, 스프링 컨테이너를 생성하여 애플리케이션을 구동합니다.
        SpringApplication.run(DemoApplication.class, args);
    }
}
