package org.example;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.example.controller.WebController;
import org.example.dto.BrandDto;
import org.example.model.Brand;
import org.example.model.Category;
import org.example.repository.BrandRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class BrandShoppingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BrandRepository brandRepository;

    // 로그 레벨을 저장할 변수
    private Level originalLogLevel;

    @BeforeEach
    void setUp() {
        // WebController의 로그 레벨을 OFF로 설정 (테스트 중 로그 출력 방지)
        Logger logger = (Logger) LoggerFactory.getLogger(WebController.class);
        originalLogLevel = logger.getLevel();
        logger.setLevel(Level.OFF);

        // 다른 관련 로거도 필요하면 비슷하게 설정
        Logger rootLogger = (Logger) LoggerFactory.getLogger("com.example");
        rootLogger.setLevel(Level.WARN); // WARN 이상 로그만 표시
    }

    @AfterEach
    void tearDown() {
        // 테스트 후 로그 레벨 복원
        Logger logger = (Logger) LoggerFactory.getLogger(WebController.class);
        logger.setLevel(originalLogLevel);

        Logger rootLogger = (Logger) LoggerFactory.getLogger("com.example");
        rootLogger.setLevel(Level.INFO); // 기본 레벨로 복원
    }

    @Test
    @DisplayName("스프링 컨텍스트 로드 확인")
    void contextLoads() {
        // 스프링 컨텍스트 로드 확인
    }

    @Test
    @DisplayName("API 1: 카테고리별 최저가격 조회 통합 테스트")
    void getLowestPriceByCategory_ShouldReturnCorrectData() throws Exception {
        // when
        MvcResult result = mockMvc.perform(get("/api/lowest-price-by-category"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categories").isArray())
                .andExpect(jsonPath("$.totalPrice").exists())
                .andReturn();

        // then
        String content = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(content);

        // 결과에 모든 카테고리가 있는지 확인
        assertEquals(Category.values().length, jsonNode.get("categories").size());

        // 총액이 올바른지 확인
        assertNotNull(jsonNode.get("totalPrice").asText());
    }

    @Test
    @DisplayName("API 2: 단일 브랜드 최저 총액 조회 통합 테스트")
    void getLowestTotalPriceBrand_ShouldReturnCorrectData() throws Exception {
        // when
        MvcResult result = mockMvc.perform(get("/api/lowest-total-price-brand"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.최저가.브랜드").exists())
                .andExpect(jsonPath("$.최저가.카테고리").isArray())
                .andExpect(jsonPath("$.최저가.총액").exists())
                .andReturn();

        // then
        String content = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(content);

        // 모든 카테고리가 있는지 확인
        assertEquals(Category.values().length, jsonNode.get("최저가").get("카테고리").size());

        // 브랜드와 총액이 존재하는지 확인
        assertNotNull(jsonNode.get("최저가").get("브랜드").asText());
        assertNotNull(jsonNode.get("최저가").get("총액").asText());
    }

    @Test
    @DisplayName("API 3: 카테고리별 최저/최고 가격 조회 통합 테스트")
    void getMinMaxPriceByCategory_ShouldReturnCorrectDataForValidCategory() throws Exception {
        // when
        MvcResult result = mockMvc.perform(get("/api/min-max-price-by-category?categoryName=상의"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.카테고리").value("상의"))
                .andExpect(jsonPath("$.최저가").isArray())
                .andExpect(jsonPath("$.최고가").isArray())
                .andReturn();

        // then
        String content = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(content);

        assertEquals("상의", jsonNode.get("카테고리").asText());
        assertNotNull(jsonNode.get("최저가").get(0).get("브랜드").asText());
        assertNotNull(jsonNode.get("최저가").get(0).get("가격").asText());
        assertNotNull(jsonNode.get("최고가").get(0).get("브랜드").asText());
        assertNotNull(jsonNode.get("최고가").get(0).get("가격").asText());
    }

    @Test
    @DisplayName("API 3: 잘못된 카테고리명 입력 시 오류 반환")
    void getMinMaxPriceByCategory_ShouldReturnErrorForInvalidCategory() throws Exception {
        // 잘못된 카테고리 이름으로 요청 시 400 Bad Request 응답 기대
        mockMvc.perform(get("/api/min-max-price-by-category?categoryName=잘못된카테고리"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("API 4: 브랜드 생성, 수정, 삭제 통합 테스트")
    void createAndUpdateAndDeleteBrand_ShouldWorkCorrectly() throws Exception {
        // 1. 새 브랜드 생성
        BrandDto brandDto = new BrandDto();
        brandDto.setName("Test Brand");

        Map<Category, Integer> prices = new HashMap<>();
        for (Category category : Category.values()) {
            prices.put(category, 5000);
        }
        brandDto.setPrices(prices);

        MvcResult createResult = mockMvc.perform(post("/api/brand")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(brandDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.brandId").exists())
                .andReturn();

        // 생성된 브랜드 ID 추출
        String content = createResult.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(content);
        String brandIdStr = jsonNode.get("brandId").asText();
        Long brandId = Long.parseLong(brandIdStr);

        // DB에서 브랜드 확인
        Brand createdBrand = brandRepository.findById(brandId).orElse(null);
        assertNotNull(createdBrand);
        assertEquals("Test Brand", createdBrand.getName());
        assertEquals(Category.values().length, createdBrand.getPrices().size());

        // 2. 브랜드 업데이트
        brandDto.setName("Updated Test Brand");
        prices.put(Category.TOP, 8000);
        brandDto.setPrices(prices);

        mockMvc.perform(put("/api/brand/{id}", brandId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(brandDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));

        // DB에서 업데이트된 브랜드 확인
        Brand updatedBrand = brandRepository.findById(brandId).orElse(null);
        assertNotNull(updatedBrand);
        assertEquals("Updated Test Brand", updatedBrand.getName());
        assertEquals(8000, updatedBrand.getPrices().get(Category.TOP));

        // 3. 브랜드 삭제
        mockMvc.perform(delete("/api/brand/{id}", brandId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));

        // DB에서 삭제 확인
        assertFalse(brandRepository.existsById(brandId));
    }

    @Test
    @DisplayName("전체 API 워크플로우 통합 테스트")
    void fullApiWorkflow_ShouldReturnCorrectData() throws Exception {
        // 1. 카테고리별 최저가격 조회
        mockMvc.perform(get("/api/lowest-price-by-category"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categories").isArray());

        // 2. 단일 브랜드 최저 총액 조회
        mockMvc.perform(get("/api/lowest-total-price-brand"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.최저가.브랜드").exists());

        // 3. 각 카테고리별 최저/최고 가격 조회
        for (Category category : Category.values()) {
            mockMvc.perform(get("/api/min-max-price-by-category?categoryName=" + category.getDisplayName()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.카테고리").value(category.getDisplayName()))
                    .andExpect(jsonPath("$.최저가").isArray())
                    .andExpect(jsonPath("$.최고가").isArray());
        }

        // 4. 새 브랜드 생성
        BrandDto brandDto = new BrandDto();
        brandDto.setName("Workflow Test Brand");

        Map<Category, Integer> prices = new HashMap<>();
        for (Category category : Category.values()) {
            prices.put(category, 6000);
        }
        brandDto.setPrices(prices);

        MvcResult createResult = mockMvc.perform(post("/api/brand")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(brandDto)))
                .andExpect(status().isCreated())
                .andReturn();

        // 생성된 브랜드 ID 추출
        String content = createResult.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(content);
        String brandIdStr = jsonNode.get("brandId").asText();
        Long brandId = Long.parseLong(brandIdStr);

        // 5. 브랜드 삭제
        mockMvc.perform(delete("/api/brand/{id}", brandId))
                .andExpect(status().isOk());

        // 데이터 일관성 확인
        List<Brand> brands = brandRepository.findAll();
        assertFalse(brands.isEmpty());

        // 각 브랜드가 모든 카테고리의 가격을 가지고 있는지 확인
        for (Brand brand : brands) {
            for (Category category : Category.values()) {
                assertNotNull(brand.getPrices().get(category),
                        String.format("브랜드 %s의 %s 카테고리 가격이 없습니다", brand.getName(), category.getDisplayName()));
            }
        }
    }

    @Test
    @DisplayName("웹 인터페이스 접근 테스트 - 메인 페이지")
    void webInterfaceAccess_ShouldReturnMainPage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    @DisplayName("웹 인터페이스 접근 테스트 - 카테고리별 최저가격 페이지")
    void webInterfaceAccess_ShouldReturnLowestPricePage() throws Exception {
        mockMvc.perform(get("/lowest-price-by-category"))
                .andExpect(status().isOk())
                .andExpect(view().name("lowest-price-by-category"))
                .andExpect(model().attributeExists("categories"))
                .andExpect(model().attributeExists("totalPrice"));
    }

    @Test
    @DisplayName("웹 인터페이스 접근 테스트 - 브랜드 관리 페이지")
    void webInterfaceAccess_ShouldReturnManageBrandsPage() throws Exception {
        mockMvc.perform(get("/manage-brands"))
                .andExpect(status().isOk())
                .andExpect(view().name("manage-brands"))
                .andExpect(model().attributeExists("brands"))
                .andExpect(model().attributeExists("categoryNames"))
                .andExpect(model().attributeExists("categories"));
    }
}