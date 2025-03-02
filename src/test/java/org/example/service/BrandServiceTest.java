package org.example.service;

import org.example.model.Brand;
import org.example.model.Category;
import org.example.repository.BrandRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BrandServiceTest {

    @Mock
    private BrandRepository brandRepository;

    @InjectMocks
    private BrandService brandService;

    private Brand brandA;
    private Brand brandB;
    private Brand brandC;
    private List<Brand> allBrands;

    @BeforeEach
    void setUp() {
        // 테스트용 브랜드 데이터 설정
        brandA = new Brand();
        brandA.setId(1L);
        brandA.setName("A");
        brandA.getPrices().put(Category.TOP, 11200);
        brandA.getPrices().put(Category.OUTER, 5500);
        brandA.getPrices().put(Category.PANTS, 4200);
        brandA.getPrices().put(Category.SNEAKERS, 9000);
        brandA.getPrices().put(Category.BAG, 2000);
        brandA.getPrices().put(Category.HAT, 1700);
        brandA.getPrices().put(Category.SOCKS, 1800);
        brandA.getPrices().put(Category.ACCESSORY, 2300);

        brandB = new Brand();
        brandB.setId(2L);
        brandB.setName("B");
        brandB.getPrices().put(Category.TOP, 10500);
        brandB.getPrices().put(Category.OUTER, 5900);
        brandB.getPrices().put(Category.PANTS, 3800);
        brandB.getPrices().put(Category.SNEAKERS, 9100);
        brandB.getPrices().put(Category.BAG, 2100);
        brandB.getPrices().put(Category.HAT, 2000);
        brandB.getPrices().put(Category.SOCKS, 2000);
        brandB.getPrices().put(Category.ACCESSORY, 2200);

        brandC = new Brand();
        brandC.setId(3L);
        brandC.setName("C");
        brandC.getPrices().put(Category.TOP, 10000);
        brandC.getPrices().put(Category.OUTER, 6200);
        brandC.getPrices().put(Category.PANTS, 3300);
        brandC.getPrices().put(Category.SNEAKERS, 9200);
        brandC.getPrices().put(Category.BAG, 2200);
        brandC.getPrices().put(Category.HAT, 1900);
        brandC.getPrices().put(Category.SOCKS, 2200);
        brandC.getPrices().put(Category.ACCESSORY, 2100);

        allBrands = Arrays.asList(brandA, brandB, brandC);
    }

    @Test
    @DisplayName("모든 브랜드 조회")
    void getAllBrands_ShouldReturnAllBrands() {
        // given
        when(brandRepository.findAll()).thenReturn(allBrands);

        // when
        List<Brand> result = brandService.getAllBrands();

        // then
        assertEquals(3, result.size());
        assertEquals("A", result.get(0).getName());
        assertEquals("B", result.get(1).getName());
        assertEquals("C", result.get(2).getName());
    }

    @Test
    @DisplayName("ID로 브랜드 조회 - 존재하는 경우")
    void getBrandById_ShouldReturnBrandWhenExists() {
        // given
        when(brandRepository.findById(1L)).thenReturn(Optional.of(brandA));

        // when
        Brand result = brandService.getBrandById(1L);

        // then
        assertNotNull(result);
        assertEquals("A", result.getName());
    }

    @Test
    @DisplayName("ID로 브랜드 조회 - 존재하지 않는 경우")
    void getBrandById_ShouldReturnNullWhenNotExists() {
        // given
        when(brandRepository.findById(99L)).thenReturn(Optional.empty());

        // when
        Brand result = brandService.getBrandById(99L);

        // then
        assertNull(result);
    }

    @Test
    @DisplayName("이름으로 브랜드 조회")
    void getBrandByName_ShouldReturnBrandWhenExists() {
        // given
        when(brandRepository.findByName("A")).thenReturn(brandA);

        // when
        Brand result = brandService.getBrandByName("A");

        // then
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("브랜드 저장")
    void saveBrand_ShouldSaveBrand() {
        // given
        Brand newBrand = new Brand();
        newBrand.setName("D");
        when(brandRepository.save(any(Brand.class))).thenReturn(newBrand);

        // when
        Brand result = brandService.saveBrand(newBrand);

        // then
        assertNotNull(result);
        assertEquals("D", result.getName());
        verify(brandRepository, times(1)).save(newBrand);
    }

    @Test
    @DisplayName("브랜드 삭제")
    void deleteBrand_ShouldDeleteBrand() {
        // given
        Long brandId = 1L;

        // when
        brandService.deleteBrand(brandId);

        // then
        verify(brandRepository, times(1)).deleteById(brandId);
    }

    @Test
    @DisplayName("특정 카테고리의 최저가격 브랜드 조회")
    void getBrandsByCategoryOrderByPriceAsc_ShouldReturnBrandsOrderedByPrice() {
        // given
        when(brandRepository.findAllByCategoryOrderByPriceAsc(Category.TOP))
                .thenReturn(Arrays.asList(brandC, brandB, brandA)); // C의 TOP 가격이 가장 낮음

        // when
        List<Brand> result = brandService.getBrandsByCategoryOrderByPriceAsc(Category.TOP);

        // then
        assertEquals(3, result.size());
        assertEquals("C", result.get(0).getName()); // 최저가
        assertEquals("B", result.get(1).getName());
        assertEquals("A", result.get(2).getName()); // 최고가
    }

    @Test
    @DisplayName("특정 카테고리의 최저가격 확인")
    void testGetLowestPriceForCategory() {
        // given
        when(brandRepository.findAllByCategoryOrderByPriceAsc(Category.TOP))
                .thenReturn(Arrays.asList(brandC, brandB, brandA)); // C의 TOP 가격이 가장 낮음

        // when - 카테고리별 최저가격 조회를 간접적으로 테스트
        List<Brand> brandsForCategory = brandService.getBrandsByCategoryOrderByPriceAsc(Category.TOP);
        Brand lowestPriceBrand = brandsForCategory.get(0);

        // then
        assertEquals("C", lowestPriceBrand.getName());
        assertEquals(10000, lowestPriceBrand.getPrices().get(Category.TOP));
    }

    @Test
    @DisplayName("동일 최저가 브랜드 처리 확인")
    void testHandlingSamePriceBrands() {
        // given
        Brand brandA_copy = new Brand();
        brandA_copy.setId(1L);
        brandA_copy.setName("A");
        brandA_copy.getPrices().put(Category.SNEAKERS, 9000);

        Brand brandG = new Brand();
        brandG.setId(7L);
        brandG.setName("G");
        brandG.getPrices().put(Category.SNEAKERS, 9000); // A와 동일한 가격

        when(brandRepository.findAllByCategoryOrderByPriceAsc(Category.SNEAKERS))
                .thenReturn(Arrays.asList(brandA_copy, brandG, brandB)); // A와 G가 같은 가격으로 최저

        // when
        List<Brand> brandsForCategory = brandService.getBrandsByCategoryOrderByPriceAsc(Category.SNEAKERS);

        // then
        assertEquals(2, brandsForCategory.stream()
                .filter(b -> b.getPrices().get(Category.SNEAKERS) == 9000)
                .count());

        List<String> lowestPriceBrandNames = Arrays.asList(
                brandsForCategory.get(0).getName(),
                brandsForCategory.get(1).getName()
        );

        assertTrue(lowestPriceBrandNames.contains("A"));
        assertTrue(lowestPriceBrandNames.contains("G"));
    }

    @Test
    @DisplayName("단일 브랜드 최저 총액 조회 - API 2")
    void getLowestTotalPriceBrand_ShouldReturnBrandWithLowestTotalPrice() {
        // given
        when(brandRepository.findAll()).thenReturn(allBrands);

        // when
        Map<String, Object> result = brandService.getLowestTotalPriceBrand();

        // then
        assertNotNull(result);
        Map<String, Object> lowestPrice = (Map<String, Object>) result.get("최저가");
        assertNotNull(lowestPrice);

        // 필드 존재 확인
        assertNotNull(lowestPrice.get("브랜드"));
        assertNotNull(lowestPrice.get("카테고리"));
        assertNotNull(lowestPrice.get("총액"));

        // 카테고리 개수 확인
        List<Map<String, String>> categories = (List<Map<String, String>>) lowestPrice.get("카테고리");
        assertEquals(8, categories.size());
    }

    @Test
    @DisplayName("카테고리별 최저/최고 가격 브랜드 조회 - API 3")
    void getMinMaxPriceByCategory_ShouldReturnMinAndMaxPriceForCategory() {
        // given
        when(brandRepository.findAllByCategoryOrderByPriceAsc(Category.TOP))
                .thenReturn(Arrays.asList(brandC, brandB, brandA)); // 최저가: C

        when(brandRepository.findAllByCategoryOrderByPriceDesc(Category.TOP))
                .thenReturn(Arrays.asList(brandA, brandB, brandC)); // 최고가: A

        // when
        Map<String, Object> result = brandService.getMinMaxPriceByCategory(Category.TOP);

        // then
        assertNotNull(result);
        assertEquals("상의", result.get("카테고리"));

        List<Map<String, String>> minPrice = (List<Map<String, String>>) result.get("최저가");
        List<Map<String, String>> maxPrice = (List<Map<String, String>>) result.get("최고가");

        assertNotNull(minPrice);
        assertNotNull(maxPrice);

        assertEquals("C", minPrice.get(0).get("브랜드"));
        assertTrue(minPrice.get(0).get("가격").contains("10,000") ||
                minPrice.get(0).get("가격").equals("10000"));

        assertEquals("A", maxPrice.get(0).get("브랜드"));
        assertTrue(maxPrice.get(0).get("가격").contains("11,200") ||
                maxPrice.get(0).get("가격").equals("11200"));
    }

    @Test
    @DisplayName("브랜드 가격 업데이트 - 존재하는 브랜드")
    void updateBrandPrice_ShouldUpdatePriceWhenBrandExists() {
        // given
        when(brandRepository.findByName("A")).thenReturn(brandA);
        when(brandRepository.save(any(Brand.class))).thenReturn(brandA);

        // when
        Brand result = brandService.updateBrandPrice("A", Category.TOP, 12000);

        // then
        assertNotNull(result);
        assertEquals(12000, result.getPrices().get(Category.TOP));
        verify(brandRepository, times(1)).save(brandA);
    }

    @Test
    @DisplayName("브랜드 가격 업데이트 - 존재하지 않는 브랜드")
    void updateBrandPrice_ShouldReturnNullWhenBrandNotExists() {
        // given
        when(brandRepository.findByName("Z")).thenReturn(null);

        // when
        Brand result = brandService.updateBrandPrice("Z", Category.TOP, 12000);

        // then
        assertNull(result);
        verify(brandRepository, never()).save(any(Brand.class));
    }
}