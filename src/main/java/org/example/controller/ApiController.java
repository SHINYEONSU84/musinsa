package org.example.controller;

import org.example.dto.*;
import org.example.model.Brand;
import org.example.model.Category;
import org.example.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 무신사 코디 서비스의 REST API를 처리하는 컨트롤러
 *
 * 이 클래스는 다음 4가지 API를 제공합니다:
 * 1. 카테고리별 최저가격 브랜드와 상품가격, 총액을 조회하는 API
 * 2. 단일 브랜드로 모든 카테고리 상품을 구매할 때 최저가격인 브랜드와 카테고리별 가격, 총액을 조회하는 API
 * 3. 카테고리 이름으로 최저, 최고 가격 브랜드와 상품 가격을 조회하는 API
 * 4. 브랜드 및 상품을 추가/업데이트/삭제하는 API
 */
@RestController
@RequestMapping("/api")
public class ApiController {

    /**
     * 브랜드 관련 비즈니스 로직을 처리하는 서비스 객체
     */
    @Autowired
    private BrandService brandService;

    /**
     * API 1: 카테고리별 최저가격 브랜드와 상품가격, 총액을 조회하는 API
     *
     * 모든 카테고리(8개)에 대해 최저가를 제공하는 브랜드와 가격 정보를 조회하고,
     * 각 카테고리의 최저가 합산 총액을 계산하여 반환합니다.
     * 같은 최저가격을 제공하는 브랜드가 여러 개일 경우 모두 표시합니다.
     *
     * @return ResponseEntity 객체로 HTTP 상태 코드와 함께 조회 결과 또는 오류 메시지를 반환
     */
    @GetMapping("/lowest-price-by-category")
    public ResponseEntity<?> getLowestPriceByCategory() {
        try {
            // 서비스 계층을 통해 카테고리별 최저가격 정보를 조회
            Map<Category, Map<String, Object>> lowestPriceByCategory = brandService.getLowestPriceByCategory();

            List<CategoryPriceDto> categories = new ArrayList<>();
            int totalPrice = 0;

            // 각 카테고리의 최저가격 정보를 DTO로 변환하고 총액 계산
            for (Category category : Category.values()) {
                Map<String, Object> categoryData = lowestPriceByCategory.get(category);
                if (categoryData != null) {
                    String brand = (String) categoryData.get("brand");
                    int price = (int) categoryData.get("price");

                    // DTO 생성 및 리스트에 추가
                    categories.add(CategoryPriceDto.builder()
                            .category(category.getDisplayName())
                            .brand(brand)
                            .price(String.format("%,d", price))  // 천 단위 구분자(콤마) 적용
                            .build());

                    // 총액 누적
                    totalPrice += price;
                }
            }

            // 응답 DTO 생성
            LowestPriceResponseDto response = LowestPriceResponseDto.builder()
                    .categories(categories)
                    .totalPrice(String.format("%,d", totalPrice))  // 천 단위 구분자(콤마) 적용
                    .build();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 오류 발생 시 오류 응답 생성
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "카테고리별 최저가격 조회 실패");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * API 2: 단일 브랜드로 모든 카테고리 상품을 구매할 때 최저가격에 판매하는 브랜드와
     * 카테고리의 상품가격, 총액을 조회하는 API
     *
     * 단일 브랜드에서 모든 카테고리의 상품을 구매할 때 총액이 가장 저렴한 브랜드를 찾고,
     * 해당 브랜드의 각 카테고리별 가격과 총액을 반환합니다.
     *
     * @return ResponseEntity 객체로 HTTP 상태 코드와 함께 조회 결과 또는 오류 메시지를 반환
     */
    @GetMapping("/lowest-total-price-brand")
    public ResponseEntity<?> getLowestTotalPriceBrand() {
        try {
            // 서비스 계층을 통해 최저 총액 브랜드 정보를 조회
            Map<String, Object> lowestTotalPriceBrand = brandService.getLowestTotalPriceBrand();
            return ResponseEntity.ok(lowestTotalPriceBrand);
        } catch (Exception e) {
            // 오류 발생 시 오류 응답 생성
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "단일 브랜드 최저가격 조회 실패");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * API 3: 카테고리 이름으로 최저, 최고 가격 브랜드와 상품 가격을 조회하는 API
     *
     * 지정된 카테고리에서 최저가격과 최고가격을 제공하는 브랜드와 가격 정보를 조회합니다.
     * 같은 최저/최고 가격을 제공하는 브랜드가 여러 개일 경우 모두 표시합니다.
     *
     * @param categoryName 조회할 카테고리 이름(표시명)
     * @return ResponseEntity 객체로 HTTP 상태 코드와 함께 조회 결과 또는 오류 메시지를 반환
     */
    @GetMapping("/min-max-price-by-category")
    public ResponseEntity<?> getMinMaxPriceByCategory(@RequestParam String categoryName) {
        try {
            // 카테고리 이름을 Category 열거형으로 변환
            Category category = Category.fromDisplayName(categoryName);
            // 서비스 계층을 통해 해당 카테고리의 최저/최고 가격 정보를 조회
            Map<String, Object> minMaxPrice = brandService.getMinMaxPriceByCategory(category);
            return ResponseEntity.ok(minMaxPrice);
        } catch (IllegalArgumentException e) {
            // 유효하지 않은 카테고리 이름일 경우 오류 응답 생성
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "잘못된 카테고리 이름");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            // 기타 오류 발생 시 오류 응답 생성
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "카테고리별 최저/최고 가격 조회 실패");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * API 4-1: 브랜드 생성 API
     *
     * 새로운 브랜드와 해당 브랜드의 카테고리별 가격 정보를 등록합니다.
     *
     * @param brandDto 생성할 브랜드 정보(이름, 카테고리별 가격)
     * @return ResponseEntity 객체로 HTTP 상태 코드와 함께 생성 결과 또는 오류 메시지를 반환
     */
    @PostMapping("/brand")
    public ResponseEntity<?> createBrand(@RequestBody BrandDto brandDto) {
        try {
            // BrandDto를 Brand 엔티티로 변환
            Brand brand = new Brand();
            brand.setName(brandDto.getName());
            brand.setPrices(brandDto.getPrices());

            // 서비스 계층을 통해 브랜드 저장
            Brand savedBrand = brandService.saveBrand(brand);

            // 성공 응답 생성
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "브랜드가 성공적으로 생성되었습니다");
            response.put("brandId", savedBrand.getId().toString());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            // 오류 발생 시 오류 응답 생성
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "브랜드 생성 실패");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * API 4-2: 브랜드 업데이트 API
     *
     * 기존 브랜드의 정보(이름, 카테고리별 가격)를 수정합니다.
     *
     * @param id 수정할 브랜드의 ID
     * @param brandDto 수정할 브랜드 정보(이름, 카테고리별 가격)
     * @return ResponseEntity 객체로 HTTP 상태 코드와 함께 수정 결과 또는 오류 메시지를 반환
     */
    @PutMapping("/brand/{id}")
    public ResponseEntity<?> updateBrand(@PathVariable Long id, @RequestBody BrandDto brandDto) {
        try {
            // 기존 브랜드 조회
            Brand existingBrand = brandService.getBrandById(id);
            if (existingBrand == null) {
                // 존재하지 않는 브랜드일 경우 오류 응답 생성
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "브랜드를 찾을 수 없음");
                errorResponse.put("message", "ID " + id + "에 해당하는 브랜드가 존재하지 않습니다");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            // 브랜드 정보 업데이트
            existingBrand.setName(brandDto.getName());
            existingBrand.setPrices(brandDto.getPrices());

            // 서비스 계층을 통해 수정된 브랜드 저장
            Brand updatedBrand = brandService.saveBrand(existingBrand);

            // 성공 응답 생성
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "브랜드가 성공적으로 업데이트되었습니다");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 오류 발생 시 오류 응답 생성
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "브랜드 업데이트 실패");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * API 4-3: 브랜드 삭제 API
     *
     * 지정된 ID의 브랜드를 삭제합니다.
     *
     * @param id 삭제할 브랜드의 ID
     * @return ResponseEntity 객체로 HTTP 상태 코드와 함께 삭제 결과 또는 오류 메시지를 반환
     */
    @DeleteMapping("/brand/{id}")
    public ResponseEntity<?> deleteBrand(@PathVariable Long id) {
        try {
            // 기존 브랜드 조회
            Brand existingBrand = brandService.getBrandById(id);
            if (existingBrand == null) {
                // 존재하지 않는 브랜드일 경우 오류 응답 생성
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "브랜드를 찾을 수 없음");
                errorResponse.put("message", "ID " + id + "에 해당하는 브랜드가 존재하지 않습니다");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            // 서비스 계층을 통해 브랜드 삭제
            brandService.deleteBrand(id);

            // 성공 응답 생성
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "브랜드가 성공적으로 삭제되었습니다");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 오류 발생 시 오류 응답 생성
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "브랜드 삭제 실패");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * API 4-4: 브랜드 가격 업데이트 API
     *
     * 특정 브랜드의 특정 카테고리 상품 가격을 업데이트합니다.
     *
     * @param updateDto 업데이트할 가격 정보(브랜드명, 카테고리명, 가격)
     * @return ResponseEntity 객체로 HTTP 상태 코드와 함께 업데이트 결과 또는 오류 메시지를 반환
     */
    @PutMapping("/brand/price")
    public ResponseEntity<?> updateBrandPrice(@RequestBody BrandPriceUpdateDto updateDto) {
        try {
            // 카테고리 이름을 Category 열거형으로 변환
            Category category = Category.fromDisplayName(updateDto.getCategoryName());
            // 서비스 계층을 통해 브랜드 가격 업데이트
            Brand updatedBrand = brandService.updateBrandPrice(updateDto.getBrandName(), category, updateDto.getPrice());

            if (updatedBrand == null) {
                // 존재하지 않는 브랜드일 경우 오류 응답 생성
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "브랜드를 찾을 수 없음");
                errorResponse.put("message", updateDto.getBrandName() + " 브랜드가 존재하지 않습니다");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            // 성공 응답 생성
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "브랜드 가격이 성공적으로 업데이트되었습니다");

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            // 유효하지 않은 카테고리 이름일 경우 오류 응답 생성
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "잘못된 카테고리 이름");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            // 기타 오류 발생 시 오류 응답 생성
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "브랜드 가격 업데이트 실패");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}