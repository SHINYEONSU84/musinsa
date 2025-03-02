package org.example;

import org.example.service.BrandService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BrandShoppingApplication {

    public static void main(String[] args) {
        SpringApplication.run(BrandShoppingApplication.class, args);
    }

    @Bean
    public CommandLineRunner init(BrandService brandService) {
        return args -> {
            // 애플리케이션 시작 시 초기 브랜드 데이터 로드
            brandService.initializeBrands();
        };
    }
}