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

/**
 * 클래스 설명 : ApiController 클래스의 단위 테스트
 * @WebMvcTest 어노테이션은 스프링 MVC 컴포넌트에 초점을 맞춘 테스트로,
 * 전체 애플리케이션 컨텍스트를 로드하지 않고 웹 레이어 관련 빈만 로드하여 테스트 속도를 높인다.
 * ApiController만 테스트 대상으로 지정하여 다른 컨트롤러는 로드하지 않는다.
 * 작성자 : sys1021
 * 작성일 : 2025.03.02
 */
@WebMvcTest(ApiController.class)
public class ApiControllerTest {

    /**
     * MockMvc: 서버를 실행하지 않고 스프링 MVC의 동작을 재현하여 컨트롤러를 테스트하는 도구
     * 실제 HTTP 요청 없이 컨트롤러의 엔드포인트를 호출하고 응답을 검증할 수 있다.
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * BrandService를 모킹하여 컨트롤러 레이어만 독립적으로 테스트한다.
     * @MockBean은 실제 서비스 대신 Mockito로 생성된 Mock 객체를 스프링 애플리케이션 컨텍스트에 주입한다.
     */
    @MockBean
    private BrandService brandService;

    /**
     * ObjectMapper: 자바 객체를 JSON으로 변환하거나 JSON을 자바 객체로 변환하는 데 사용된다.
     * 테스트에서는 주로 요청 본문을 JSON으로 직렬화하는 데 활용된다.
     */
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 테스트에 사용할 모델 객체들
     */
    private Brand brandA; // 테스트용 브랜드 A
    private Map<Category, Map<String, Object>> lowestPriceByCategory; // API 1 테스트용 응답 데이터
    private Map<String, Object> lowestTotalPriceBrand; // API 2 테스트용 응답 데이터
    private Map<String, Object> minMaxPriceByCategory; // API 3 테스트용 응답 데이터

    /**
     * 각 테스트 메서드 실행 전에 수행되는 초기화 작업
     * 테스트에 필요한 데이터를 설정하고 모킹한다.
     */
    @BeforeEach
    void setUp() {
        // 테스트 데이터 설정
        setupTestBrands();
        setupLowestPriceByCategory();
        setupLowestTotalPriceBrand();
        setupMinMaxPriceByCategory();
    }

    /**
     * 테스트에서 사용할 브랜드 데이터를 설정하는 헬퍼 메서드
     * 브랜드 A를 생성하고 각 카테고리별 가격을 설정한다.
     */
    private void setupTestBrands() {

        brandA = new Brand();
        brandA.setId(1L);
        brandA.setName("A");
        brandA.getPrices().put(Category.TOP, 11200);      // 상의 가격
        brandA.getPrices().put(Category.OUTER, 5500);     // 아우터 가격
        brandA.getPrices().put(Category.PANTS, 4200);     // 바지 가격
        brandA.getPrices().put(Category.SNEAKERS, 9000);  // 스니커즈 가격
        brandA.getPrices().put(Category.BAG, 2000);       // 가방 가격
        brandA.getPrices().put(Category.HAT, 1700);       // 모자 가격
        brandA.getPrices().put(Category.SOCKS, 1800);     // 양말 가격
        brandA.getPrices().put(Category.ACCESSORY, 2300); // 액세서리 가격

    }

    /**
     * API 1 카테고리별 최저가격 데이터 설정
     * 모든 카테고리에 대해 최저가격 브랜드와 가격 정보를 맵 형태로 구성한다.
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
     * API 2 최저 총액 브랜드 데이터 설정
     * 단일 브랜드로 모든 카테고리 상품을 구매할 때 최저가격인 브랜드와 총액 정보를 맵 형태로 구성한다.
     * LinkedHashMap을 사용하여 삽입 순서를 유지한다.
     */
    private void setupLowestTotalPriceBrand() {

        lowestTotalPriceBrand = new LinkedHashMap<>();
        Map<String, Object> brandInfo = new LinkedHashMap<>();
        brandInfo.put("브랜드", "D");

        List<Map<String, String>> categoryPrices = new ArrayList<>();
        for (Category category : Category.values()) {
            Map<String, String> categoryPrice = new LinkedHashMap<>();
            categoryPrice.put("카테고리", category.getDisplayName());
            categoryPrice.put("가격", "5,000"); // 천 단위 구분자(콤마) 포함
            categoryPrices.add(categoryPrice);
        }

        brandInfo.put("카테고리", categoryPrices);
        brandInfo.put("총액", "36,100"); // 천 단위 구분자(콤마) 포함
        lowestTotalPriceBrand.put("최저가", brandInfo);

    }

    /**
     * API 3 카테고리별 최저/최고 가격 데이터 설정
     * 특정 카테고리(상의)에 대한 최저가격 브랜드와 최고가격 브랜드 정보를 맵 형태로 구성한다.
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
     * API 1: 카테고리별 최저가격 브랜드와 상품가격 조회 테스트
     * GET /api/lowest-price-by-category 엔드포인트가 올바른 응답을 반환하는지 검증한다.
     * - HTTP 상태 코드 200(OK)
     * - 응답 JSON에 categories 배열과 totalPrice 포함
     * - categories 배열의 각 요소가 category, brand, price 필드를 포함
     */
    @Test
    @DisplayName("API 1: 카테고리별 최저가격 브랜드와 상품가격 조회")
    void getLowestPriceByCategory_ShouldReturnLowestPriceForEachCategory() throws Exception {

        // given: 서비스 메서드가 호출될 때 반환할 값을 모킹
        when(brandService.getLowestPriceByCategory()).thenReturn(lowestPriceByCategory);

        // when & then: API 요청 및 응답 검증
        mockMvc.perform(get("/api/lowest-price-by-category"))
                .andDo(print()) // 테스트 결과를 로그로 출력
                .andExpect(status().isOk()) // 상태 코드 200 확인
                .andExpect(jsonPath("$.categories").isArray()) // categories가 배열인지 확인
                .andExpect(jsonPath("$.categories", hasSize(8))) // 8개 카테고리 확인
                .andExpect(jsonPath("$.totalPrice").exists()) // totalPrice 필드 존재 확인
                .andExpect(jsonPath("$.categories[0]", hasKey("category"))) // 첫 번째 요소에 category 키 확인
                .andExpect(jsonPath("$.categories[0]", hasKey("brand"))) // 첫 번째 요소에 brand 키 확인
                .andExpect(jsonPath("$.categories[0]", hasKey("price"))); // 첫 번째 요소에 price 키 확인

        // 서비스 메서드가 정확히 한 번 호출되었는지 확인
        verify(brandService, times(1)).getLowestPriceByCategory();

    }

    /**
     * API 1: 서비스 예외 발생 시 오류 응답 반환 테스트
     * 서비스에서 예외가 발생할 경우 컨트롤러가 적절한 오류 응답을 반환하는지 검증한다.
     * - HTTP 상태 코드 500(Internal Server Error)
     * - 응답에 오류 메시지 포함
     */
    @Test
    @DisplayName("API 1: 서비스 예외 발생 시 오류 응답 반환")
    void getLowestPriceByCategory_ShouldReturnErrorWhenServiceThrowsException() throws Exception {

        // given: 서비스 메서드가 예외를 던지도록 모킹
        when(brandService.getLowestPriceByCategory()).thenThrow(new RuntimeException("서비스 오류"));

        // when & then: API 요청 및 오류 응답 검증
        mockMvc.perform(get("/api/lowest-price-by-category"))
                .andDo(print()) // 테스트 결과를 로그로 출력
                .andExpect(status().isInternalServerError()) // 상태 코드 500 확인
                .andExpect(jsonPath("$.error").exists()) // error 필드 존재 확인
                .andExpect(jsonPath("$.message").value("서비스 오류")); // 오류 메시지 확인

    }

    /**
     * API 2: 단일 브랜드 최저 총액 조회 테스트
     * GET /api/lowest-total-price-brand 엔드포인트가 올바른 응답을 반환하는지 검증한다.
     * - HTTP 상태 코드 200(OK)
     * - 응답 JSON의 구조 및 필드 값이 예상대로인지 확인
     */
    @Test
    @DisplayName("API 2: 단일 브랜드 최저 총액 조회")
    void getLowestTotalPriceBrand_ShouldReturnBrandWithLowestTotalPrice() throws Exception {

        // given: 서비스 메서드가 호출될 때 반환할 값을 모킹
        when(brandService.getLowestTotalPriceBrand()).thenReturn(lowestTotalPriceBrand);

        // when & then: API 요청 및 응답 검증
        mockMvc.perform(get("/api/lowest-total-price-brand"))
                .andDo(print()) // 테스트 결과를 로그로 출력
                .andExpect(status().isOk()) // 상태 코드 200 확인
                .andExpect(jsonPath("$.최저가.브랜드").value("D")) // 브랜드명 확인
                .andExpect(jsonPath("$.최저가.카테고리").isArray()) // 카테고리가 배열인지 확인
                .andExpect(jsonPath("$.최저가.카테고리", hasSize(8))) // 8개 카테고리 확인
                .andExpect(jsonPath("$.최저가.총액").value("36,100")); // 총액 확인

        // 서비스 메서드가 정확히 한 번 호출되었는지 확인
        verify(brandService, times(1)).getLowestTotalPriceBrand();

    }

    /**
     * API 2: 서비스 예외 발생 시 오류 응답 반환 테스트
     * 서비스에서 예외가 발생할 경우 컨트롤러가 적절한 오류 응답을 반환하는지 검증한다.
     * - HTTP 상태 코드 500(Internal Server Error)
     * - 응답에 오류 메시지 포함
     */
    @Test
    @DisplayName("API 2: 서비스 예외 발생 시 오류 응답 반환")
    void getLowestTotalPriceBrand_ShouldReturnErrorWhenServiceThrowsException() throws Exception {

        // given: 서비스 메서드가 예외를 던지도록 모킹
        when(brandService.getLowestTotalPriceBrand()).thenThrow(new RuntimeException("서비스 오류"));

        // when & then: API 요청 및 오류 응답 검증
        mockMvc.perform(get("/api/lowest-total-price-brand"))
                .andDo(print()) // 테스트 결과를 로그로 출력
                .andExpect(status().isInternalServerError()) // 상태 코드 500 확인
                .andExpect(jsonPath("$.error").exists()) // error 필드 존재 확인
                .andExpect(jsonPath("$.message").value("서비스 오류")); // 오류 메시지 확인

    }

    /**
     * API 3: 카테고리별 최저/최고 가격 브랜드 조회 테스트 (모든 카테고리 대상)
     * GET /api/min-max-price-by-category 엔드포인트가 올바른 응답을 반환하는지 검증한다.
     * @ParameterizedTest와 @EnumSource를 사용하여 모든 카테고리에 대해 테스트를 반복 실행한다.
     */
    @ParameterizedTest
    @EnumSource(Category.class) // Category enum의 모든 값에 대해 테스트 반복
    @DisplayName("API 3: 카테고리별 최저/최고 가격 브랜드 조회 - 모든 카테고리 테스트")
    void getMinMaxPriceByCategory_ShouldReturnMinAndMaxPriceForAllCategories(Category category) throws Exception {

        // given: 테스트 데이터의 카테고리를 현재 테스트 중인 카테고리로 변경하고 서비스 모킹
        minMaxPriceByCategory.put("카테고리", category.getDisplayName());
        when(brandService.getMinMaxPriceByCategory(category)).thenReturn(minMaxPriceByCategory);

        // when & then: API 요청 및 응답 검증
        mockMvc.perform(get("/api/min-max-price-by-category")
                        .param("categoryName", category.getDisplayName())) // 카테고리명 파라미터 추가
                .andDo(print()) // 테스트 결과를 로그로 출력
                .andExpect(status().isOk()) // 상태 코드 200 확인
                .andExpect(jsonPath("$.카테고리").value(category.getDisplayName())) // 카테고리명 확인
                .andExpect(jsonPath("$.최저가").exists()) // 최저가 필드 존재 확인
                .andExpect(jsonPath("$.최고가").exists()); // 최고가 필드 존재 확인

        // 서비스 메서드가 정확히 한 번 호출되었는지 확인
        verify(brandService, times(1)).getMinMaxPriceByCategory(category);

    }

    /**
     * API 3: 잘못된 카테고리명으로 조회 시 오류 반환 테스트
     * 유효하지 않은 카테고리명으로 요청 시 컨트롤러가 적절한 오류 응답을 반환하는지 검증한다.
     * - HTTP 상태 코드 400(Bad Request)
     * - 응답에 오류 메시지 포함
     */
    @Test
    @DisplayName("API 3: 잘못된 카테고리명으로 조회 시 오류 반환")
    void getMinMaxPriceByCategory_ShouldReturnErrorForInvalidCategory() throws Exception {

        // given: 서비스 메서드가 IllegalArgumentException을 던지도록 모킹
        when(brandService.getMinMaxPriceByCategory(any()))
                .thenThrow(new IllegalArgumentException("잘못된 카테고리 이름: 존재하지않는카테고리"));

        // when & then: API 요청 및 오류 응답 검증
        mockMvc.perform(get("/api/min-max-price-by-category")
                        .param("categoryName", "존재하지않는카테고리")) // 존재하지 않는 카테고리명 파라미터
                .andDo(print()) // 테스트 결과를 로그로 출력
                .andExpect(status().isBadRequest()) // 상태 코드 400 확인
                .andExpect(jsonPath("$.error").value("잘못된 카테고리 이름")) // 오류 타입 확인
                .andExpect(jsonPath("$.message").value(containsString("잘못된 카테고리 이름"))); // 오류 메시지 확인

    }

    /**
     * API 4: 브랜드 생성 테스트
     * POST /api/brand 엔드포인트가 새로운 브랜드를 올바르게 생성하는지 검증한다.
     * - HTTP 상태 코드 201(Created)
     * - 응답 JSON에 성공 상태 및 생성된 브랜드 ID 포함
     */
    @Test
    @DisplayName("API 4: 브랜드 생성")
    void createBrand_ShouldCreateNewBrand() throws Exception {

        // given: 테스트용 브랜드 DTO 생성 및 서비스 메서드 모킹
        BrandDto brandDto = createBrandDto("New Brand", 10000, 5000);
        Brand savedBrand = createBrand(3L, "New Brand", 10000, 5000);

        when(brandService.saveBrand(any(Brand.class))).thenReturn(savedBrand);

        // when & then: API 요청 및 응답 검증
        ResultActions result = mockMvc.perform(post("/api/brand") // POST 요청
                        .contentType(MediaType.APPLICATION_JSON) // JSON 타입 지정
                        .content(objectMapper.writeValueAsString(brandDto))) // 요청 본문에 브랜드 DTO JSON 포함
                .andDo(print()) // 테스트 결과를 로그로 출력
                .andExpect(status().isCreated()) // 상태 코드 201 확인
                .andExpect(jsonPath("$.status").value("success")) // 성공 상태 확인
                .andExpect(jsonPath("$.message").exists()) // 메시지 필드 존재 확인
                .andExpect(jsonPath("$.brandId").value("3")); // 생성된 브랜드 ID 확인

        // 서비스 메서드가 정확히 한 번 호출되었는지 확인
        verify(brandService, times(1)).saveBrand(any(Brand.class));

    }

    /**
     * API 4: 브랜드 수정 테스트
     * PUT /api/brand/{id} 엔드포인트가 기존 브랜드를 올바르게 수정하는지 검증한다.
     * - HTTP 상태 코드 200(OK)
     * - 응답 JSON에 성공 상태 및 메시지 포함
     */
    @Test
    @DisplayName("API 4: 브랜드 수정")
    void updateBrand_ShouldUpdateExistingBrand() throws Exception {

        // given: 테스트용 브랜드 ID, DTO 및 서비스 메서드 모킹
        Long brandId = 1L;
        BrandDto brandDto = createBrandDto("Updated Brand", 10000, 5000);
        Brand existingBrand = createBrand(brandId, "A", 11200, 5500); // 기존 브랜드
        Brand updatedBrand = createBrand(brandId, "Updated Brand", 10000, 5000); // 업데이트된 브랜드

        when(brandService.getBrandById(brandId)).thenReturn(existingBrand);
        when(brandService.saveBrand(any(Brand.class))).thenReturn(updatedBrand);

        // when & then: API 요청 및 응답 검증
        mockMvc.perform(put("/api/brand/{id}", brandId) // PUT 요청
                        .contentType(MediaType.APPLICATION_JSON) // JSON 타입 지정
                        .content(objectMapper.writeValueAsString(brandDto))) // 요청 본문에 브랜드 DTO JSON 포함
                .andDo(print()) // 테스트 결과를 로그로 출력
                .andExpect(status().isOk()) // 상태 코드 200 확인
                .andExpect(jsonPath("$.status").value("success")) // 성공 상태 확인
                .andExpect(jsonPath("$.message").exists()); // 메시지 필드 존재 확인

        // 서비스 메서드 호출 확인
        verify(brandService, times(1)).getBrandById(brandId);
        verify(brandService, times(1)).saveBrand(any(Brand.class));

    }

    /**
     * API 4: 존재하지 않는 브랜드 수정 시 오류 반환 테스트
     * 존재하지 않는 브랜드 ID로 수정 요청 시 컨트롤러가 적절한 오류 응답을 반환하는지 검증한다.
     * - HTTP 상태 코드 404(Not Found)
     * - 응답에 오류 메시지 포함
     */
    @Test
    @DisplayName("API 4: 존재하지 않는 브랜드 수정 시 오류 반환")
    void updateBrand_ShouldReturnErrorForNonExistingBrand() throws Exception {

        // given: 존재하지 않는 브랜드 ID, DTO 및 서비스 메서드 모킹
        Long brandId = 999L;
        BrandDto brandDto = createBrandDto("Non Existing Brand", 10000, 5000);

        when(brandService.getBrandById(brandId)).thenReturn(null); // null 반환하여 브랜드 없음 표시

        // when & then: API 요청 및 오류 응답 검증
        mockMvc.perform(put("/api/brand/{id}", brandId) // PUT 요청
                        .contentType(MediaType.APPLICATION_JSON) // JSON 타입 지정
                        .content(objectMapper.writeValueAsString(brandDto))) // 요청 본문에 브랜드 DTO JSON 포함
                .andDo(print()) // 테스트 결과를 로그로 출력
                .andExpect(status().isNotFound()) // 상태 코드 404 확인
                .andExpect(jsonPath("$.error").exists()) // error 필드 존재 확인
                .andExpect(jsonPath("$.message").exists()); // message 필드 존재 확인

        // saveBrand가 호출되지 않아야 함
        verify(brandService, never()).saveBrand(any(Brand.class));

    }

    /**
     * API 4: 브랜드 삭제 테스트
     * DELETE /api/brand/{id} 엔드포인트가 브랜드를 올바르게 삭제하는지 검증한다.
     * - HTTP 상태 코드 200(OK)
     * - 응답 JSON에 성공 상태 및 메시지 포함
     */
    @Test
    @DisplayName("API 4: 브랜드 삭제")
    void deleteBrand_ShouldDeleteExistingBrand() throws Exception {

        // given: 테스트용 브랜드 ID 및 서비스 메서드 모킹
        Long brandId = 1L;
        when(brandService.getBrandById(brandId)).thenReturn(brandA);
        doNothing().when(brandService).deleteBrand(brandId); // void 메서드 모킹

        // when & then: API 요청 및 응답 검증
        mockMvc.perform(delete("/api/brand/{id}", brandId)) // DELETE 요청
                .andDo(print()) // 테스트 결과를 로그로 출력
                .andExpect(status().isOk()) // 상태 코드 200 확인
                .andExpect(jsonPath("$.status").value("success")) // 성공 상태 확인
                .andExpect(jsonPath("$.message").exists()); // 메시지 필드 존재 확인

        // 서비스 메서드 호출 확인
        verify(brandService, times(1)).getBrandById(brandId);
        verify(brandService, times(1)).deleteBrand(brandId);

    }

    /**
     * API 4: 존재하지 않는 브랜드 삭제 시 오류 반환 테스트
     * 존재하지 않는 브랜드 ID로 삭제 요청 시 컨트롤러가 적절한 오류 응답을 반환하는지 검증한다.
     * - HTTP 상태 코드 404(Not Found)
     * - 응답에 오류 메시지 포함
     */
    @Test
    @DisplayName("API 4: 존재하지 않는 브랜드 삭제 시 오류 반환")
    void deleteBrand_ShouldReturnErrorForNonExistingBrand() throws Exception {

        // given: 존재하지 않는 브랜드 ID 및 서비스 메서드 모킹
        Long brandId = 999L;
        when(brandService.getBrandById(brandId)).thenReturn(null); // null 반환하여 브랜드 없음 표시

        // when & then: API 요청 및 오류 응답 검증
        mockMvc.perform(delete("/api/brand/{id}", brandId)) // DELETE 요청
                .andDo(print()) // 테스트 결과를 로그로 출력
                .andExpect(status().isNotFound()) // 상태 코드 404 확인
                .andExpect(jsonPath("$.error").exists()) // error 필드 존재 확인
                .andExpect(jsonPath("$.message").exists()); // message 필드 존재 확인

        // deleteBrand가 호출되지 않아야 함
        verify(brandService, never()).deleteBrand(anyLong());

    }

    /**
     * API 4: 브랜드 가격 업데이트 테스트
     * PUT /api/brand/price 엔드포인트가 브랜드의 특정 카테고리 가격을 올바르게 업데이트하는지 검증한다.
     * - HTTP 상태 코드 200(OK)
     * - 응답 JSON에 성공 상태 및 메시지 포함
     */
    @Test
    @DisplayName("API 4: 브랜드 가격 업데이트")
    void updateBrandPrice_ShouldUpdatePrice() throws Exception {

        // given: 테스트용 가격 업데이트 DTO 및 서비스 메서드 모킹
        BrandPriceUpdateDto updateDto = new BrandPriceUpdateDto();
        updateDto.setBrandName("A");
        updateDto.setCategoryName("상의");
        updateDto.setPrice(12000);

        when(brandService.updateBrandPrice(eq("A"), eq(Category.TOP), eq(12000))).thenReturn(brandA);

        // when & then: API 요청 및 응답 검증
        mockMvc.perform(put("/api/brand/price") // PUT 요청
                        .contentType(MediaType.APPLICATION_JSON) // JSON 타입 지정
                        .content(objectMapper.writeValueAsString(updateDto))) // 요청 본문에 업데이트 DTO JSON 포함
                .andDo(print()) // 테스트 결과를 로그로 출력
                .andExpect(status().isOk()) // 상태 코드 200 확인
                .andExpect(jsonPath("$.status").value("success")) // 성공 상태 확인
                .andExpect(jsonPath("$.message").exists()); // 메시지 필드 존재 확인

        // 서비스 메서드 호출 확인 (정확한 파라미터로 호출되었는지 검증)
        verify(brandService, times(1)).updateBrandPrice(eq("A"), eq(Category.TOP), eq(12000));

    }

    /**
     * API 4: 존재하지 않는 브랜드 가격 업데이트 시 오류 반환 테스트
     * 존재하지 않는 브랜드명으로 가격 업데이트 요청 시 컨트롤러가 적절한 오류 응답을 반환하는지 검증한다.
     * - HTTP 상태 코드 404(Not Found)
     * - 응답에 오류 메시지 포함
     */
    @Test
    @DisplayName("API 4: 존재하지 않는 브랜드 가격 업데이트 시 오류 반환")
    void updateBrandPrice_ShouldReturnErrorForNonExistingBrand() throws Exception {

        // given: 존재하지 않는 브랜드명을 포함한 업데이트 DTO 및 서비스 메서드 모킹
        BrandPriceUpdateDto updateDto = new BrandPriceUpdateDto();
        updateDto.setBrandName("Z");
        updateDto.setCategoryName("상의");
        updateDto.setPrice(12000);

        when(brandService.updateBrandPrice(eq("Z"), eq(Category.TOP), eq(12000))).thenReturn(null);

        // when & then: API 요청 및 오류 응답 검증
        mockMvc.perform(put("/api/brand/price") // PUT 요청
                        .contentType(MediaType.APPLICATION_JSON) // JSON 타입 지정
                        .content(objectMapper.writeValueAsString(updateDto))) // 요청 본문에 업데이트 DTO JSON 포함
                .andDo(print()) // 테스트 결과를 로그로 출력
                .andExpect(status().isNotFound()) // 상태 코드 404 확인
                .andExpect(jsonPath("$.error").exists()) // error 필드 존재 확인
                .andExpect(jsonPath("$.message").exists()); // message 필드 존재 확인

    }

    /**
     * API 4: 잘못된 카테고리로 가격 업데이트 시 오류 반환 테스트
     * 유효하지 않은 카테고리명으로 가격 업데이트 요청 시 컨트롤러가 적절한 오류 응답을 반환하는지 검증한다.
     * - HTTP 상태 코드 400(Bad Request)
     * - 응답에 오류 메시지 포함
     */
    @Test
    @DisplayName("API 4: 잘못된 카테고리로 가격 업데이트 시 오류 반환")
    void updateBrandPrice_ShouldReturnErrorForInvalidCategory() throws Exception {

        // given: 유효하지 않은 카테고리명을 포함한 업데이트 DTO 및 서비스 메서드 모킹
        BrandPriceUpdateDto updateDto = new BrandPriceUpdateDto();
        updateDto.setBrandName("A");
        updateDto.setCategoryName("존재하지않는카테고리");
        updateDto.setPrice(12000);

        when(brandService.updateBrandPrice(eq("A"), any(), eq(12000)))
                .thenThrow(new IllegalArgumentException("잘못된 카테고리 이름"));

        // when & then: API 요청 및 오류 응답 검증
        mockMvc.perform(put("/api/brand/price") // PUT 요청
                        .contentType(MediaType.APPLICATION_JSON) // JSON 타입 지정
                        .content(objectMapper.writeValueAsString(updateDto))) // 요청 본문에 업데이트 DTO JSON 포함
                .andDo(print()) // 테스트 결과를 로그로 출력
                .andExpect(status().isBadRequest()) // 상태 코드 400 확인
                .andExpect(jsonPath("$.error").value("잘못된 카테고리 이름")) // 오류 타입 확인
                .andExpect(jsonPath("$.message").exists()); // 메시지 필드 존재 확인

    }

    /**
     * BrandDto 객체 생성 헬퍼 메서드
     * 테스트에 사용할 BrandDto 객체를 생성한다.
     * @param name 브랜드명
     * @param topPrice 상의 가격
     * @param outerPrice 아우터 가격
     * @return 생성된 BrandDto 객체
     */
    private BrandDto createBrandDto(String name, int topPrice, int outerPrice) {

        BrandDto brandDto = new BrandDto();
        brandDto.setName(name);

        Map<Category, Integer> prices = new HashMap<>();
        prices.put(Category.TOP, topPrice);
        prices.put(Category.OUTER, outerPrice);

        // 기타 카테고리에 대한 기본 가격 설정
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
     * 테스트에 사용할 Brand 객체를 생성한다.
     * @param id 브랜드 ID
     * @param name 브랜드명
     * @param topPrice 상의 가격
     * @param outerPrice 아우터 가격
     * @return 생성된 Brand 객체
     */
    private Brand createBrand(Long id, String name, int topPrice, int outerPrice) {

        Brand brand = new Brand();
        brand.setId(id);
        brand.setName(name);

        Map<Category, Integer> prices = new HashMap<>();
        prices.put(Category.TOP, topPrice);
        prices.put(Category.OUTER, outerPrice);

        // 기타 카테고리에 대한 기본 가격 설정
        for (Category category : Category.values()) {
            if (!prices.containsKey(category)) {
                prices.put(category, 1000); // 기본 가격
            }
        }

        brand.setPrices(prices);

        return brand;
    }

}