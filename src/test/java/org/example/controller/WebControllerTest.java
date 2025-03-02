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

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WebController.class)
public class WebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BrandService brandService;

    private Brand brandA;
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

        Brand brandB = new Brand();
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

        // 카테고리별 최저가격 설정
        lowestPriceByCategory = new HashMap<>();
        for (Category category : Category.values()) {
            Map<String, Object> categoryData = new HashMap<>();
            categoryData.put("brand", "C");
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

    @AfterEach
    void tearDown() {
        // 테스트 후 로그 레벨 복원
        Logger logger = (Logger) LoggerFactory.getLogger(WebController.class);
        logger.setLevel(originalLogLevel);
    }

    @Test
    @DisplayName("메인 페이지 접근")
    void home_ShouldReturnIndexPage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    @DisplayName("카테고리별 최저가격 페이지 접근")
    void lowestPriceByCategory_ShouldReturnCorrectViewAndData() throws Exception {
        // given
        when(brandService.getLowestPriceByCategory()).thenReturn(lowestPriceByCategory);

        // when & then
        mockMvc.perform(get("/lowest-price-by-category"))
                .andExpect(status().isOk())
                .andExpect(view().name("lowest-price-by-category"))
                .andExpect(model().attributeExists("categories"))
                .andExpect(model().attributeExists("totalPrice"));
    }

    @Test
    @DisplayName("단일 브랜드 최저 총액 페이지 접근")
    void lowestTotalPriceBrand_ShouldReturnCorrectViewAndData() throws Exception {
        // given
        when(brandService.getLowestTotalPriceBrand()).thenReturn(lowestTotalPriceBrand);

        // when & then
        mockMvc.perform(get("/lowest-total-price-brand"))
                .andExpect(status().isOk())
                .andExpect(view().name("lowest-total-price-brand"))
                .andExpect(model().attributeExists("brandName"))
                .andExpect(model().attributeExists("categories"))
                .andExpect(model().attributeExists("totalPrice"));
    }

    @Test
    @DisplayName("카테고리별 최저/최고 가격 폼 페이지 접근")
    void minMaxPriceByCategory_ShouldReturnFormPage() throws Exception {
        mockMvc.perform(get("/min-max-price-by-category"))
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
        mockMvc.perform(get("/min-max-price-result?categoryName=상의"))
                .andExpect(status().isOk())
                .andExpect(view().name("min-max-price-result"))
                .andExpect(model().attributeExists("categoryName"))
                .andExpect(model().attributeExists("minPrices"))
                .andExpect(model().attributeExists("maxPrices"));
    }

    @Test
    @DisplayName("잘못된 카테고리 이름으로 최저/최고 가격 결과 조회 시 오류 페이지 반환")
    void minMaxPriceResult_ShouldReturnErrorPageForInvalidCategory() throws Exception {
        // 예외를 모킹할 수 없으므로 직접 요청을 보내고 응답을 검증
        MvcResult result = mockMvc.perform(get("/min-max-price-result?categoryName=잘못된카테고리"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("error"))
                .andReturn();

        // 응답 검증 - 에러 메시지에 특정 문구가 포함되어 있는지 확인
        Object errorMsg = result.getModelAndView().getModel().get("error");
        assertTrue(errorMsg != null && errorMsg.toString().contains("잘못된 카테고리 이름"));

        // 서비스 메소드가 호출되지 않았는지 확인
        verify(brandService, never()).getMinMaxPriceByCategory(any());
    }

    @Test
    @DisplayName("브랜드 관리 페이지 접근")
    void manageBrands_ShouldReturnCorrectViewAndData() throws Exception {
        // given
        when(brandService.getAllBrands()).thenReturn(allBrands);

        // when & then
        mockMvc.perform(get("/manage-brands"))
                .andExpect(status().isOk())
                .andExpect(view().name("manage-brands"))
                .andExpect(model().attributeExists("brands"))
                .andExpect(model().attributeExists("categoryNames"))
                .andExpect(model().attributeExists("categories"));
    }

    @Test
    @DisplayName("브랜드 추가 폼 페이지 접근")
    void addBrandForm_ShouldReturnFormPage() throws Exception {
        mockMvc.perform(get("/add-brand"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-brand"))
                .andExpect(model().attributeExists("categories"));
    }

    @Test
    @DisplayName("브랜드 수정 폼 페이지 접근")
    void editBrandForm_ShouldReturnFormPageWithBrandData() throws Exception {
        // given
        when(brandService.getBrandById(1L)).thenReturn(brandA);

        // when & then
        mockMvc.perform(get("/edit-brand?id=1"))
                .andExpect(status().isOk())
                .andExpect(view().name("edit-brand"))
                .andExpect(model().attributeExists("brand"))
                .andExpect(model().attributeExists("categories"));
    }

    @Test
    @DisplayName("존재하지 않는 브랜드 수정 폼 접근 시 오류 페이지 반환")
    void editBrandForm_ShouldReturnErrorPageForNonExistingBrand() throws Exception {
        // given
        when(brandService.getBrandById(99L)).thenReturn(null);

        // when & then
        mockMvc.perform(get("/edit-brand?id=99"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    @DisplayName("브랜드 삭제 성공 시 리다이렉트")
    void deleteBrand_ShouldRedirectToManageBrands() throws Exception {
        // given
        when(brandService.getBrandById(1L)).thenReturn(brandA);

        // when & then
        mockMvc.perform(get("/delete-brand?id=1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/manage-brands"));

        verify(brandService, times(1)).deleteBrand(1L);
    }

    @Test
    @DisplayName("존재하지 않는 브랜드 삭제 시 오류 페이지 반환")
    void deleteBrand_ShouldReturnErrorPageForNonExistingBrand() throws Exception {
        // given
        when(brandService.getBrandById(99L)).thenReturn(null);

        // when & then
        mockMvc.perform(get("/delete-brand?id=99"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("error"));

        verify(brandService, never()).deleteBrand(anyLong());
    }
}