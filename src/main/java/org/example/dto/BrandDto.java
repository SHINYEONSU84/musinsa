package org.example.dto;

import org.example.model.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 브랜드 정보를 전송하기 위한 데이터 전송 객체(DTO)
 *
 * 브랜드 생성, 수정 API 요청과 응답에 사용된다.
 * 브랜드 이름과 카테고리별 가격 정보를 포함한다.
 */
@Data               // Lombok: getter, setter, equals, hashCode, toString 자동 생성
@NoArgsConstructor  // Lombok: 파라미터 없는 기본 생성자 자동 생성
@AllArgsConstructor // Lombok: 모든 필드를 파라미터로 받는 생성자 자동 생성
@Builder            // Lombok: 빌더 패턴 구현을 자동으로 생성
public class BrandDto {
    /**
     * 브랜드 이름
     * API 4(브랜드 관리)의 요청/응답에 사용된다.
     */
    private String name;

    /**
     * 카테고리별 가격 정보를 저장하는 맵
     * Key: 카테고리 열거형, Value: 가격(정수)
     * API 4(브랜드 관리)의 요청/응답에 사용된다.
     */
    private Map<Category, Integer> prices;
}