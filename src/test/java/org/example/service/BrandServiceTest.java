package org.example.service;

import org.example.model.Brand;
import org.example.model.Category;
import org.example.repository.BrandRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.stream.Collectors;

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
        setupTestBrands();
        allBrands = Arrays.asList(brandA, brandB, brandC);
    }

    /**
     * 테스트 브랜드 데이터 설정
     */
    private void setupTestBrands() {
        brandA = createBrand(1L, "A", 11200, 5500, 4200, 9000, 2000, 1700, 1800, 2300);
        brandB = createBrand(2L, "B", 10500, 5900, 3800, 9100, 2100, 2000, 2000, 2200);
        brandC = createBrand(3L, "C", 10000, 6200, 3300, 9200, 2200, 1900, 2200, 2100);
    }

    /**
     * 브랜드 생성 헬퍼 메서드
     */
    private Brand createBrand(Long id, String name, int top, int outer, int pants, int sneakers,
                              int bag, int hat, int socks, int accessory) {
        Brand brand = new Brand();
        brand.setId(id);
        brand.setName(name);

        brand.getPrices().put(Category.TOP, top);
        brand.getPrices().put(Category.OUTER, outer);
        brand.getPrices().put(Category.PANTS, pants);
        brand.getPrices().put(Category.SNEAKERS, sneakers);
        brand.getPrices().put(Category.BAG, bag);
        brand.getPrices().put(Category.HAT, hat);
        brand.getPrices().put(Category.SOCKS, socks);
        brand.getPrices().put(Category.ACCESSORY, accessory);

        return brand;
    }

    @Test
    @DisplayName("모든 브랜드 조회")
    void getAllBrands_ShouldReturnAllBrands() {
        // given
        when(brandRepository.findAll()).thenReturn(allBrands);

        // when
        List<Brand> result = brandService.getAllBrands();

        // then
        assertEquals(3, result.size(), "브랜드 개수가 3개여야 합니다");
        assertEquals("A", result.get(0).getName(), "첫 번째 브랜드명이 A여야 합니다");
        assertEquals("B", result.get(1).getName(), "두 번째 브랜드명이 B여야 합니다");
        assertEquals("C", result.get(2).getName(), "세 번째 브랜드명이 C여야 합니다");

        verify(brandRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("ID로 브랜드 조회 - 존재하는 경우")
    void getBrandById_ShouldReturnBrandWhenExists() {
        // given
        when(brandRepository.findById(1L)).thenReturn(Optional.of(brandA));

        // when
        Brand result = brandService.getBrandById(1L);

        // then
        assertNotNull(result, "브랜드가 null이 아니어야 합니다");
        assertEquals("A", result.getName(), "브랜드명이 A여야 합니다");
        assertEquals(1L, result.getId(), "브랜드 ID가 1이어야 합니다");

        verify(brandRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("ID로 브랜드 조회 - 존재하지 않는 경우")
    void getBrandById_ShouldReturnNullWhenNotExists() {
        // given
        when(brandRepository.findById(99L)).thenReturn(Optional.empty());

        // when
        Brand result = brandService.getBrandById(99L);

        // then
        assertNull(result, "존재하지 않는 ID로 조회 시 null을 반환해야 합니다");

        verify(brandRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("이름으로 브랜드 조회")
    void getBrandByName_ShouldReturnBrandWhenExists() {
        // given
        when(brandRepository.findByName("A")).thenReturn(brandA);

        // when
        Brand result = brandService.getBrandByName("A");

        // then
        assertNotNull(result, "브랜드가 null이 아니어야 합니다");
        assertEquals(1L, result.getId(), "브랜드 ID가 1이어야 합니다");
        assertEquals("A", result.getName(), "브랜드명이 A여야 합니다");

        verify(brandRepository, times(1)).findByName("A");
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
        assertNotNull(result, "저장된 브랜드가 null이 아니어야 합니다");
        assertEquals("D", result.getName(), "저장된 브랜드명이 D여야 합니다");

        verify(brandRepository, times(1)).save(newBrand);
    }

    @Test
    @DisplayName("브랜드 삭제")
    void deleteBrand_ShouldDeleteBrand() {
        // given
        Long brandId = 1L;
        doNothing().when(brandRepository).deleteById(brandId);

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
        assertEquals(3, result.size(), "결과 목록의 크기가 3이어야 합니다");
        assertEquals("C", result.get(0).getName(), "첫 번째(최저가) 브랜드명이 C여야 합니다");
        assertEquals("B", result.get(1).getName(), "두 번째 브랜드명이 B여야 합니다");
        assertEquals("A", result.get(2).getName(), "세 번째(최고가) 브랜드명이 A여야 합니다");

        verify(brandRepository, times(1)).findAllByCategoryOrderByPriceAsc(Category.TOP);
    }

    @Test
    @DisplayName("특정 카테고리의 최고가격 브랜드 조회")
    void getBrandsByCategoryOrderByPriceDesc_ShouldReturnBrandsOrderedByPrice() {
        // given
        when(brandRepository.findAllByCategoryOrderByPriceDesc(Category.TOP))
                .thenReturn(Arrays.asList(brandA, brandB, brandC)); // A의 TOP 가격이 가장 높음

        // when
        List<Brand> result = brandService.getBrandsByCategoryOrderByPriceDesc(Category.TOP);

        // then
        assertEquals(3, result.size(), "결과 목록의 크기가 3이어야 합니다");
        assertEquals("A", result.get(0).getName(), "첫 번째(최고가) 브랜드명이 A여야 합니다");
        assertEquals("B", result.get(1).getName(), "두 번째 브랜드명이 B여야 합니다");
        assertEquals("C", result.get(2).getName(), "세 번째(최저가) 브랜드명이 C여야 합니다");

        verify(brandRepository, times(1)).findAllByCategoryOrderByPriceDesc(Category.TOP);
    }

    @Test
    @DisplayName("API 1: 카테고리별 최저가격 조회")
    void getLowestPriceByCategory_ShouldReturnLowestPriceForEachCategory() {
        // 각 카테고리별로 최저가 브랜드 모킹
        for (Category category : Category.values()) {
            List<Brand> brandsForCategory;

            // 카테고리별로 다른 최저가 브랜드 설정
            if (category == Category.TOP) {
                brandsForCategory = Collections.singletonList(brandC); // C의 TOP 가격이 가장 낮음
            } else if (category == Category.PANTS) {
                // 동일한 최저가격을 가진 브랜드가 여러 개인 경우
                Brand brandB_copy = createBrand(2L, "B", 10500, 5900, 3300, 9100, 2100, 2000, 2000, 2200);
                brandsForCategory = Arrays.asList(brandB_copy, brandC); // B와 C가 동일한 PANTS 가격
            } else {
                brandsForCategory = Collections.singletonList(brandA); // 다른 카테고리는 A가 가장 낮음
            }

            when(brandRepository.findAllByCategoryOrderByPriceAsc(category)).thenReturn(brandsForCategory);
        }

        // when
        Map<Category, Map<String, Object>> result = brandService.getLowestPriceByCategory();

        // then
        assertNotNull(result, "결과가 null이 아니어야 합니다");
        assertEquals(8, result.size(), "결과 맵의 크기가 8이어야 합니다(8개 카테고리)");

        // TOP 카테고리 검증
        Map<String, Object> topCategoryData = result.get(Category.TOP);
        assertNotNull(topCategoryData, "TOP 카테고리 데이터가 null이 아니어야 합니다");
        assertEquals("C", topCategoryData.get("brand"), "TOP 카테고리의 최저가 브랜드는 C여야 합니다");

        // PANTS 카테고리 검증 (동일 가격의 브랜드가 여러 개인 경우)
        Map<String, Object> pantsCategoryData = result.get(Category.PANTS);
        assertNotNull(pantsCategoryData, "PANTS 카테고리 데이터가 null이 아니어야 합니다");
        String pantsBrands = (String) pantsCategoryData.get("brand");
        assertTrue(pantsBrands.contains("B") && pantsBrands.contains("C"),
                "PANTS 카테고리의 최저가 브랜드는 B와 C를 포함해야 합니다");

        // 모든 카테고리에 대해 findAllByCategoryOrderByPriceAsc가 호출되었는지 확인
        for (Category category : Category.values()) {
            verify(brandRepository, times(1)).findAllByCategoryOrderByPriceAsc(category);
        }
    }

    @Test
    @DisplayName("API 2: 단일 브랜드 최저 총액 조회")
    void getLowestTotalPriceBrand_ShouldReturnBrandWithLowestTotalPrice() {
        // given
        when(brandRepository.findAll()).thenReturn(allBrands);

        // when
        Map<String, Object> result = brandService.getLowestTotalPriceBrand();

        // then
        assertNotNull(result, "결과가 null이 아니어야 합니다");
        assertTrue(result.containsKey("최저가"), "결과 맵에 '최저가' 키가 있어야 합니다");

        Map<String, Object> lowestPrice = (Map<String, Object>) result.get("최저가");
        assertNotNull(lowestPrice, "최저가 맵이 null이 아니어야 합니다");

        // 필수 필드 검증
        assertTrue(lowestPrice.containsKey("브랜드"), "최저가 맵에 '브랜드' 키가 있어야 합니다");
        assertTrue(lowestPrice.containsKey("카테고리"), "최저가 맵에 '카테고리' 키가 있어야 합니다");
        assertTrue(lowestPrice.containsKey("총액"), "최저가 맵에 '총액' 키가 있어야 합니다");

        // 카테고리 목록 검증
        List<Map<String, String>> categories = (List<Map<String, String>>) lowestPrice.get("카테고리");
        assertEquals(8, categories.size(), "카테고리 목록의 크기가 8이어야 합니다(8개 카테고리)");

        // 각 카테고리 맵의 구조 검증
        for (Map<String, String> category : categories) {
            assertTrue(category.containsKey("카테고리"), "카테고리 맵에 '카테고리' 키가 있어야 합니다");
            assertTrue(category.containsKey("가격"), "카테고리 맵에 '가격' 키가 있어야 합니다");
        }

        verify(brandRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("API 2: 브랜드별 총액 계산 및 최저가 브랜드 선택 로직 검증")
    void getLowestTotalPriceBrand_ShouldSelectCorrectBrand() {
        // given
        // 각 브랜드의 실제 총액을 계산:
        // 브랜드 A의 총액: 11200 + 5500 + 4200 + 9000 + 2000 + 1700 + 1800 + 2300 = 37700
        // 브랜드 B의 총액: 10500 + 5900 + 3800 + 9100 + 2100 + 2000 + 2000 + 2200 = 37600
        // 브랜드 C의 총액: 10000 + 6200 + 3300 + 9200 + 2200 + 1900 + 2200 + 2100 = 37100 (최저가)
        when(brandRepository.findAll()).thenReturn(allBrands);

        // when
        Map<String, Object> result = brandService.getLowestTotalPriceBrand();

        // then
        Map<String, Object> lowestPrice = (Map<String, Object>) result.get("최저가");
        assertEquals("C", lowestPrice.get("브랜드"), "최저 총액 브랜드는 C여야 합니다");

        // 총액 문자열에서 콤마 제거 후 숫자로 변환
        String totalPriceStr = (String) lowestPrice.get("총액");
        int totalPrice = Integer.parseInt(totalPriceStr.replace(",", ""));
        assertEquals(37100, totalPrice, "최저 총액은 37,100이어야 합니다");

        verify(brandRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("API 3: 카테고리별 최저/최고 가격 브랜드 조회")
    void getMinMaxPriceByCategory_ShouldReturnMinAndMaxPriceForCategory() {
        // given
        when(brandRepository.findAllByCategoryOrderByPriceAsc(Category.TOP))
                .thenReturn(Arrays.asList(brandC, brandB, brandA)); // 최저가: C

        when(brandRepository.findAllByCategoryOrderByPriceDesc(Category.TOP))
                .thenReturn(Arrays.asList(brandA, brandB, brandC)); // 최고가: A

        // when
        Map<String, Object> result = brandService.getMinMaxPriceByCategory(Category.TOP);

        // then
        assertNotNull(result, "결과가 null이 아니어야 합니다");
        assertEquals("상의", result.get("카테고리"), "카테고리명이 '상의'여야 합니다");

        // 최저가 검증
        List<Map<String, String>> minPrice = (List<Map<String, String>>) result.get("최저가");
        assertNotNull(minPrice, "최저가 목록이 null이 아니어야 합니다");
        assertFalse(minPrice.isEmpty(), "최저가 목록이 비어있지 않아야 합니다");
        assertEquals("C", minPrice.get(0).get("브랜드"), "최저가 브랜드는 C여야 합니다");

        // 가격 문자열에서 콤마 제거 후 숫자로 변환
        String minPriceStr = minPrice.get(0).get("가격").replace(",", "");
        int minPriceValue = Integer.parseInt(minPriceStr);
        assertEquals(10000, minPriceValue, "최저가는 10,000이어야 합니다");

        // 최고가 검증
        List<Map<String, String>> maxPrice = (List<Map<String, String>>) result.get("최고가");
        assertNotNull(maxPrice, "최고가 목록이 null이 아니어야 합니다");
        assertFalse(maxPrice.isEmpty(), "최고가 목록이 비어있지 않아야 합니다");
        assertEquals("A", maxPrice.get(0).get("브랜드"), "최고가 브랜드는 A여야 합니다");

        // 가격 문자열에서 콤마 제거 후 숫자로 변환
        String maxPriceStr = maxPrice.get(0).get("가격").replace(",", "");
        int maxPriceValue = Integer.parseInt(maxPriceStr);
        assertEquals(11200, maxPriceValue, "최고가는 11,200이어야 합니다");

        verify(brandRepository, times(1)).findAllByCategoryOrderByPriceAsc(Category.TOP);
        verify(brandRepository, times(1)).findAllByCategoryOrderByPriceDesc(Category.TOP);
    }

    @Test
    @DisplayName("API 3: 동일 최저가 브랜드 처리 확인")
    void getMinMaxPriceByCategory_ShouldHandleSamePriceBrands() {
        // given
        // 최저가가 동일한 브랜드 (B와 C)
        Brand brandB_copy = createBrand(2L, "B", 10000, 5900, 3800, 9100, 2100, 2000, 2000, 2200);
        Brand brandC_copy = createBrand(3L, "C", 10000, 6200, 3300, 9200, 2200, 1900, 2200, 2100);
        when(brandRepository.findAllByCategoryOrderByPriceAsc(Category.TOP))
                .thenReturn(Arrays.asList(brandB_copy, brandC_copy, brandA));

        // 최고가가 동일한 브랜드 (A와 또 다른 브랜드 D)
        Brand brandA_copy = createBrand(1L, "A", 11200, 5500, 4200, 9000, 2000, 1700, 1800, 2300);
        Brand brandD = createBrand(4L, "D", 11200, 5100, 3000, 9500, 2500, 1500, 2400, 2000);
        when(brandRepository.findAllByCategoryOrderByPriceDesc(Category.TOP))
                .thenReturn(Arrays.asList(brandA_copy, brandD, brandB_copy));

        // when
        Map<String, Object> result = brandService.getMinMaxPriceByCategory(Category.TOP);

        // then
        // 최저가 브랜드 리스트 검증 (B와 C가 모두 포함되어야 함)
        List<Map<String, String>> minPrice = (List<Map<String, String>>) result.get("최저가");
        assertEquals(2, minPrice.size(), "최저가 브랜드 목록의 크기가 2여야 합니다");

        List<String> minPriceBrands = minPrice.stream()
                .map(map -> map.get("브랜드"))
                .collect(Collectors.toList());
        assertTrue(minPriceBrands.contains("B"), "최저가 브랜드 목록에 B가 포함되어야 합니다");
        assertTrue(minPriceBrands.contains("C"), "최저가 브랜드 목록에 C가 포함되어야 합니다");

        // 최고가 브랜드 리스트 검증 (A와 D가 모두 포함되어야 함)
        List<Map<String, String>> maxPrice = (List<Map<String, String>>) result.get("최고가");
        assertEquals(2, maxPrice.size(), "최고가 브랜드 목록의 크기가 2여야 합니다");

        List<String> maxPriceBrands = maxPrice.stream()
                .map(map -> map.get("브랜드"))
                .collect(Collectors.toList());
        assertTrue(maxPriceBrands.contains("A"), "최고가 브랜드 목록에 A가 포함되어야 합니다");
        assertTrue(maxPriceBrands.contains("D"), "최고가 브랜드 목록에 D가 포함되어야 합니다");
    }

    @Test
    @DisplayName("API 4: 브랜드 가격 업데이트 - 존재하는 브랜드")
    void updateBrandPrice_ShouldUpdatePriceWhenBrandExists() {
        // given
        when(brandRepository.findByName("A")).thenReturn(brandA);
        when(brandRepository.save(any(Brand.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        Brand result = brandService.updateBrandPrice("A", Category.TOP, 12000);

        // then
        assertNotNull(result, "결과가 null이 아니어야 합니다");
        assertEquals(12000, result.getPrices().get(Category.TOP), "TOP 카테고리 가격이 12,000으로 업데이트되어야 합니다");

        // 아규먼트 캡처를 통해 저장된 브랜드 객체 검증
        ArgumentCaptor<Brand> brandCaptor = ArgumentCaptor.forClass(Brand.class);
        verify(brandRepository, times(1)).save(brandCaptor.capture());
        Brand savedBrand = brandCaptor.getValue();

        assertEquals("A", savedBrand.getName(), "저장된 브랜드명이 A여야 합니다");
        assertEquals(12000, savedBrand.getPrices().get(Category.TOP), "저장된 브랜드의 TOP 카테고리 가격이 12,000이어야 합니다");

        // 다른 카테고리 가격은 변경되지 않아야 함
        assertEquals(5500, savedBrand.getPrices().get(Category.OUTER), "OUTER 카테고리 가격이 변경되지 않아야 합니다");
    }

    @Test
    @DisplayName("API 4: 브랜드 가격 업데이트 - 존재하지 않는 브랜드")
    void updateBrandPrice_ShouldReturnNullWhenBrandNotExists() {
        // given
        when(brandRepository.findByName("Z")).thenReturn(null);

        // when
        Brand result = brandService.updateBrandPrice("Z", Category.TOP, 12000);

        // then
        assertNull(result, "존재하지 않는 브랜드 이름으로 가격 업데이트 시 null을 반환해야 합니다");

        verify(brandRepository, never()).save(any(Brand.class));
    }

    @Test
    @DisplayName("브랜드 초기화 - 설정된 브랜드가 없는 경우")
    void initializeBrands_ShouldCreateBrandsWhenNoneExist() {
        // given
        when(brandRepository.count()).thenReturn(0L);
        when(brandRepository.save(any(Brand.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        brandService.initializeBrands();

        // then
        // 9개 브랜드(A-I)가 생성되어야 함
        verify(brandRepository, times(9)).save(any(Brand.class));
    }

    @Test
    @DisplayName("브랜드 초기화 - 이미 브랜드가 있는 경우")
    void initializeBrands_ShouldNotCreateBrandsWhenAlreadyExist() {
        // given
        when(brandRepository.count()).thenReturn(9L);

        // when
        brandService.initializeBrands();

        // then
        // 이미 브랜드가 있으므로 save가 호출되지 않아야 함
        verify(brandRepository, never()).save(any(Brand.class));
    }
}