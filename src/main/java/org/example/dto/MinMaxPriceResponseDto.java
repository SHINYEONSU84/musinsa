package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 클래스 설명 : API 3(카테고리별 최저/최고 가격 조회)의 응답 데이터를 담는 데이터 전송 객체(DTO)
 * 특정 카테고리에 대해 최저가와 최고가를 제공하는 브랜드들의 정보를 포함한다.
 * 작성자 : sys1021
 * 작성일 : 2025.03.02
 */
@Data               // Lombok: getter, setter, equals, hashCode, toString 자동 생성
@NoArgsConstructor  // Lombok: 파라미터 없는 기본 생성자 자동 생성
@AllArgsConstructor // Lombok: 모든 필드를 파라미터로 받는 생성자 자동 생성
@Builder            // Lombok: 빌더 패턴 구현을 자동으로 생성
public class MinMaxPriceResponseDto {

    /**
     * 조회한 카테고리 이름
     * API 3(카테고리별 최저/최고 가격 조회) 응답의 카테고리 필드로 사용된다.
     */
    private String category;

    /**
     * 해당 카테고리에서 최저가를 제공하는 브랜드 정보 목록
     * 동일한 최저가를 제공하는 브랜드가 여러 개일 경우 모두 포함
     * 각 항목은 브랜드 이름과 가격을 포함하는 맵으로 구성
     * API 3(카테고리별 최저/최고 가격 조회) 응답의 최저가 필드로 사용된다.
     */
    private List<Map<String, String>> minPrice;

    /**
     * 해당 카테고리에서 최고가를 제공하는 브랜드 정보 목록
     * 동일한 최고가를 제공하는 브랜드가 여러 개일 경우 모두 포함
     * 각 항목은 브랜드 이름과 가격을 포함하는 맵으로 구성
     * API 3(카테고리별 최저/최고 가격 조회) 응답의 최고가 필드로 사용된다.
     */
    private List<Map<String, String>> maxPrice;

}