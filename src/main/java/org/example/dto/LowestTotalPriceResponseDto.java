package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * API 2(단일 브랜드 최저 총액 조회)의 응답 데이터를 담는 데이터 전송 객체(DTO)
 *
 * 모든 카테고리 제품을 구매할 때 최저 총액을 제공하는 브랜드 정보,
 * 각 카테고리별 가격 정보, 총 금액을 포함한다.
 */
@Data               // Lombok: getter, setter, equals, hashCode, toString 자동 생성
@NoArgsConstructor  // Lombok: 파라미터 없는 기본 생성자 자동 생성
@AllArgsConstructor // Lombok: 모든 필드를 파라미터로 받는 생성자 자동 생성
@Builder            // Lombok: 빌더 패턴 구현을 자동으로 생성
public class LowestTotalPriceResponseDto {
    /**
     * 최저 총액을 제공하는 브랜드 이름
     * API 2(단일 브랜드 최저 총액 조회) 응답의 브랜드 이름 필드로 사용된다.
     */
    private String brand;

    /**
     * 각 카테고리별 가격 정보 목록
     * 키-값 쌍 형태로 카테고리 이름과 가격 정보를 포함한다.
     * API 2(단일 브랜드 최저 총액 조회) 응답의 카테고리 리스트로 사용된다.
     */
    private List<Map<String, String>> categories;

    /**
     * 모든 카테고리 제품의 총 금액
     * 천 단위 구분자(콤마)가 포함된 문자열 형태로 저장
     * API 2(단일 브랜드 최저 총액 조회) 응답의 총액 필드로 사용된다.
     */
    private String totalPrice;
}