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

@RestController
@RequestMapping("/api")
public class ApiController {
    @Autowired
    private BrandService brandService;

    // API 1: 카테고리별 최저가격 브랜드와 상품가격, 총액을 조회하는 API
    @GetMapping("/lowest-price-by-category")
    public ResponseEntity<?> getLowestPriceByCategory() {
        try {
            Map<Category, Map<String, Object>> lowestPriceByCategory = brandService.getLowestPriceByCategory();

            List<CategoryPriceDto> categories = new ArrayList<>();
            int totalPrice = 0;

            for (Category category : Category.values()) {
                Map<String, Object> categoryData = lowestPriceByCategory.get(category);
                if (categoryData != null) {
                    String brand = (String) categoryData.get("brand");
                    int price = (int) categoryData.get("price");

                    categories.add(CategoryPriceDto.builder()
                            .category(category.getDisplayName())
                            .brand(brand)
                            .price(String.format("%,d", price))
                            .build());

                    totalPrice += price;
                }
            }

            LowestPriceResponseDto response = LowestPriceResponseDto.builder()
                    .categories(categories)
                    .totalPrice(String.format("%,d", totalPrice))
                    .build();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "카테고리별 최저가격 조회 실패");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // API 2: 단일 브랜드로 모든 카테고리 상품을 구매할 때 최저가격에 판매하는 브랜드와 카테고리의 상품가격, 총액 조회 API
    @GetMapping("/lowest-total-price-brand")
    public ResponseEntity<?> getLowestTotalPriceBrand() {
        try {
            Map<String, Object> lowestTotalPriceBrand = brandService.getLowestTotalPriceBrand();
            return ResponseEntity.ok(lowestTotalPriceBrand);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "단일 브랜드 최저가격 조회 실패");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // API 3: 카테고리 이름으로 최저, 최고 가격 브랜드와 상품 가격을 조회하는 API
    @GetMapping("/min-max-price-by-category")
    public ResponseEntity<?> getMinMaxPriceByCategory(@RequestParam String categoryName) {
        try {
            Category category = Category.fromDisplayName(categoryName);
            Map<String, Object> minMaxPrice = brandService.getMinMaxPriceByCategory(category);
            return ResponseEntity.ok(minMaxPrice);
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "잘못된 카테고리 이름");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "카테고리별 최저/최고 가격 조회 실패");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // API 4: 브랜드 및 상품을 추가/업데이트/삭제하는 API
    @PostMapping("/brand")
    public ResponseEntity<?> createBrand(@RequestBody BrandDto brandDto) {
        try {
            Brand brand = new Brand();
            brand.setName(brandDto.getName());
            brand.setPrices(brandDto.getPrices());

            Brand savedBrand = brandService.saveBrand(brand);

            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "브랜드가 성공적으로 생성되었습니다");
            response.put("brandId", savedBrand.getId().toString());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "브랜드 생성 실패");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PutMapping("/brand/{id}")
    public ResponseEntity<?> updateBrand(@PathVariable Long id, @RequestBody BrandDto brandDto) {
        try {
            Brand existingBrand = brandService.getBrandById(id);
            if (existingBrand == null) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "브랜드를 찾을 수 없음");
                errorResponse.put("message", "ID " + id + "에 해당하는 브랜드가 존재하지 않습니다");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            existingBrand.setName(brandDto.getName());
            existingBrand.setPrices(brandDto.getPrices());

            Brand updatedBrand = brandService.saveBrand(existingBrand);

            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "브랜드가 성공적으로 업데이트되었습니다");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "브랜드 업데이트 실패");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @DeleteMapping("/brand/{id}")
    public ResponseEntity<?> deleteBrand(@PathVariable Long id) {
        try {
            Brand existingBrand = brandService.getBrandById(id);
            if (existingBrand == null) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "브랜드를 찾을 수 없음");
                errorResponse.put("message", "ID " + id + "에 해당하는 브랜드가 존재하지 않습니다");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            brandService.deleteBrand(id);

            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "브랜드가 성공적으로 삭제되었습니다");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "브랜드 삭제 실패");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PutMapping("/brand/price")
    public ResponseEntity<?> updateBrandPrice(@RequestBody BrandPriceUpdateDto updateDto) {
        try {
            Category category = Category.fromDisplayName(updateDto.getCategoryName());
            Brand updatedBrand = brandService.updateBrandPrice(updateDto.getBrandName(), category, updateDto.getPrice());

            if (updatedBrand == null) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "브랜드를 찾을 수 없음");
                errorResponse.put("message", updateDto.getBrandName() + " 브랜드가 존재하지 않습니다");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "브랜드 가격이 성공적으로 업데이트되었습니다");

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "잘못된 카테고리 이름");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "브랜드 가격 업데이트 실패");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}