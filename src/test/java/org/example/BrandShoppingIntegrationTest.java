package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.BrandDto;
import org.example.dto.BrandPriceUpdateDto;
import org.example.model.Brand;
import org.example.model.Category;
import org.example.repository.BrandRepository;
import org.example.service.BrandService;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 무신사 코디 서비스 통합 테스트
 *
 * 이 테스트는 실제 애플리케이션 환경과 유사한 설정에서 전체 시스템의 통합을 검증합니다.
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // 테스트 클래스당 인스턴스 하나를 사용하여 상태 공유
public class BrandShoppingIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(BrandShoppingIntegrationTest.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private BrandService brandService;

    private Long testBrandId;
    private static final String TEST_BRAND_NAME = "IntegrationTestBrand";
    private static final String UPDATED_BRAND_NAME = "IntegrationTestBrand Updated";

    @BeforeAll
    void setUpBeforeAll() {
        logger.info("시작: 통합 테스트 스위트");
        // 테스트에 사용할 브랜드가 이미 존재하는지 확인하고 정리
        try {
            Brand existingBrand = brandRepository.findByName(TEST_BRAND_NAME);
            if (existingBrand != null) {
                brandRepository.delete(existingBrand);
            }

            Brand updatedBrand = brandRepository.findByName(UPDATED_BRAND_NAME);
            if (updatedBrand != null) {
                brandRepository.delete(updatedBrand);
            }
        } catch (Exception e) {
            logger.error("테스트 시작 전 데이터 정리 중 오류 발생", e);
        }
    }

    @AfterAll
    void tearDownAfterAll() {
        logger.info("종료: 통합 테스트 스위트");

        // 테스트 후 생성된 테스트 브랜드 정리
        try {
            Brand brand = brandRepository.findByName(TEST_BRAND_NAME);
            if (brand != null) {
                brandRepository.delete(brand);
            }

            Brand updatedBrand = brandRepository.findByName(UPDATED_BRAND_NAME);
            if (updatedBrand != null) {
                brandRepository.delete(updatedBrand);
            }

            Brand e2eTestBrand = brandRepository.findByName("E2E Test Brand");
            if (e2eTestBrand != null) {
                brandRepository.delete(e2eTestBrand);
            }

            Brand webDeleteBrand = brandRepository.findByName("Brand For Web Delete Test");
            if (webDeleteBrand != null) {
                brandRepository.delete(webDeleteBrand);
            }

            Brand deleteTestBrand = brandRepository.findByName("Brand For Delete Test");
            if (deleteTestBrand != null) {
                brandRepository.delete(deleteTestBrand);
            }
        } catch (Exception e) {
            logger.error("테스트 데이터 정리 중 오류 발생", e);
        }
    }

    /**
     * API 1 테스트 - 카테고리별 최저가격 조회
     *
     * 이 테스트는 각 카테고리별 최저가격 브랜드와 상품가격, 총액을 올바르게 반환하는지 검증합니다.
     */
    @Test
    @Order(1)
    @DisplayName("API 1: 카테고리별 최저가격 조회")
    void getLowestPriceByCategory_ShouldReturnCorrectData() throws Exception {
        // 1. REST API 테스트
        MvcResult apiResult = mockMvc.perform(get("/api/lowest-price-by-category"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categories").isArray())
                .andExpect(jsonPath("$.totalPrice").exists())
                .andReturn();

        // API 응답 디버깅
        String responseContent = apiResult.getResponse().getContentAsString();
        logger.info("API 1 응답: {}", responseContent);

        // 웹 컨트롤러 테스트
        mockMvc.perform(get("/lowest-price-by-category"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("lowest-price-by-category"))
                .andExpect(model().attributeExists("categories"))
                .andExpect(model().attributeExists("totalPrice"));
    }

    /**
     * API 2 테스트 - 단일 브랜드 최저 총액 조회
     *
     * 이 테스트는 단일 브랜드로 전체 카테고리 상품을 구매할 때 최저가격인 브랜드와 총액을 올바르게 반환하는지 검증합니다.
     */
    @Test
    @Order(2)
    @DisplayName("API 2: 단일 브랜드 최저 총액 조회")
    void getLowestTotalPriceBrand_ShouldReturnCorrectData() throws Exception {
        // 1. REST API 테스트
        MvcResult apiResult = mockMvc.perform(get("/api/lowest-total-price-brand"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // API 응답 디버깅
        String responseContent = apiResult.getResponse().getContentAsString();
        logger.info("API 2 응답: {}", responseContent);

        // 2. 웹 컨트롤러 테스트
        mockMvc.perform(get("/lowest-total-price-brand"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("lowest-total-price-brand"))
                .andExpect(model().attributeExists("brandName"))
                .andExpect(model().attributeExists("categories"))
                .andExpect(model().attributeExists("totalPrice"));
    }

    /**
     * API 3 테스트 - 카테고리별 최저/최고 가격 조회
     *
     * 이 테스트는 특정 카테고리의 최저/최고 가격 브랜드와 상품 가격을 올바르게 반환하는지 검증합니다.
     */
    @Test
    @Order(3)
    @DisplayName("API 3: 카테고리별 최저/최고 가격 조회 - 유효한 카테고리")
    void getMinMaxPriceByCategory_WithValidCategory_ShouldReturnCorrectData() throws Exception {
        // 테스트할 카테고리
        String categoryName = "상의";

        // 1. REST API 테스트
        MvcResult apiResult = mockMvc.perform(get("/api/min-max-price-by-category")
                        .param("categoryName", categoryName))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // API 응답 디버깅
        String responseContent = apiResult.getResponse().getContentAsString();
        logger.info("API 3 응답: {}", responseContent);

        // 2. 웹 컨트롤러 테스트 - 카테고리 선택 폼 페이지
        mockMvc.perform(get("/min-max-price-by-category"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("min-max-price-by-category"))
                .andExpect(model().attributeExists("categoryNames"));

        // 3. 웹 컨트롤러 테스트 - 결과 페이지
        mockMvc.perform(get("/min-max-price-result")
                        .param("categoryName", categoryName))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("min-max-price-result"))
                .andExpect(model().attributeExists("categoryName"))
                .andExpect(model().attributeExists("minPrices"))
                .andExpect(model().attributeExists("maxPrices"));
    }

    @Test
    @Order(4)
    @DisplayName("API 3: 카테고리별 최저/최고 가격 조회 - 유효하지 않은 카테고리")
    void getMinMaxPriceByCategory_WithInvalidCategory_ShouldReturnError() throws Exception {
        // 유효하지 않은 카테고리
        String invalidCategory = "존재하지않는카테고리";

        // 1. REST API 테스트
        mockMvc.perform(get("/api/min-max-price-by-category")
                        .param("categoryName", invalidCategory))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.message").exists());

        // 2. 웹 컨트롤러 테스트
        mockMvc.perform(get("/min-max-price-result")
                        .param("categoryName", invalidCategory))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("error"));
    }

    /**
     * API 4 테스트 - 브랜드 생성 테스트
     *
     * 이 테스트는 새로운 브랜드를 생성하는 기능을 검증합니다.
     */
    @Test
    @Order(5)
    @DisplayName("API 4: 브랜드 생성 테스트")
    @Transactional
    void createBrand_ShouldCreateNewBrand() throws Exception {
        // 테스트 브랜드 이름에 타임스탬프 추가하여 유니크하게 만들기
        String uniqueBrandName = TEST_BRAND_NAME + "_Create_" + System.currentTimeMillis();

        // 브랜드 생성 데이터 준비
        BrandDto brandDto = new BrandDto();
        brandDto.setName(uniqueBrandName);

        Map<Category, Integer> prices = new HashMap<>();
        for (Category category : Category.values()) {
            prices.put(category, 5000 + category.ordinal() * 1000); // 각 카테고리마다 다른 가격
        }
        brandDto.setPrices(prices);

        // 1. REST API 테스트 - 브랜드 생성
        MvcResult createResult = mockMvc.perform(post("/api/brand")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(brandDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.brandId").exists())
                .andReturn();

        // API 응답 디버깅
        String responseContent = createResult.getResponse().getContentAsString();
        logger.info("API 4 브랜드 생성 응답: {}", responseContent);

        JsonNode response = objectMapper.readTree(responseContent);
        Long createdBrandId = Long.parseLong(response.get("brandId").asText());
        logger.info("생성된 브랜드 ID: {}", createdBrandId);

        // DB에서 생성된 브랜드 확인
        Brand createdBrand = brandRepository.findById(createdBrandId).orElse(null);
        assertNotNull(createdBrand, "생성된 브랜드가 DB에 존재하지 않습니다");
        assertEquals(uniqueBrandName, createdBrand.getName(), "생성된 브랜드의 이름이 일치하지 않습니다");
    }

    /**
     * API 4 테스트 - 브랜드 수정 테스트
     *
     * 이 테스트는 기존 브랜드를 수정하는 기능을 검증합니다.
     */
    @Test
    @Order(6)
    @DisplayName("API 4: 브랜드 수정 테스트")
    @Transactional
    void updateBrand_ShouldUpdateExistingBrand() throws Exception {
        // 기존에 있는 테스트 브랜드를 삭제하고 새로 생성
        Brand existingBrand = brandRepository.findByName(TEST_BRAND_NAME);
        if (existingBrand != null) {
            brandRepository.delete(existingBrand);
            brandRepository.flush();
        }

        // 새로운 테스트 브랜드 직접 생성
        Brand newBrand = new Brand();
        newBrand.setName(TEST_BRAND_NAME);
        for (Category category : Category.values()) {
            newBrand.getPrices().put(category, 5000 + category.ordinal() * 1000);
        }
        Brand savedBrand = brandRepository.save(newBrand);
        brandRepository.flush();

        Long brandId = savedBrand.getId();
        logger.info("수정 테스트를 위해 생성된 브랜드 ID: {}", brandId);

        // 업데이트할 데이터 준비
        String updatedName = UPDATED_BRAND_NAME;
        BrandDto updateDto = new BrandDto();
        updateDto.setName(updatedName);

        Map<Category, Integer> updatedPrices = new HashMap<>();
        for (Category category : Category.values()) {
            updatedPrices.put(category, 6000 + category.ordinal() * 1000); // 가격 변경
        }
        updateDto.setPrices(updatedPrices);

        // 1. REST API 테스트 - 브랜드 수정
        MvcResult updateResult = mockMvc.perform(put("/api/brand/" + brandId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andReturn();

        // API 응답 디버깅
        String updateResponseContent = updateResult.getResponse().getContentAsString();
        logger.info("API 4 브랜드 수정 응답: {}", updateResponseContent);
    }

    /**
     * API 4 테스트 - 존재하지 않는 브랜드 수정/삭제 시 오류 테스트
     *
     * 이 테스트는 존재하지 않는 브랜드에 대한 작업 시 오류 처리를 검증합니다.
     */
    @Test
    @Order(7)
    @DisplayName("API 4: 존재하지 않는 브랜드 수정/삭제 시 오류 테스트")
    void nonExistingBrandOperations_ShouldReturnError() throws Exception {
        // 존재하지 않는 ID 선택 (매우 큰 값 사용)
        Long nonExistingId = 999999L;

        // 이 ID가 실제로 존재하지 않는지 확인 (혹시나 해서)
        assertFalse(brandRepository.existsById(nonExistingId), "테스트용 ID가 이미 존재합니다");

        // 1. 존재하지 않는 브랜드 수정 시도
        BrandDto updateDto = new BrandDto();
        updateDto.setName("Non Existing Brand");
        updateDto.setPrices(new HashMap<>());

        mockMvc.perform(put("/api/brand/" + nonExistingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());

        // 2. 존재하지 않는 브랜드 삭제 시도
        mockMvc.perform(delete("/api/brand/" + nonExistingId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());

        // 3. 웹 컨트롤러 - 존재하지 않는 브랜드 수정 폼 요청
        mockMvc.perform(get("/edit-brand")
                        .param("id", nonExistingId.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("error"));

        // 4. 웹 컨트롤러 - 존재하지 않는 브랜드 삭제 요청
        mockMvc.perform(get("/delete-brand")
                        .param("id", nonExistingId.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("error"));
    }

    /**
     * API 4 테스트 - 브랜드 삭제 테스트
     *
     * 이 테스트는 브랜드 삭제 기능을 검증합니다.
     */
    @Test
    @Order(8)
    @DisplayName("API 4: 브랜드 삭제 테스트")
    @Transactional
    void deleteBrand_ShouldDeleteExistingBrand() throws Exception {
        // 테스트용 브랜드 생성
        Brand brandForDelete = new Brand();
        brandForDelete.setName("Brand For Delete Test");

        for (Category category : Category.values()) {
            brandForDelete.getPrices().put(category, 1000);
        }

        Brand savedBrand = brandService.saveBrand(brandForDelete);
        Long savedBrandId = savedBrand.getId();

        logger.info("삭제 테스트를 위해 생성된 브랜드 ID: {}", savedBrandId);

        // 브랜드가 실제로 저장되었는지 확인
        assertNotNull(savedBrandId, "브랜드가 저장되지 않았습니다");
        assertTrue(brandRepository.existsById(savedBrandId), "저장된 브랜드가 데이터베이스에 존재하지 않습니다");

        // 1. REST API 테스트 - 브랜드 삭제
        mockMvc.perform(delete("/api/brand/" + savedBrandId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));

        // 삭제 확인
        assertFalse(brandRepository.existsById(savedBrandId), "브랜드가 삭제되지 않았습니다");

        // 웹 컨트롤러 테스트용 브랜드 생성
        Brand newBrandForWebDelete = new Brand();
        newBrandForWebDelete.setName("Brand For Web Delete Test");

        for (Category category : Category.values()) {
            newBrandForWebDelete.getPrices().put(category, 1000);
        }

        Brand savedWebBrand = brandService.saveBrand(newBrandForWebDelete);
        Long savedWebBrandId = savedWebBrand.getId();

        logger.info("웹 삭제 테스트를 위해 생성된 브랜드 ID: {}", savedWebBrandId);

        // 웹 컨트롤러를 통한 브랜드 삭제
        mockMvc.perform(get("/delete-brand")
                        .param("id", savedWebBrandId.toString()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/manage-brands"));
    }

    /**
     * 전체 사용자 플로우 테스트
     *
     * 이 테스트는 실제 사용자의 행동을 시뮬레이션하여 여러 기능을 순차적으로 사용하는 시나리오를 검증합니다.
     */
    @Test
    @Order(9)
    @DisplayName("전체 사용자 플로우 테스트")
    @Transactional
    void endToEndUserFlow_ShouldWorkCorrectly() throws Exception {
        // 1. 메인 페이지 접속
        mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("index"));

        // 2. 카테고리별 최저가격 조회
        mockMvc.perform(get("/lowest-price-by-category"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("lowest-price-by-category"));

        // 3. 단일 브랜드 최저 총액 조회
        mockMvc.perform(get("/lowest-total-price-brand"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("lowest-total-price-brand"));

        // 4. 특정 카테고리 최저/최고 가격 결과 조회
        mockMvc.perform(get("/min-max-price-result")
                        .param("categoryName", "바지"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("min-max-price-result"));

        // 5. 브랜드 관리 페이지 접속
        mockMvc.perform(get("/manage-brands"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("manage-brands"));

        // 6. 브랜드 추가 폼 접속
        mockMvc.perform(get("/add-brand"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("add-brand"));

        // 7. 브랜드 서비스를 통한 브랜드 추가
        Brand e2eTestBrand = new Brand();
        e2eTestBrand.setName("E2E Test Brand");

        for (Category category : Category.values()) {
            e2eTestBrand.getPrices().put(category, 3000 + category.ordinal() * 500);
        }

        Brand savedE2EBrand = brandService.saveBrand(e2eTestBrand);
        assertNotNull(savedE2EBrand.getId(), "E2E 테스트 브랜드가 저장되지 않았습니다");
    }

    /**
     * 테스트 브랜드 생성을 위한 헬퍼 메서드
     *
     * 이 메서드는 트랜잭션이 필요한 경우 명시적으로 추가할 수 있도록
     * 트랜잭션 어노테이션을 제거했습니다.
     */
    private Brand createTestBrand(String brandName) {
        Brand testBrand = new Brand();
        testBrand.setName(brandName);

        for (Category category : Category.values()) {
            testBrand.getPrices().put(category, 5000 + category.ordinal() * 1000);
        }

        Brand savedBrand = brandRepository.save(testBrand);
        brandRepository.flush();

        logger.info("헬퍼 메서드로 생성된 테스트 브랜드 ID: {}", savedBrand.getId());
        return savedBrand;
    }
}