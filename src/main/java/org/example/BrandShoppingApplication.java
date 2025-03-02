package org.example;

import org.example.service.BrandService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * 무신사 코디 서비스 애플리케이션의 메인 클래스
 *
 * 이 클래스는 Spring Boot 애플리케이션의 진입점(entry point) 역할을 하며,
 * 애플리케이션 실행 및 초기화 로직을 담당한다.
 *
 * @SpringBootApplication 어노테이션은 다음 세 가지 어노테이션을 포함한다:
 * - @Configuration: 스프링 설정 클래스임을 나타낸다
 * - @EnableAutoConfiguration: 스프링 부트의 자동 설정 기능을 활성화한다
 * - @ComponentScan: 애플리케이션 컴포넌트를 자동으로 탐색하고 빈으로 등록한다
 */
@SpringBootApplication
public class BrandShoppingApplication {

    /**
     * 애플리케이션의 진입점(entry point)
     *
     * 이 메서드는 JVM에 의해 호출되어 Spring Boot 애플리케이션을 실행한다.
     * SpringApplication.run 메서드는 Spring 애플리케이션 컨텍스트를 생성하고,
     * 애플리케이션을 실행하며, 내장 웹 서버를 시작한다.
     *
     * @param args 명령행 인자
     */
    public static void main(String[] args) {
        SpringApplication.run(BrandShoppingApplication.class, args);
    }

    /**
     * 애플리케이션 초기화를 위한 CommandLineRunner 빈
     *
     * CommandLineRunner 인터페이스는 애플리케이션 컨텍스트가 완전히 로드된 후,
     * 그러나 애플리케이션이 완전히 시작되기 전에 실행되는 코드를 정의한다.
     *
     * 이 빈은 애플리케이션 시작 시 초기 브랜드 데이터를 데이터베이스에 로드하는 역할을 한다.
     * Spring의 의존성 주입을 통해 BrandService가 자동으로 주입된다.
     *
     * @param brandService 브랜드 관련 비즈니스 로직을 처리하는 서비스 객체
     * @return CommandLineRunner 구현체 - 애플리케이션 시작 시 실행될 람다 함수
     */
    @Bean
    public CommandLineRunner init(BrandService brandService) {
        return args -> {
            // 애플리케이션 시작 시 초기 브랜드 데이터 로드
            // 이 메서드는 데이터베이스가 비어 있을 경우에만 초기 데이터를 추가한다
            brandService.initializeBrands();
        };
    }
}