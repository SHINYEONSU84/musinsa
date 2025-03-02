package org.example.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * 브랜드 엔티티 클래스
 *
 * 무신사 코디 서비스에서 상품을 판매하는 브랜드를 나타내는 JPA 엔티티 클래스이다.
 * 하나의 브랜드는 여러 카테고리의 상품을 가질 수 있으며, 각 카테고리별로 가격 정보를 포함한다.
 * Lombok 라이브러리를 활용하여 기본 메서드(getter, setter, equals, hashCode 등)를 자동 생성한다.
 *
 * 데이터베이스에는 'brand' 테이블(기본 엔티티명)과 관련 컬렉션 테이블('brand_products')로 저장된다.
 */
@Entity  // JPA 엔티티임을 나타내는 어노테이션
@Data    // Lombok 어노테이션: getter, setter, equals, hashCode, toString 메서드 자동 생성
@NoArgsConstructor  // Lombok 어노테이션: 파라미터 없는 기본 생성자 자동 생성
@AllArgsConstructor // Lombok 어노테이션: 모든 필드를 매개변수로 받는 생성자 자동 생성
@Builder  // Lombok 어노테이션: 빌더 패턴 구현 코드 자동 생성
public class Brand {

    /**
     * 브랜드의 고유 식별자(ID)
     *
     * 데이터베이스에서 기본 키(Primary Key)로 사용되며, 자동 증가(Auto Increment) 방식으로 생성된다.
     */
    @Id  // JPA 기본 키 지정 어노테이션
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 기본 키 생성 전략: IDENTITY(데이터베이스에 위임)
    private Long id;

    /**
     * 브랜드 이름
     *
     * 브랜드를 식별하는 문자열 이름
     * 데이터베이스의 'name' 컬럼에 매핑된다.
     */
    private String name;

    /**
     * 카테고리별 상품 가격 정보
     *
     * 카테고리(Category)를 키로, 가격(Integer)을 값으로 하는 맵 구조로 저장된다.
     * 이 맵은 별도의 컬렉션 테이블('brand_products')에 저장되며, 브랜드와 일대다(1:N) 관계를 가진다.
     *
     * 새로운 브랜드 인스턴스 생성 시 빈 HashMap으로 초기화된다.
     */
    @ElementCollection  // 값 타입 컬렉션을 매핑하는 어노테이션
    @CollectionTable(
            name = "brand_products",  // 컬렉션을 저장할 테이블 이름
            joinColumns = @JoinColumn(name = "brand_id")  // 외래 키로 사용할 컬럼
    )
    @MapKeyEnumerated(EnumType.STRING)  // 맵의 키(Category 열거형)를 문자열로 저장
    @MapKeyColumn(name = "category")   // 맵의 키가 저장될 컬럼 이름
    @Column(name = "price")  // 맵의 값(가격)이 저장될 컬럼 이름
    private Map<Category, Integer> prices = new HashMap<>();  // 기본값으로 빈 HashMap 설정
}