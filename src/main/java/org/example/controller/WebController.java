package org.example.controller;

import org.example.model.Brand;
import org.example.model.Category;
import org.example.service.BrandService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class WebController {

    private static final Logger logger = LoggerFactory.getLogger(WebController.class);

    @Autowired
    private BrandService brandService;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    // API 1: 카테고리별 최저가격 브랜드와 상품가격, 총액을 조회
    @GetMapping("/lowest-price-by-category")
    public String lowestPriceByCategory(Model model) {
        try {
            Map<Category, Map<String, Object>> lowestPriceByCategory = brandService.getLowestPriceByCategory();

            // 응답 형식 준비
            List<Map<String, Object>> categoryList = new ArrayList<>();
            int totalPrice = 0;

            for (Category category : Category.values()) {
                Map<String, Object> categoryData = lowestPriceByCategory.get(category);
                if (categoryData != null) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("category", category.getDisplayName());
                    data.put("brand", categoryData.get("brand"));
                    data.put("price", categoryData.get("price"));
                    categoryList.add(data);

                    totalPrice += (int) categoryData.get("price");
                }
            }

            model.addAttribute("categories", categoryList);
            model.addAttribute("totalPrice", String.format("%,d", totalPrice));

            return "lowest-price-by-category";
        } catch (Exception e) {
            logger.error("API 1 오류: ", e);
            model.addAttribute("error", "카테고리별 최저가격 조회 실패: " + e.getMessage());
            return "error";
        }
    }

    // API 2: 단일 브랜드로 모든 카테고리 상품을 구매할 때 최저가격 브랜드 조회
    @GetMapping("/lowest-total-price-brand")
    public String lowestTotalPriceBrand(Model model) {
        try {
            Map<String, Object> result = brandService.getLowestTotalPriceBrand();

            // 디버그용 로깅
            logger.info("API 2 결과: {}", objectMapper.writeValueAsString(result));

            // 모델에 데이터 추가
            model.addAttribute("brandName", ((Map<String, Object>)result.get("최저가")).get("브랜드"));
            model.addAttribute("categories", ((Map<String, Object>)result.get("최저가")).get("카테고리"));
            model.addAttribute("totalPrice", ((Map<String, Object>)result.get("최저가")).get("총액"));

            return "lowest-total-price-brand";
        } catch (Exception e) {
            logger.error("API 2 오류: ", e);
            model.addAttribute("error", "단일 브랜드 최저 총액 조회 실패: " + e.getMessage());
            return "error";
        }
    }

    // API 3: 카테고리 이름으로 최저, 최고 가격 브랜드와 상품 가격을 조회
    @GetMapping("/min-max-price-by-category")
    public String minMaxPriceByCategory(Model model) {
        try {
            List<String> categoryNames = Arrays.stream(Category.values())
                    .map(Category::getDisplayName)
                    .collect(Collectors.toList());

            model.addAttribute("categoryNames", categoryNames);
            return "min-max-price-by-category";
        } catch (Exception e) {
            logger.error("API 3 폼 오류: ", e);
            model.addAttribute("error", "카테고리 조회 실패: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/min-max-price-result")
    public String minMaxPriceResult(@RequestParam String categoryName, Model model) {
        try {
            Category category = Category.fromDisplayName(categoryName);
            Map<String, Object> result = brandService.getMinMaxPriceByCategory(category);

            // 디버그용 로깅
            logger.info("API 3 결과: {}", objectMapper.writeValueAsString(result));

            // 모델에 데이터 추가
            model.addAttribute("categoryName", result.get("카테고리"));
            model.addAttribute("minPrices", result.get("최저가"));
            model.addAttribute("maxPrices", result.get("최고가"));

            return "min-max-price-result";
        } catch (IllegalArgumentException e) {
            logger.error("API 3 카테고리 오류: ", e);
            model.addAttribute("error", "잘못된 카테고리 이름: " + categoryName);
            return "error";
        } catch (Exception e) {
            logger.error("API 3 결과 오류: ", e);
            model.addAttribute("error", "카테고리별 최저/최고 가격 조회 실패: " + e.getMessage());
            return "error";
        }
    }

    // API 4: 브랜드 및 상품을 추가/업데이트/삭제하는 API
    @GetMapping("/manage-brands")
    public String manageBrands(Model model) {
        try {
            List<Brand> brands = brandService.getAllBrands();
            List<String> categoryNames = Arrays.stream(Category.values())
                    .map(Category::getDisplayName)
                    .collect(Collectors.toList());

            model.addAttribute("brands", brands);
            model.addAttribute("categoryNames", categoryNames);
            model.addAttribute("categories", Category.values());

            return "manage-brands";
        } catch (Exception e) {
            logger.error("API 4 브랜드 관리 오류: ", e);
            model.addAttribute("error", "브랜드 관리 페이지 로드 실패: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/add-brand")
    public String addBrandForm(Model model) {
        try {
            model.addAttribute("categories", Category.values());
            return "add-brand";
        } catch (Exception e) {
            logger.error("브랜드 추가 폼 오류: ", e);
            model.addAttribute("error", "브랜드 추가 페이지 로드 실패: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/add-brand")
    public String addBrand(@RequestParam String brandName, @RequestParam Map<String, String> formData, Model model) {
        try {
            Brand brand = new Brand();
            brand.setName(brandName);

            for (Category category : Category.values()) {
                String priceStr = formData.get(category.name());
                if (priceStr != null && !priceStr.isEmpty()) {
                    int price = Integer.parseInt(priceStr);
                    brand.getPrices().put(category, price);
                }
            }

            brandService.saveBrand(brand);

            return "redirect:/manage-brands";
        } catch (Exception e) {
            logger.error("브랜드 추가 처리 오류: ", e);
            model.addAttribute("error", "브랜드 추가 실패: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/edit-brand")
    public String editBrandForm(@RequestParam Long id, Model model) {
        try {
            Brand brand = brandService.getBrandById(id);
            if (brand == null) {
                model.addAttribute("error", "브랜드를 찾을 수 없습니다");
                return "error";
            }

            model.addAttribute("brand", brand);
            model.addAttribute("categories", Category.values());

            return "edit-brand";
        } catch (Exception e) {
            logger.error("브랜드 수정 폼 오류: ", e);
            model.addAttribute("error", "브랜드 수정 페이지 로드 실패: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/edit-brand")
    public String editBrand(@RequestParam Long id, @RequestParam String brandName, @RequestParam Map<String, String> formData, Model model) {
        try {
            Brand brand = brandService.getBrandById(id);
            if (brand == null) {
                model.addAttribute("error", "브랜드를 찾을 수 없습니다");
                return "error";
            }

            brand.setName(brandName);

            for (Category category : Category.values()) {
                String priceStr = formData.get(category.name());
                if (priceStr != null && !priceStr.isEmpty()) {
                    int price = Integer.parseInt(priceStr);
                    brand.getPrices().put(category, price);
                }
            }

            brandService.saveBrand(brand);

            return "redirect:/manage-brands";
        } catch (Exception e) {
            logger.error("브랜드 수정 처리 오류: ", e);
            model.addAttribute("error", "브랜드 수정 실패: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/delete-brand")
    public String deleteBrand(@RequestParam Long id, Model model) {
        try {
            Brand brand = brandService.getBrandById(id);
            if (brand == null) {
                model.addAttribute("error", "브랜드를 찾을 수 없습니다");
                return "error";
            }

            brandService.deleteBrand(id);

            return "redirect:/manage-brands";
        } catch (Exception e) {
            logger.error("브랜드 삭제 오류: ", e);
            model.addAttribute("error", "브랜드 삭제 실패: " + e.getMessage());
            return "error";
        }
    }
}