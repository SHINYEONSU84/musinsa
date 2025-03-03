package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 클래스 설명 : 카테고리별 최저가격 정보를 전송하기 위한 데이터 전송 객체(DTO)
 * API 1(카테고리별 최저가격 조회)의 응답에 사용된다.
 * 카테고리 이름, 브랜드 이름, 가격 정보를 포함한다.
 * 작성자 : sys1021
 * 작성일 : 2025.03.02
 */
@Data               // Lombok: getter, setter, equals, hashCode, toString 자동 생성
@NoArgsConstructor  // Lombok: 파라미터 없는 기본 생성자 자동 생성
@AllArgsConstructor // Lombok: 모든 필드를 파라미터로 받는 생성자 자동 생성
@Builder            // Lombok: 빌더 패턴 구현을 자동으로 생성
public class CategoryPriceDto {

    /**
     * 카테고리 이름
     * 예: "상의", "아우터", "바지" 등
     * API 1(카테고리별 최저가격 조회) 응답의 카테고리 이름 필드로 사용된다.
     */
    private String category;

    /**
     * 브랜드 이름
     * 해당 카테고리에서 최저가를 제공하는 브랜드 이름
     * 동일한 최저가를 제공하는 브랜드가 여러 개일 경우 콤마로 구분하여 표시
     * API 1(카테고리별 최저가격 조회) 응답의 브랜드 이름 필드로 사용된다.
     */
    private String brand;

    /**
     * 가격 정보
     * 천 단위 구분자(콤마)가 포함된 문자열 형태로 저장
     * API 1(카테고리별 최저가격 조회) 응답의 가격 필드로 사용된다.
     */
    private String price;

}