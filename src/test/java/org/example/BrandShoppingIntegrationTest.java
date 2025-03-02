package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.BrandDto;
import org.example.model.Brand;
import org.example.model.Category;
import org.example.repository.BrandRepository;
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

    @BeforeEach
    void setUp() {
        LoggerFactory.getLogger("org.example").info("Starting integration test");
    }

    @AfterEach
    void tearDown() {
        LoggerFactory.getLogger("org.example").info("Integration test finished");
    }

    @Test
    @DisplayName("API 1: 카테고리별 최저가격 조회")
    void getLowestPriceByCategory_ShouldReturnCorrectData() throws Exception {
        mockMvc.perform(get("/api/lowest-price-by-category"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categories").isArray())
                .andExpect(jsonPath("$.totalPrice").exists());
    }

    @Test
    @DisplayName("API 2: 단일 브랜드 최저 총액 조회")
    void getLowestTotalPriceBrand_ShouldReturnCorrectData() throws Exception {
        mockMvc.perform(get("/api/lowest-total-price-brand"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.최저가.브랜드").exists())
                .andExpect(jsonPath("$.최저가.카테고리").isArray())
                .andExpect(jsonPath("$.최저가.총액").exists());
    }

    @Test
    @DisplayName("API 3: 카테고리별 최저/최고 가격 조회")
    void getMinMaxPriceByCategory_ShouldReturnCorrectData() throws Exception {
        mockMvc.perform(get("/api/min-max-price-by-category").param("categoryName", "상의"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.카테고리").value("상의"))
                .andExpect(jsonPath("$.최저가").isArray())
                .andExpect(jsonPath("$.최고가").isArray());
    }

    @Test
    @DisplayName("API 4: 브랜드 생성, 수정, 삭제 통합 테스트")
    void createUpdateDeleteBrand_ShouldWorkCorrectly() throws Exception {
        BrandDto brandDto = new BrandDto("Test Brand", new HashMap<>());
        for (Category category : Category.values()) {
            brandDto.getPrices().put(category, 5000);
        }

        MvcResult createResult = mockMvc.perform(post("/api/brand")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(brandDto)))
                .andExpect(status().isCreated())
                .andReturn();

        String brandId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("brandId").asText();
        assertNotNull(brandId);

        brandDto.setName("Updated Brand");
        mockMvc.perform(put("/api/brand/" + brandId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(brandDto)))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/brand/" + brandId))
                .andExpect(status().isOk());
    }
}
