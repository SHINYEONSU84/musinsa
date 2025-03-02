package org.example.repository;

import org.example.model.Brand;
import org.example.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Brand 엔티티에 대한 데이터 액세스 계층 인터페이스
 *
 * Spring Data JPA를 활용하여 Brand 엔티티의 CRUD 연산 및
 * 추가적인 쿼리 기능을 제공한다. JpaRepository를 상속받아
 * 기본적인 CRUD 메서드(findAll, findById, save, delete 등)를
 * 자동으로 구현한다.
 *
 * 무신사 코디 서비스의 브랜드 데이터 조회와 관련된 특화된 쿼리 메서드를
 * 정의하고 있으며, 이는 BrandService에서 활용된다.
 */
@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {

    /**
     * 브랜드 이름으로 브랜드를 조회하는 메서드
     *
     * Spring Data JPA의 메서드 이름 규칙을 따라 자동으로 쿼리가 생성된다.
     * 이 메서드는 내부적으로 "SELECT b FROM Brand b WHERE b.name = ?1" 쿼리를 실행한다.
     *
     * @param name 조회할 브랜드의 이름
     * @return 이름과 일치하는 브랜드 객체, 없을 경우 null 반환
     */
    Brand findByName(String name);

    /**
     * 특정 카테고리의 가격을 기준으로 오름차순으로 정렬된 브랜드 목록을 조회하는 메서드
     *
     * Map 형태로 저장된 카테고리별 가격 정보(prices)에서 특정 카테고리의 가격이
     * 낮은 순서대로 브랜드를 정렬하여 반환한다.
     *
     * JOIN 구문을 통해 브랜드와 해당 브랜드의 가격 맵을 조인하고,
     * WHERE 절에서 특정 카테고리를 필터링한 후,
     * ORDER BY 절에서 가격을 기준으로 오름차순 정렬한다.
     *
     * KEY(p)는 Map의 키(Category)를 참조하고, VALUE(p)는 Map의 값(Integer 가격)을 참조한다.
     *
     * @param category 가격을 비교할 카테고리 (Map의 키)
     * @return 해당 카테고리의 가격이 낮은 순으로 정렬된 브랜드 목록
     */
    @Query("SELECT b FROM Brand b JOIN b.prices p WHERE KEY(p) = :category ORDER BY VALUE(p) ASC")
    List<Brand> findAllByCategoryOrderByPriceAsc(@Param("category") Category category);

    /**
     * 특정 카테고리의 가격을 기준으로 내림차순으로 정렬된 브랜드 목록을 조회하는 메서드
     *
     * Map 형태로 저장된 카테고리별 가격 정보(prices)에서 특정 카테고리의 가격이
     * 높은 순서대로 브랜드를 정렬하여 반환한다.
     *
     * JOIN 구문을 통해 브랜드와 해당 브랜드의 가격 맵을 조인하고,
     * WHERE 절에서 특정 카테고리를 필터링한 후,
     * ORDER BY 절에서 가격을 기준으로 내림차순 정렬한다.
     *
     * KEY(p)는 Map의 키(Category)를 참조하고, VALUE(p)는 Map의 값(Integer 가격)을 참조한다.
     *
     * @param category 가격을 비교할 카테고리 (Map의 키)
     * @return 해당 카테고리의 가격이 높은 순으로 정렬된 브랜드 목록
     */
    @Query("SELECT b FROM Brand b JOIN b.prices p WHERE KEY(p) = :category ORDER BY VALUE(p) DESC")
    List<Brand> findAllByCategoryOrderByPriceDesc(@Param("category") Category category);
}