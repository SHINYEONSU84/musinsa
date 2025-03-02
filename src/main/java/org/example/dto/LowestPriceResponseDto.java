package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * API 1(카테고리별 최저가격 조회)의 응답 데이터를 담는 데이터 전송 객체(DTO)
 *
 * 각 카테고리별 최저가격 브랜드와 가격 정보를 리스트 형태로 포함하고,
 * 모든 카테고리 최저가의 총합을 함께 제공한다.
 */
@Data               // Lombok: getter, setter, equals, hashCode, toString 자동 생성
@NoArgsConstructor  // Lombok: 파라미터 없는 기본 생성자 자동 생성
@AllArgsConstructor // Lombok: 모든 필드를 파라미터로 받는 생성자 자동 생성
@Builder            // Lombok: 빌더 패턴 구현을 자동으로 생성
public class LowestPriceResponseDto {
    /**
     * 카테고리별 최저가격 정보 목록
     * 각 카테고리에 대해 최저가를 제공하는 브랜드와 가격 정보를 포함한다.
     * API 1(카테고리별 최저가격 조회) 응답의 카테고리 리스트로 사용된다.
     */
    private List<CategoryPriceDto> categories;

    /**
     * 모든 카테고리 최저가의 총합
     * 천 단위 구분자(콤마)가 포함된 문자열 형태로 저장
     * API 1(카테고리별 최저가격 조회) 응답의 총액 필드로 사용된다.
     */
    private String totalPrice;
}