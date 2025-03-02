package org.example.controller;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.example.model.Brand;
import org.example.model.Category;
import org.example.service.BrandService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WebController.class)
public class WebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BrandService brandService;

    private Brand brandA;
    private Brand brandB;
    private List<Brand> allBrands;
    private Map<Category, Map<String, Object>> lowestPriceByCategory;
    private Map<String, Object> lowestTotalPriceBrand;
    private Map<String, Object> minMaxPriceByCategory;

    // 로그 레벨을 저장할 변수
    private Level originalLogLevel;

    @BeforeEach
    void setUp() {
        // WebController의 로그 레벨을 OFF로 설정 (테스트 중 로그 출력 방지)
        Logger logger = (Logger) LoggerFactory.getLogger(WebController.class);
        originalLogLevel = logger.getLevel();
        logger.setLevel(Level.OFF);

        // 테스트 데이터 설정
        setupTestBrands();
        setupLowestPriceByCategory();
        setupLowestTotalPriceBrand();
        setupMinMaxPriceByCategory();
    }

    @AfterEach
    void tearDown() {
        // 테스트 후 로그 레벨 복원
        Logger logger = (Logger) LoggerFactory.getLogger(WebController.class);
        logger.setLevel(originalLogLevel);
    }

    /**
     * 테스트 브랜드 데이터 설정
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

        allBrands = Arrays.asList(brandA, brandB);
    }

    /**
     * API 1: 카테고리별 최저가격 데이터 설정
     */
    private void setupLowestPriceByCategory() {
        lowestPriceByCategory = new HashMap<>();
        for (Category category : Category.values()) {
            Map<String, Object> categoryData = new HashMap<>();
            categoryData.put("brand", "C");
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
    @DisplayName("메인 페이지 접근")
    void home_ShouldReturnIndexPage() throws Exception {
        mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().size(0)); // 메인 페이지는 모델 속성이 없음
    }

    @Test
    @DisplayName("카테고리별 최저가격 페이지 접근")
    void lowestPriceByCategory_ShouldReturnCorrectViewAndData() throws Exception {
        // given
        when(brandService.getLowestPriceByCategory()).thenReturn(lowestPriceByCategory);

        // when & then
        MvcResult result = mockMvc.perform(get("/lowest-price-by-category"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("lowest-price-by-category"))
                .andExpect(model().attributeExists("categories"))
                .andExpect(model().attributeExists("totalPrice"))
                .andReturn();

        // 모델 속성 상세 검증
        List<Map<String, Object>> categories = (List<Map<String, Object>>) result.getModelAndView().getModel().get("categories");
        assertNotNull(categories, "카테고리 목록이 null입니다");
        assertEquals(8, categories.size(), "카테고리 개수가 8개여야 합니다");

        // 카테고리 속성 및 총액 검증
        String totalPrice = (String) result.getModelAndView().getModel().get("totalPrice");
        assertNotNull(totalPrice, "총액이 null입니다");
        assertTrue(totalPrice.contains(","), "총액은 천 단위 구분자(콤마)를 포함해야 합니다");

        // 서비스 메서드 호출 확인
        verify(brandService, times(1)).getLowestPriceByCategory();
    }

    @Test
    @DisplayName("카테고리별 최저가격 조회 실패 시 오류 페이지 반환")
    void lowestPriceByCategory_ShouldReturnErrorPageOnFailure() throws Exception {
        // given
        when(brandService.getLowestPriceByCategory()).thenThrow(new RuntimeException("테스트 오류"));

        // when & then
        mockMvc.perform(get("/lowest-price-by-category"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", containsString("테스트 오류")));

        verify(brandService, times(1)).getLowestPriceByCategory();
    }

    @Test
    @DisplayName("단일 브랜드 최저 총액 페이지 접근")
    void lowestTotalPriceBrand_ShouldReturnCorrectViewAndData() throws Exception {
        // given
        when(brandService.getLowestTotalPriceBrand()).thenReturn(lowestTotalPriceBrand);

        // when & then
        MvcResult result = mockMvc.perform(get("/lowest-total-price-brand"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("lowest-total-price-brand"))
                .andExpect(model().attributeExists("brandName"))
                .andExpect(model().attributeExists("categories"))
                .andExpect(model().attributeExists("totalPrice"))
                .andReturn();

        // 모델 속성 상세 검증
        String brandName = (String) result.getModelAndView().getModel().get("brandName");
        assertEquals("D", brandName, "브랜드명이 D여야 합니다");

        List<Map<String, String>> categories = (List<Map<String, String>>) result.getModelAndView().getModel().get("categories");
        assertNotNull(categories, "카테고리 목록이 null입니다");
        assertEquals(8, categories.size(), "카테고리 개수가 8개여야 합니다");

        String totalPrice = (String) result.getModelAndView().getModel().get("totalPrice");
        assertEquals("36,100", totalPrice, "총액이 36,100이어야 합니다");

        verify(brandService, times(1)).getLowestTotalPriceBrand();
    }

    @Test
    @DisplayName("단일 브랜드 최저 총액 조회 실패 시 오류 페이지 반환")
    void lowestTotalPriceBrand_ShouldReturnErrorPageOnFailure() throws Exception {
        // given
        when(brandService.getLowestTotalPriceBrand()).thenThrow(new RuntimeException("테스트 오류"));

        // when & then
        mockMvc.perform(get("/lowest-total-price-brand"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", containsString("테스트 오류")));

        verify(brandService, times(1)).getLowestTotalPriceBrand();
    }

    @Test
    @DisplayName("카테고리별 최저/최고 가격 폼 페이지 접근")
    void minMaxPriceByCategory_ShouldReturnFormPage() throws Exception {
        // when & then
        mockMvc.perform(get("/min-max-price-by-category"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("min-max-price-by-category"))
                .andExpect(model().attributeExists("categoryNames"));
    }

    @Test
    @DisplayName("카테고리별 최저/최고 가격 결과 페이지 접근")
    void minMaxPriceResult_ShouldReturnCorrectViewAndData() throws Exception {
        // given
        when(brandService.getMinMaxPriceByCategory(Category.TOP)).thenReturn(minMaxPriceByCategory);

        // when & then
        MvcResult result = mockMvc.perform(get("/min-max-price-result")
                        .param("categoryName", "상의"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("min-max-price-result"))
                .andExpect(model().attributeExists("categoryName"))
                .andExpect(model().attributeExists("minPrices"))
                .andExpect(model().attributeExists("maxPrices"))
                .andReturn();

        // 모델 속성 상세 검증
        String categoryName = (String) result.getModelAndView().getModel().get("categoryName");
        assertEquals("상의", categoryName, "카테고리명이 상의여야 합니다");

        List<Map<String, String>> minPrices = (List<Map<String, String>>) result.getModelAndView().getModel().get("minPrices");
        assertNotNull(minPrices, "최저가 목록이 null입니다");
        assertFalse(minPrices.isEmpty(), "최저가 목록이 비어있습니다");
        assertEquals("C", minPrices.get(0).get("브랜드"), "최저가 브랜드가 C여야 합니다");

        List<Map<String, String>> maxPrices = (List<Map<String, String>>) result.getModelAndView().getModel().get("maxPrices");
        assertNotNull(maxPrices, "최고가 목록이 null입니다");
        assertFalse(maxPrices.isEmpty(), "최고가 목록이 비어있습니다");
        assertEquals("I", maxPrices.get(0).get("브랜드"), "최고가 브랜드가 I여야 합니다");

        verify(brandService, times(1)).getMinMaxPriceByCategory(Category.TOP);
    }

    @Test
    @DisplayName("잘못된 카테고리 이름으로 최저/최고 가격 결과 조회 시 오류 페이지 반환")
    void minMaxPriceResult_ShouldReturnErrorPageForInvalidCategory() throws Exception {
        // 유효하지 않은 카테고리명을 사용하면 Category.fromDisplayName에서 예외가 발생함
        // WebController에서 예외를 캐치하여 오류 페이지를 반환

        // when & then
        MvcResult result = mockMvc.perform(get("/min-max-price-result")
                        .param("categoryName", "존재하지않는카테고리"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("error"))
                .andReturn();

        // 오류 메시지 검증
        String errorMsg = (String) result.getModelAndView().getModel().get("error");
        assertTrue(errorMsg.contains("잘못된 카테고리 이름"), "오류 메시지에 '잘못된 카테고리 이름'이 포함되어야 합니다");

        // brandService.getMinMaxPriceByCategory는 호출되지 않음
        verify(brandService, never()).getMinMaxPriceByCategory(any());
    }

    @Test
    @DisplayName("브랜드 관리 페이지 접근")
    void manageBrands_ShouldReturnCorrectViewAndData() throws Exception {
        // given
        when(brandService.getAllBrands()).thenReturn(allBrands);

        // when & then
        MvcResult result = mockMvc.perform(get("/manage-brands"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("manage-brands"))
                .andExpect(model().attributeExists("brands"))
                .andExpect(model().attributeExists("categoryNames"))
                .andExpect(model().attributeExists("categories"))
                .andReturn();

        // 모델 속성 상세 검증
        List<Brand> brands = (List<Brand>) result.getModelAndView().getModel().get("brands");
        assertNotNull(brands, "브랜드 목록이 null입니다");
        assertEquals(2, brands.size(), "브랜드 개수가 2개여야 합니다");
        assertEquals("A", brands.get(0).getName(), "첫 번째 브랜드명이 A여야 합니다");
        assertEquals("B", brands.get(1).getName(), "두 번째 브랜드명이 B여야 합니다");

        verify(brandService, times(1)).getAllBrands();
    }

    @Test
    @DisplayName("브랜드 관리 페이지 접근 실패 시 오류 페이지 반환")
    void manageBrands_ShouldReturnErrorPageOnFailure() throws Exception {
        // given
        when(brandService.getAllBrands()).thenThrow(new RuntimeException("테스트 오류"));

        // when & then
        mockMvc.perform(get("/manage-brands"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", containsString("테스트 오류")));

        verify(brandService, times(1)).getAllBrands();
    }

    @Test
    @DisplayName("브랜드 추가 폼 페이지 접근")
    void addBrandForm_ShouldReturnFormPage() throws Exception {
        // when & then
        mockMvc.perform(get("/add-brand"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("add-brand"))
                .andExpect(model().attributeExists("categories"));
    }

    @Test
    @DisplayName("브랜드 추가 폼 제출")
    void addBrand_ShouldCreateBrandAndRedirect() throws Exception {
        // given
        Brand newBrand = new Brand();
        newBrand.setId(3L);
        newBrand.setName("Test Brand");

        when(brandService.saveBrand(any(Brand.class))).thenReturn(newBrand);

        // 폼 파라미터 준비
        MultiValueMap<String, String> formParams = new LinkedMultiValueMap<>();
        formParams.add("brandName", "Test Brand");

        // 각 카테고리에 대한 가격 설정
        for (Category category : Category.values()) {
            formParams.add(category.name(), String.valueOf(1000 + category.ordinal() * 500));
        }

        // when & then
        mockMvc.perform(post("/add-brand")
                        .params(formParams))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/manage-brands"));

        // saveBrand가 정확히 한 번 호출되었는지 확인
        verify(brandService, times(1)).saveBrand(any(Brand.class));
    }

    @Test
    @DisplayName("브랜드 추가 실패 시 오류 페이지 반환")
    void addBrand_ShouldReturnErrorPageOnFailure() throws Exception {
        // given
        when(brandService.saveBrand(any(Brand.class))).thenThrow(new RuntimeException("테스트 오류"));

        // 폼 파라미터 준비
        MultiValueMap<String, String> formParams = new LinkedMultiValueMap<>();
        formParams.add("brandName", "Error Brand");

        // 각 카테고리에 대한 가격 설정
        for (Category category : Category.values()) {
            formParams.add(category.name(), String.valueOf(1000));
        }

        // when & then
        mockMvc.perform(post("/add-brand")
                        .params(formParams))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", containsString("테스트 오류")));

        verify(brandService, times(1)).saveBrand(any(Brand.class));
    }

    @Test
    @DisplayName("브랜드 수정 폼 페이지 접근")
    void editBrandForm_ShouldReturnFormPageWithBrandData() throws Exception {
        // given
        when(brandService.getBrandById(1L)).thenReturn(brandA);

        // when & then
        MvcResult result = mockMvc.perform(get("/edit-brand")
                        .param("id", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("edit-brand"))
                .andExpect(model().attributeExists("brand"))
                .andExpect(model().attributeExists("categories"))
                .andReturn();

        // 모델 속성 상세 검증
        Brand brand = (Brand) result.getModelAndView().getModel().get("brand");
        assertNotNull(brand, "브랜드가 null입니다");
        assertEquals("A", brand.getName(), "브랜드명이 A여야 합니다");
        assertEquals(1L, brand.getId(), "브랜드 ID가 1이어야 합니다");

        // 모든 카테고리 가격 확인
        for (Category category : Category.values()) {
            assertTrue(brand.getPrices().containsKey(category),
                    String.format("브랜드의 가격 맵에 %s 카테고리가 포함되어야 합니다", category));
        }

        verify(brandService, times(1)).getBrandById(1L);
    }

    @Test
    @DisplayName("존재하지 않는 브랜드 수정 폼 접근 시 오류 페이지 반환")
    void editBrandForm_ShouldReturnErrorPageForNonExistingBrand() throws Exception {
        // given
        when(brandService.getBrandById(99L)).thenReturn(null);

        // when & then
        mockMvc.perform(get("/edit-brand")
                        .param("id", "99"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("error"));

        verify(brandService, times(1)).getBrandById(99L);
    }

    @Test
    @DisplayName("브랜드 수정 폼 제출")
    void editBrand_ShouldUpdateBrandAndRedirect() throws Exception {
        // given
        when(brandService.getBrandById(1L)).thenReturn(brandA);
        when(brandService.saveBrand(any(Brand.class))).thenReturn(brandA);

        // 폼 파라미터 준비
        MultiValueMap<String, String> formParams = new LinkedMultiValueMap<>();
        formParams.add("id", "1");
        formParams.add("brandName", "Updated A");

        // 각 카테고리에 대한 가격 설정
        for (Category category : Category.values()) {
            formParams.add(category.name(), String.valueOf(2000 + category.ordinal() * 500));
        }

        // when & then
        mockMvc.perform(post("/edit-brand")
                        .params(formParams))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/manage-brands"));

        // 서비스 메서드 호출 확인
        verify(brandService, times(1)).getBrandById(1L);
        verify(brandService, times(1)).saveBrand(any(Brand.class));
    }

    @Test
    @DisplayName("브랜드 수정 실패 시 오류 페이지 반환")
    void editBrand_ShouldReturnErrorPageOnFailure() throws Exception {
        // given
        when(brandService.getBrandById(1L)).thenReturn(brandA);
        when(brandService.saveBrand(any(Brand.class))).thenThrow(new RuntimeException("테스트 오류"));

        // 폼 파라미터 준비
        MultiValueMap<String, String> formParams = new LinkedMultiValueMap<>();
        formParams.add("id", "1");
        formParams.add("brandName", "Error A");

        // 각 카테고리에 대한 가격 설정
        for (Category category : Category.values()) {
            formParams.add(category.name(), String.valueOf(1000));
        }

        // when & then
        mockMvc.perform(post("/edit-brand")
                        .params(formParams))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", containsString("테스트 오류")));

        verify(brandService, times(1)).getBrandById(1L);
        verify(brandService, times(1)).saveBrand(any(Brand.class));
    }

    @Test
    @DisplayName("브랜드 삭제 성공 시 리다이렉트")
    void deleteBrand_ShouldRedirectToManageBrands() throws Exception {
        // given
        when(brandService.getBrandById(1L)).thenReturn(brandA);
        doNothing().when(brandService).deleteBrand(1L);

        // when & then
        mockMvc.perform(get("/delete-brand")
                        .param("id", "1"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/manage-brands"));

        // 서비스 메서드 호출 확인
        verify(brandService, times(1)).getBrandById(1L);
        verify(brandService, times(1)).deleteBrand(1L);
    }

    @Test
    @DisplayName("존재하지 않는 브랜드 삭제 시 오류 페이지 반환")
    void deleteBrand_ShouldReturnErrorPageForNonExistingBrand() throws Exception {
        // given
        when(brandService.getBrandById(99L)).thenReturn(null);

        // when & then
        mockMvc.perform(get("/delete-brand")
                        .param("id", "99"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("error"));

        // deleteBrand가 호출되지 않아야 함
        verify(brandService, times(1)).getBrandById(99L);
        verify(brandService, never()).deleteBrand(anyLong());
    }

    @Test
    @DisplayName("브랜드 삭제 실패 시 오류 페이지 반환")
    void deleteBrand_ShouldReturnErrorPageOnFailure() throws Exception {
        // given
        when(brandService.getBrandById(1L)).thenReturn(brandA);
        doThrow(new RuntimeException("테스트 오류")).when(brandService).deleteBrand(1L);

        // when & then
        mockMvc.perform(get("/delete-brand")
                        .param("id", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", containsString("테스트 오류")));

        // 서비스 메서드 호출 확인
        verify(brandService, times(1)).getBrandById(1L);
        verify(brandService, times(1)).deleteBrand(1L);
    }
}