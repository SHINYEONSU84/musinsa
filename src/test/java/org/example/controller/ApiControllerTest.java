package org.example.controller;

import org.example.dto.BrandDto;
import org.example.dto.BrandPriceUpdateDto;
import org.example.model.Brand;
import org.example.model.Category;
import org.example.service.BrandService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ApiController.class)
public class ApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BrandService brandService;

    @Autowired
    private ObjectMapper objectMapper;

    private Brand brandA;
    private Map<Category, Map<String, Object>> lowestPriceByCategory;
    private Map<String, Object> lowestTotalPriceBrand;
    private Map<String, Object> minMaxPriceByCategory;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 설정
        setupTestBrands();
        setupLowestPriceByCategory();
        setupLowestTotalPriceBrand();
        setupMinMaxPriceByCategory();
    }

    /**
     * 테스트에서 사용할 브랜드 데이터 설정
     */
    private void setupTestBrands() {
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
    }

    /**
     * API 1: 카테고리별 최저가격 데이터 설정
     */
    private void setupLowestPriceByCategory() {
        lowestPriceByCategory = new HashMap<>();
        for (Category category : Category.values()) {
            Map<String, Object> categoryData = new HashMap<>();

            if (category == Category.SNEAKERS) {
                // 스니커즈는 A,G 브랜드가 모두 최저가 (동일 최저가 테스트)
                categoryData.put("brand", "A,G");
            } else {
                categoryData.put("brand", "C");
            }

            categoryData.put("price", 10000);
            lowestPriceByCategory.put(category, categoryData);
        }
    }

    /**
     * API 2: 최저 총액 브랜드 데이터 설정
     */
    private void setupLowestTotalPriceBrand() {
        lowestTotalPriceBrand = new LinkedHashMap<>();
        Map<String, Object> brandInfo = new LinkedHashMap<>();
        brandInfo.put("브랜드", "D");

        List<Map<String, String>> categoryPrices = new ArrayList<>();
        for (Category category : Category.values()) {
            Map<String, String> categoryPrice = new LinkedHashMap<>();
            categoryPrice.put("카테고리", category.getDisplayName());
            categoryPrice.put("가격", "5,000");
            categoryPrices.add(categoryPrice);
        }

        brandInfo.put("카테고리", categoryPrices);
        brandInfo.put("총액", "36,100");
        lowestTotalPriceBrand.put("최저가", brandInfo);
    }

    /**
     * API 3: 카테고리별 최저/최고 가격 데이터 설정
     */
    private void setupMinMaxPriceByCategory() {
        minMaxPriceByCategory = new HashMap<>();
        minMaxPriceByCategory.put("카테고리", "상의");

        // 최저가 정보
        List<Map<String, String>> minPriceList = new ArrayList<>();
        Map<String, String> minPriceInfo = new HashMap<>();
        minPriceInfo.put("브랜드", "C");
        minPriceInfo.put("가격", "10,000");
        minPriceList.add(minPriceInfo);
        minMaxPriceByCategory.put("최저가", minPriceList);

        // 최고가 정보
        List<Map<String, String>> maxPriceList = new ArrayList<>();
        Map<String, String> maxPriceInfo = new HashMap<>();
        maxPriceInfo.put("브랜드", "I");
        maxPriceInfo.put("가격", "11,400");
        maxPriceList.add(maxPriceInfo);
        minMaxPriceByCategory.put("최고가", maxPriceList);
    }

    @Test
    @DisplayName("API 1: 카테고리별 최저가격 브랜드와 상품가격 조회")
    void getLowestPriceByCategory_ShouldReturnLowestPriceForEachCategory() throws Exception {
        // given
        when(brandService.getLowestPriceByCategory()).thenReturn(lowestPriceByCategory);

        // when & then
        mockMvc.perform(get("/api/lowest-price-by-category"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categories").isArray())
                .andExpect(jsonPath("$.categories", hasSize(8))) // 8개 카테고리 확인
                .andExpect(jsonPath("$.totalPrice").exists())
                .andExpect(jsonPath("$.categories[0]", hasKey("category")))
                .andExpect(jsonPath("$.categories[0]", hasKey("brand")))
                .andExpect(jsonPath("$.categories[0]", hasKey("price")));

        // 서비스 메서드가 정확히 한 번 호출되었는지 확인
        verify(brandService, times(1)).getLowestPriceByCategory();
    }

    @Test
    @DisplayName("API 1: 서비스 예외 발생 시 오류 응답 반환")
    void getLowestPriceByCategory_ShouldReturnErrorWhenServiceThrowsException() throws Exception {
        // given
        when(brandService.getLowestPriceByCategory()).thenThrow(new RuntimeException("서비스 오류"));

        // when & then
        mockMvc.perform(get("/api/lowest-price-by-category"))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.message").value("서비스 오류"));
    }

    @Test
    @DisplayName("API 2: 단일 브랜드 최저 총액 조회")
    void getLowestTotalPriceBrand_ShouldReturnBrandWithLowestTotalPrice() throws Exception {
        // given
        when(brandService.getLowestTotalPriceBrand()).thenReturn(lowestTotalPriceBrand);

        // when & then
        mockMvc.perform(get("/api/lowest-total-price-brand"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.최저가.브랜드").value("D"))
                .andExpect(jsonPath("$.최저가.카테고리").isArray())
                .andExpect(jsonPath("$.최저가.카테고리", hasSize(8))) // 8개 카테고리 확인
                .andExpect(jsonPath("$.최저가.총액").value("36,100"));

        // 서비스 메서드가 정확히 한 번 호출되었는지 확인
        verify(brandService, times(1)).getLowestTotalPriceBrand();
    }

    @Test
    @DisplayName("API 2: 서비스 예외 발생 시 오류 응답 반환")
    void getLowestTotalPriceBrand_ShouldReturnErrorWhenServiceThrowsException() throws Exception {
        // given
        when(brandService.getLowestTotalPriceBrand()).thenThrow(new RuntimeException("서비스 오류"));

        // when & then
        mockMvc.perform(get("/api/lowest-total-price-brand"))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.message").value("서비스 오류"));
    }

    @ParameterizedTest
    @EnumSource(Category.class)
    @DisplayName("API 3: 카테고리별 최저/최고 가격 브랜드 조회 - 모든 카테고리 테스트")
    void getMinMaxPriceByCategory_ShouldReturnMinAndMaxPriceForAllCategories(Category category) throws Exception {
        // given
        // 테스트 데이터의 카테고리를 현재 테스트 중인 카테고리로 변경
        minMaxPriceByCategory.put("카테고리", category.getDisplayName());
        when(brandService.getMinMaxPriceByCategory(category)).thenReturn(minMaxPriceByCategory);

        // when & then
        mockMvc.perform(get("/api/min-max-price-by-category")
                        .param("categoryName", category.getDisplayName()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.카테고리").value(category.getDisplayName()))
                .andExpect(jsonPath("$.최저가").exists())
                .andExpect(jsonPath("$.최고가").exists());

        // 서비스 메서드가 정확히 한 번 호출되었는지 확인
        verify(brandService, times(1)).getMinMaxPriceByCategory(category);
    }

    @Test
    @DisplayName("API 3: 잘못된 카테고리명으로 조회 시 오류 반환")
    void getMinMaxPriceByCategory_ShouldReturnErrorForInvalidCategory() throws Exception {
        // given
        when(brandService.getMinMaxPriceByCategory(any()))
                .thenThrow(new IllegalArgumentException("잘못된 카테고리 이름: 존재하지않는카테고리"));

        // when & then
        mockMvc.perform(get("/api/min-max-price-by-category")
                        .param("categoryName", "존재하지않는카테고리"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("잘못된 카테고리 이름"))
                .andExpect(jsonPath("$.message").value(containsString("잘못된 카테고리 이름")));
    }

    @Test
    @DisplayName("API 4: 브랜드 생성")
    void createBrand_ShouldCreateNewBrand() throws Exception {
        // given
        BrandDto brandDto = createBrandDto("New Brand", 10000, 5000);
        Brand savedBrand = createBrand(3L, "New Brand", 10000, 5000);

        when(brandService.saveBrand(any(Brand.class))).thenReturn(savedBrand);

        // when & then
        ResultActions result = mockMvc.perform(post("/api/brand")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(brandDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.brandId").value("3"));

        // 서비스 메서드가 정확히 한 번 호출되었는지 확인
        verify(brandService, times(1)).saveBrand(any(Brand.class));
    }

    @Test
    @DisplayName("API 4: 브랜드 수정")
    void updateBrand_ShouldUpdateExistingBrand() throws Exception {
        // given
        Long brandId = 1L;
        BrandDto brandDto = createBrandDto("Updated Brand", 10000, 5000);
        Brand existingBrand = createBrand(brandId, "A", 11200, 5500);
        Brand updatedBrand = createBrand(brandId, "Updated Brand", 10000, 5000);

        when(brandService.getBrandById(brandId)).thenReturn(existingBrand);
        when(brandService.saveBrand(any(Brand.class))).thenReturn(updatedBrand);

        // when & then
        mockMvc.perform(put("/api/brand/{id}", brandId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(brandDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").exists());

        // 서비스 메서드 호출 확인
        verify(brandService, times(1)).getBrandById(brandId);
        verify(brandService, times(1)).saveBrand(any(Brand.class));
    }

    @Test
    @DisplayName("API 4: 존재하지 않는 브랜드 수정 시 오류 반환")
    void updateBrand_ShouldReturnErrorForNonExistingBrand() throws Exception {
        // given
        Long brandId = 999L;
        BrandDto brandDto = createBrandDto("Non Existing Brand", 10000, 5000);

        when(brandService.getBrandById(brandId)).thenReturn(null);

        // when & then
        mockMvc.perform(put("/api/brand/{id}", brandId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(brandDto)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.message").exists());

        // saveBrand가 호출되지 않아야 함
        verify(brandService, never()).saveBrand(any(Brand.class));
    }

    @Test
    @DisplayName("API 4: 브랜드 삭제")
    void deleteBrand_ShouldDeleteExistingBrand() throws Exception {
        // given
        Long brandId = 1L;
        when(brandService.getBrandById(brandId)).thenReturn(brandA);
        doNothing().when(brandService).deleteBrand(brandId);

        // when & then
        mockMvc.perform(delete("/api/brand/{id}", brandId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").exists());

        // 서비스 메서드 호출 확인
        verify(brandService, times(1)).getBrandById(brandId);
        verify(brandService, times(1)).deleteBrand(brandId);
    }

    @Test
    @DisplayName("API 4: 존재하지 않는 브랜드 삭제 시 오류 반환")
    void deleteBrand_ShouldReturnErrorForNonExistingBrand() throws Exception {
        // given
        Long brandId = 999L;
        when(brandService.getBrandById(brandId)).thenReturn(null);

        // when & then
        mockMvc.perform(delete("/api/brand/{id}", brandId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.message").exists());

        // deleteBrand가 호출되지 않아야 함
        verify(brandService, never()).deleteBrand(anyLong());
    }

    @Test
    @DisplayName("API 4: 브랜드 가격 업데이트")
    void updateBrandPrice_ShouldUpdatePrice() throws Exception {
        // given
        BrandPriceUpdateDto updateDto = new BrandPriceUpdateDto();
        updateDto.setBrandName("A");
        updateDto.setCategoryName("상의");
        updateDto.setPrice(12000);

        when(brandService.updateBrandPrice(eq("A"), eq(Category.TOP), eq(12000))).thenReturn(brandA);

        // when & then
        mockMvc.perform(put("/api/brand/price")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").exists());

        // 서비스 메서드 호출 확인
        verify(brandService, times(1)).updateBrandPrice(eq("A"), eq(Category.TOP), eq(12000));
    }

    @Test
    @DisplayName("API 4: 존재하지 않는 브랜드 가격 업데이트 시 오류 반환")
    void updateBrandPrice_ShouldReturnErrorForNonExistingBrand() throws Exception {
        // given
        BrandPriceUpdateDto updateDto = new BrandPriceUpdateDto();
        updateDto.setBrandName("Z");
        updateDto.setCategoryName("상의");
        updateDto.setPrice(12000);

        when(brandService.updateBrandPrice(eq("Z"), eq(Category.TOP), eq(12000))).thenReturn(null);

        // when & then
        mockMvc.perform(put("/api/brand/price")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("API 4: 잘못된 카테고리로 가격 업데이트 시 오류 반환")
    void updateBrandPrice_ShouldReturnErrorForInvalidCategory() throws Exception {
        // given
        BrandPriceUpdateDto updateDto = new BrandPriceUpdateDto();
        updateDto.setBrandName("A");
        updateDto.setCategoryName("존재하지않는카테고리");
        updateDto.setPrice(12000);

        when(brandService.updateBrandPrice(eq("A"), any(), eq(12000)))
                .thenThrow(new IllegalArgumentException("잘못된 카테고리 이름"));

        // when & then
        mockMvc.perform(put("/api/brand/price")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("잘못된 카테고리 이름"))
                .andExpect(jsonPath("$.message").exists());
    }

    /**
     * BrandDto 객체 생성 헬퍼 메서드
     */
    private BrandDto createBrandDto(String name, int topPrice, int outerPrice) {
        BrandDto brandDto = new BrandDto();
        brandDto.setName(name);

        Map<Category, Integer> prices = new HashMap<>();
        prices.put(Category.TOP, topPrice);
        prices.put(Category.OUTER, outerPrice);

        for (Category category : Category.values()) {
            if (!prices.containsKey(category)) {
                prices.put(category, 1000); // 기본 가격
            }
        }

        brandDto.setPrices(prices);
        return brandDto;
    }

    /**
     * Brand 객체 생성 헬퍼 메서드
     */
    private Brand createBrand(Long id, String name, int topPrice, int outerPrice) {
        Brand brand = new Brand();
        brand.setId(id);
        brand.setName(name);

        Map<Category, Integer> prices = new HashMap<>();
        prices.put(Category.TOP, topPrice);
        prices.put(Category.OUTER, outerPrice);

        for (Category category : Category.values()) {
            if (!prices.containsKey(category)) {
                prices.put(category, 1000); // 기본 가격
            }
        }

        brand.setPrices(prices);
        return brand;
    }
}