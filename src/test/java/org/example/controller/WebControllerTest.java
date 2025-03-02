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

/**
 * WebController 클래스의 기능을 테스트하는 테스트 클래스
 *
 * 이 클래스는 Spring의 MVC 테스트 기능을 사용하여 컨트롤러의 모든 웹 엔드포인트를 테스트합니다.
 * 각 API 기능 및 사용자 인터페이스 페이지에 대한 테스트 케이스를 포함하며,
 * 정상 동작 시나리오와 오류 처리 시나리오를 모두 검증합니다.
 */
@WebMvcTest(WebController.class) // WebController만 로드하여 웹 계층 테스트에 집중
public class WebControllerTest {

    @Autowired
    private MockMvc mockMvc; // HTTP 요청을 시뮬레이션하기 위한 MockMvc

    @MockBean
    private BrandService brandService; // 컨트롤러가 의존하는 서비스를 모킹

    // 테스트에 사용할 데이터 객체들
    private Brand brandA;
    private Brand brandB;
    private List<Brand> allBrands;
    private Map<Category, Map<String, Object>> lowestPriceByCategory;
    private Map<String, Object> lowestTotalPriceBrand;
    private Map<String, Object> minMaxPriceByCategory;

    // 로그 레벨을 저장할 변수
    private Level originalLogLevel;

    /**
     * 각 테스트 실행 전에 호출되는 설정 메서드
     *
     * 테스트 중 불필요한 로그 출력을 방지하기 위해 로그 레벨을 조정하고,
     * 테스트에 필요한 모든 데이터 객체를 초기화합니다.
     */
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

    /**
     * 각 테스트 실행 후에 호출되는 정리 메서드
     *
     * 테스트 전에 변경한 로그 레벨을 원래대로 복원합니다.
     */
    @AfterEach
    void tearDown() {
        // 테스트 후 로그 레벨 복원
        Logger logger = (Logger) LoggerFactory.getLogger(WebController.class);
        logger.setLevel(originalLogLevel);
    }

    /**
     * 테스트 브랜드 데이터를 초기화하는 헬퍼 메서드
     *
     * 테스트에 사용할 Brand 객체들을 생성하고 각 카테고리별 가격을 설정합니다.
     * 이 데이터는 테이블에 표시된 명세에 따라 설정되었습니다.
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
     * API 1: 카테고리별 최저가격 데이터를 초기화하는 헬퍼 메서드
     *
     * 각 카테고리별로 최저가격 브랜드와 가격 정보를 담은 데이터 구조를 생성합니다.
     * 이 데이터는 brandService.getLowestPriceByCategory()의 반환값을 모킹하는 데 사용됩니다.
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
     * API 2: 최저 총액 브랜드 데이터를 초기화하는 헬퍼 메서드
     *
     * 단일 브랜드로 전체 카테고리 상품 구매 시 최저가격인 브랜드와
     * 각 카테고리별 가격, 총액 정보를 담은 데이터 구조를 생성합니다.
     * 이 데이터는 brandService.getLowestTotalPriceBrand()의 반환값을 모킹하는 데 사용됩니다.
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
     * API 3: 카테고리별 최저/최고 가격 데이터를 초기화하는 헬퍼 메서드
     *
     * 특정 카테고리(상의)에 대한 최저가 브랜드와 최고가 브랜드, 각 가격 정보를 담은
     * 데이터 구조를 생성합니다.
     * 이 데이터는 brandService.getMinMaxPriceByCategory()의 반환값을 모킹하는 데 사용됩니다.
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

    /**
     * 메인 페이지 접근 테스트
     *
     * "/"(루트) 경로로 GET 요청 시 올바른 뷰 이름(index)을 반환하는지 검증합니다.
     * 메인 페이지는 모델 속성이 필요하지 않으므로 모델 크기가 0인지도 확인합니다.
     */
    @Test
    @DisplayName("메인 페이지 접근")
    void home_ShouldReturnIndexPage() throws Exception {
        mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().size(0)); // 메인 페이지는 모델 속성이 없음
    }

    /**
     * API 1: 카테고리별 최저가격 페이지 접근 테스트
     *
     * "/lowest-price-by-category" 경로로 GET 요청 시:
     * 1. 올바른 뷰 이름을 반환하는지
     * 2. 필요한 모델 속성("categories", "totalPrice")이 존재하는지
     * 3. 모델 속성의 값이 예상대로인지(8개 카테고리, 총액 포맷팅)
     * 4. brandService.getLowestPriceByCategory()가 정확히 1번 호출되는지 검증합니다.
     */
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

    /**
     * API 1: 카테고리별 최저가격 조회 실패 시 오류 페이지 반환 테스트
     *
     * 서비스 메서드가 예외를 던지는 경우:
     * 1. 오류 페이지(error)가 반환되는지
     * 2. 오류 메시지가 모델에 포함되는지
     * 3. 예외 메시지가 모델의 오류 속성에 포함되는지 검증합니다.
     */
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

    /**
     * API 2: 단일 브랜드 최저 총액 페이지 접근 테스트
     *
     * "/lowest-total-price-brand" 경로로 GET 요청 시:
     * 1. 올바른 뷰 이름을 반환하는지
     * 2. 필요한 모델 속성("brandName", "categories", "totalPrice")이 존재하는지
     * 3. 모델 속성의 값이 예상대로인지(브랜드명, 카테고리 개수, 총액)
     * 4. brandService.getLowestTotalPriceBrand()가 정확히 1번 호출되는지 검증합니다.
     */
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

    /**
     * API 2: 단일 브랜드 최저 총액 조회 실패 시 오류 페이지 반환 테스트
     *
     * 서비스 메서드가 예외를 던지는 경우:
     * 1. 오류 페이지(error)가 반환되는지
     * 2. 오류 메시지가 모델에 포함되는지
     * 3. 예외 메시지가 모델의 오류 속성에 포함되는지 검증합니다.
     */
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

    /**
     * API 3: 카테고리별 최저/최고 가격 폼 페이지 접근 테스트
     *
     * "/min-max-price-by-category" 경로로 GET 요청 시:
     * 1. 올바른 뷰 이름을 반환하는지
     * 2. 필요한 모델 속성("categoryNames")이 존재하는지 검증합니다.
     */
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

    /**
     * API 3: 카테고리별 최저/최고 가격 결과 페이지 접근 테스트
     *
     * "/min-max-price-result" 경로로 GET 요청 시:
     * 1. 올바른 뷰 이름을 반환하는지
     * 2. 필요한 모델 속성("categoryName", "minPrices", "maxPrices")이 존재하는지
     * 3. 모델 속성의 값이 예상대로인지(카테고리명, 최저가 브랜드, 최고가 브랜드)
     * 4. brandService.getMinMaxPriceByCategory(Category.TOP)가 정확히 1번 호출되는지 검증합니다.
     */
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

    /**
     * API 3: 잘못된 카테고리 이름으로 최저/최고 가격 결과 조회 시 오류 페이지 반환 테스트
     *
     * 유효하지 않은 카테고리명으로 요청 시:
     * 1. 오류 페이지(error)가 반환되는지
     * 2. 오류 메시지가 모델에 포함되는지
     * 3. 오류 메시지에 "잘못된 카테고리 이름"이 포함되는지
     * 4. brandService.getMinMaxPriceByCategory()가 호출되지 않는지 검증합니다.
     */
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

    /**
     * API 4: 브랜드 관리 페이지 접근 테스트
     *
     * "/manage-brands" 경로로 GET 요청 시:
     * 1. 올바른 뷰 이름을 반환하는지
     * 2. 필요한 모델 속성("brands", "categoryNames", "categories")이 존재하는지
     * 3. 모델 속성의 값이 예상대로인지(브랜드 목록 크기, 브랜드명)
     * 4. brandService.getAllBrands()가 정확히 1번 호출되는지 검증합니다.
     */
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

    /**
     * API 4: 브랜드 관리 페이지 접근 실패 시 오류 페이지 반환 테스트
     *
     * 서비스 메서드가 예외를 던지는 경우:
     * 1. 오류 페이지(error)가 반환되는지
     * 2. 오류 메시지가 모델에 포함되는지
     * 3. 예외 메시지가 모델의 오류 속성에 포함되는지 검증합니다.
     */
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

    /**
     * API 4: 브랜드 추가 폼 페이지 접근 테스트
     *
     * "/add-brand" 경로로 GET 요청 시:
     * 1. 올바른 뷰 이름을 반환하는지
     * 2. 필요한 모델 속성("categories")이 존재하는지 검증합니다.
     */
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

    /**
     * API 4: 브랜드 추가 폼 제출 테스트
     *
     * "/add-brand" 경로로 POST 요청 시:
     * 1. 리다이렉트 상태코드(3xx)를 반환하는지
     * 2. 브랜드 관리 페이지(/manage-brands)로 리다이렉트되는지
     * 3. brandService.saveBrand()가 정확히 1번 호출되는지 검증합니다.
     */
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

    /**
     * API 4: 브랜드 추가 실패 시 오류 페이지 반환 테스트
     *
     * 브랜드 추가 중 서비스 메서드가 예외를 던지는 경우:
     * 1. 오류 페이지(error)가 반환되는지
     * 2. 오류 메시지가 모델에 포함되는지
     * 3. 예외 메시지가 모델의 오류 속성에 포함되는지 검증합니다.
     */
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

    /**
     * API 4: 브랜드 수정 폼 페이지 접근 테스트
     *
     * "/edit-brand" 경로로 GET 요청 시:
     * 1. 올바른 뷰 이름을 반환하는지
     * 2. 필요한 모델 속성("brand", "categories")이 존재하는지
     * 3. 모델 속성의 값이 예상대로인지(브랜드명, ID, 가격 정보)
     * 4. brandService.getBrandById()가 정확히 1번 호출되는지 검증합니다.
     */
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

    /**
     * API 4: 존재하지 않는 브랜드 수정 폼 접근 시 오류 페이지 반환 테스트
     *
     * 존재하지 않는 브랜드 ID로 수정 폼 접근 시:
     * 1. 오류 페이지(error)가 반환되는지
     * 2. 오류 메시지가 모델에 포함되는지 검증합니다.
     */
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

    /**
     * API 4: 브랜드 수정 폼 제출 테스트
     *
     * "/edit-brand" 경로로 POST 요청 시:
     * 1. 리다이렉트 상태코드(3xx)를 반환하는지
     * 2. 브랜드 관리 페이지(/manage-brands)로 리다이렉트되는지
     * 3. brandService.getBrandById()와 brandService.saveBrand()가
     *    각각 정확히 1번 호출되는지 검증합니다.
     */
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

    /**
     * API 4: 브랜드 수정 실패 시 오류 페이지 반환 테스트
     *
     * 브랜드 수정 중 서비스 메서드가 예외를 던지는 경우:
     * 1. 오류 페이지(error)가 반환되는지
     * 2. 오류 메시지가 모델에 포함되는지
     * 3. 예외 메시지가 모델의 오류 속성에 포함되는지 검증합니다.
     */
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

    /**
     * API 4: 브랜드 삭제 성공 시 리다이렉트 테스트
     *
     * "/delete-brand" 경로로 GET 요청 시:
     * 1. 리다이렉트 상태코드(3xx)를 반환하는지
     * 2. 브랜드 관리 페이지(/manage-brands)로 리다이렉트되는지
     * 3. brandService.getBrandById()와 brandService.deleteBrand()가
     *    각각 정확히 1번 호출되는지 검증합니다.
     */
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

    /**
     * API 4: 존재하지 않는 브랜드 삭제 시 오류 페이지 반환 테스트
     *
     * 존재하지 않는 브랜드 ID로 삭제 요청 시:
     * 1. 오류 페이지(error)가 반환되는지
     * 2. 오류 메시지가 모델에 포함되는지
     * 3. brandService.deleteBrand()가 호출되지 않는지 검증합니다.
     */
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

    /**
     * API 4: 브랜드 삭제 실패 시 오류 페이지 반환 테스트
     *
     * 브랜드 삭제 중 서비스 메서드가 예외를 던지는 경우:
     * 1. 오류 페이지(error)가 반환되는지
     * 2. 오류 메시지가 모델에 포함되는지
     * 3. 예외 메시지가 모델의 오류 속성에 포함되는지 검증합니다.
     */
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