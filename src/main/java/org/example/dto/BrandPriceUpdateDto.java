package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 브랜드의 특정 카테고리 가격 업데이트를 위한 데이터 전송 객체(DTO)
 *
 * API 4에서 브랜드의 특정 카테고리 상품 가격만 업데이트하는 요청에 사용된다.
 * 브랜드 이름, 카테고리 이름, 업데이트할 가격 정보를 포함한다.
 */
@Data               // Lombok: getter, setter, equals, hashCode, toString 자동 생성
@NoArgsConstructor  // Lombok: 파라미터 없는 기본 생성자 자동 생성
@AllArgsConstructor // Lombok: 모든 필드를 파라미터로 받는 생성자 자동 생성
@Builder            // Lombok: 빌더 패턴 구현을 자동으로 생성
public class BrandPriceUpdateDto {
    /**
     * 가격을 업데이트할 브랜드 이름
     * API 4(브랜드 관리)의 PUT /api/brand/price 요청에 사용된다.
     */
    private String brandName;

    /**
     * 가격을 업데이트할 카테고리 이름
     * API 4(브랜드 관리)의 PUT /api/brand/price 요청에 사용된다.
     */
    private String categoryName;

    /**
     * 업데이트할 새 가격
     * API 4(브랜드 관리)의 PUT /api/brand/price 요청에 사용된다.
     */
    private int price;
}