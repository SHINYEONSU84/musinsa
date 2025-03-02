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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

        // 카테고리별 최저가격 설정
        lowestPriceByCategory = new HashMap<>();
        for (Category category : Category.values()) {
            Map<String, Object> categoryData = new HashMap<>();

            if (category == Category.SNEAKERS) {
                // 스니커즈는 A,G 브랜드가 모두 최저가
                categoryData.put("brand", "A,G");
            } else {
                categoryData.put("brand", "C");
            }

            categoryData.put("price", 10000);
            lowestPriceByCategory.put(category, categoryData);
        }

        // 최저 총액 브랜드 설정
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

        // 카테고리별 최저/최고 가격 설정
        minMaxPriceByCategory = new HashMap<>();
        minMaxPriceByCategory.put("카테고리", "상의");

        List<Map<String, String>> minPriceList = new ArrayList<>();
        Map<String, String> minPriceInfo = new HashMap<>();
        minPriceInfo.put("브랜드", "C");
        minPriceInfo.put("가격", "10,000");
        minPriceList.add(minPriceInfo);
        minMaxPriceByCategory.put("최저가", minPriceList);

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
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categories").isArray())
                .andExpect(jsonPath("$.totalPrice").exists());
    }

    @Test
    @DisplayName("API 2: 단일 브랜드 최저 총액 조회")
    void getLowestTotalPriceBrand_ShouldReturnBrandWithLowestTotalPrice() throws Exception {
        // given
        when(brandService.getLowestTotalPriceBrand()).thenReturn(lowestTotalPriceBrand);

        // when & then
        mockMvc.perform(get("/api/lowest-total-price-brand"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.최저가.브랜드").value("D"))
                .andExpect(jsonPath("$.최저가.카테고리").isArray())
                .andExpect(jsonPath("$.최저가.총액").value("36,100"));
    }

    @Test
    @DisplayName("API 3: 카테고리별 최저/최고 가격 브랜드 조회")
    void getMinMaxPriceByCategory_ShouldReturnMinAndMaxPriceForCategory() throws Exception {
        // given
        when(brandService.getMinMaxPriceByCategory(Category.TOP)).thenReturn(minMaxPriceByCategory);

        // when & then
        mockMvc.perform(get("/api/min-max-price-by-category?categoryName=상의"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.카테고리").value("상의"))
                .andExpect(jsonPath("$.최저가[0].브랜드").value("C"))
                .andExpect(jsonPath("$.최저가[0].가격").value("10,000"))
                .andExpect(jsonPath("$.최고가[0].브랜드").value("I"))
                .andExpect(jsonPath("$.최고가[0].가격").value("11,400"));
    }

    @Test
    @DisplayName("API 3: 잘못된 카테고리명으로 조회 시 오류 반환")
    void getMinMaxPriceByCategory_ShouldReturnErrorForInvalidCategory() throws Exception {
        // given
        when(brandService.getMinMaxPriceByCategory(any())).thenThrow(new IllegalArgumentException("잘못된 카테고리 이름"));

        // when & then
        mockMvc.perform(get("/api/min-max-price-by-category?categoryName=잘못된카테고리"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("API 4: 브랜드 생성")
    void createBrand_ShouldCreateNewBrand() throws Exception {
        // given
        BrandDto brandDto = new BrandDto();
        brandDto.setName("New Brand");
        Map<Category, Integer> prices = new HashMap<>();
        prices.put(Category.TOP, 10000);
        prices.put(Category.OUTER, 5000);
        brandDto.setPrices(prices);

        Brand savedBrand = new Brand();
        savedBrand.setId(3L);
        savedBrand.setName("New Brand");
        savedBrand.setPrices(prices);

        when(brandService.saveBrand(any(Brand.class))).thenReturn(savedBrand);

        // when & then
        mockMvc.perform(post("/api/brand")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(brandDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.brandId").value("3"));
    }

    @Test
    @DisplayName("API 4: 브랜드 수정")
    void updateBrand_ShouldUpdateExistingBrand() throws Exception {
        // given
        Long brandId = 1L;
        BrandDto brandDto = new BrandDto();
        brandDto.setName("Updated Brand");
        Map<Category, Integer> prices = new HashMap<>();
        prices.put(Category.TOP, 10000);
        prices.put(Category.OUTER, 5000);
        brandDto.setPrices(prices);

        Brand existingBrand = new Brand();
        existingBrand.setId(brandId);
        existingBrand.setName("A");

        Brand updatedBrand = new Brand();
        updatedBrand.setId(brandId);
        updatedBrand.setName("Updated Brand");
        updatedBrand.setPrices(prices);

        when(brandService.getBrandById(brandId)).thenReturn(existingBrand);
        when(brandService.saveBrand(any(Brand.class))).thenReturn(updatedBrand);

        // when & then
        mockMvc.perform(put("/api/brand/{id}", brandId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(brandDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    @DisplayName("API 4: 존재하지 않는 브랜드 수정 시 오류 반환")
    void updateBrand_ShouldReturnErrorForNonExistingBrand() throws Exception {
        // given
        Long brandId = 999L;
        BrandDto brandDto = new BrandDto();
        brandDto.setName("Non Existing Brand");

        when(brandService.getBrandById(brandId)).thenReturn(null);

        // when & then
        mockMvc.perform(put("/api/brand/{id}", brandId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(brandDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("API 4: 브랜드 삭제")
    void deleteBrand_ShouldDeleteExistingBrand() throws Exception {
        // given
        Long brandId = 1L;
        when(brandService.getBrandById(brandId)).thenReturn(brandA);

        // when & then
        mockMvc.perform(delete("/api/brand/{id}", brandId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    @DisplayName("API 4: 존재하지 않는 브랜드 삭제 시 오류 반환")
    void deleteBrand_ShouldReturnErrorForNonExistingBrand() throws Exception {
        // given
        Long brandId = 999L;
        when(brandService.getBrandById(brandId)).thenReturn(null);

        // when & then
        mockMvc.perform(delete("/api/brand/{id}", brandId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
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
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
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
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }
}