package org.example.controller;

import org.example.model.Brand;
import org.example.model.Category;
import org.example.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class WebController {
    @Autowired
    private BrandService brandService;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    // API 1: 카테고리별 최저가격 브랜드와 상품가격, 총액을 조회
    @GetMapping("/lowest-price-by-category")
    public String lowestPriceByCategory(Model model) {
        Map<Category, Map<String, Object>> lowestPriceByCategory = brandService.getLowestPriceByCategory();

        List<Map<String, Object>> categories = Arrays.stream(Category.values())
                .map(category -> {
                    Map<String, Object> categoryData = lowestPriceByCategory.get(category);
                    Map<String, Object> data = new HashMap<>();
                    data.put("category", category.getDisplayName());
                    data.put("brand", categoryData.get("brand"));
                    data.put("price", categoryData.get("price"));
                    return data;
                })
                .collect(Collectors.toList());

        int totalPrice = categories.stream()
                .mapToInt(data -> (int) data.get("price"))
                .sum();

        model.addAttribute("categories", categories);
        model.addAttribute("totalPrice", String.format("%,d", totalPrice));

        return "lowest-price-by-category";
    }

    // API 2: 단일 브랜드로 모든 카테고리 상품을 구매할 때 최저가격 브랜드 조회
    @GetMapping("/lowest-total-price-brand")
    public String lowestTotalPriceBrand(Model model) {
        try {
            Map<String, Object> lowestTotalPriceBrand = brandService.getLowestTotalPriceBrand();
            model.addAttribute("lowestTotalPriceBrand", lowestTotalPriceBrand);
            return "lowest-total-price-brand";
        } catch (Exception e) {
            model.addAttribute("error", "단일 브랜드 최저 총액 조회 실패: " + e.getMessage());
            return "error";
        }
    }

    // API 3: 카테고리 이름으로 최저, 최고 가격 브랜드와 상품 가격을 조회
    @GetMapping("/min-max-price-by-category")
    public String minMaxPriceByCategory(Model model) {
        List<String> categoryNames = Arrays.stream(Category.values())
                .map(Category::getDisplayName)
                .collect(Collectors.toList());

        model.addAttribute("categoryNames", categoryNames);
        return "min-max-price-by-category";
    }

    @GetMapping("/min-max-price-result")
    public String minMaxPriceResult(@RequestParam String categoryName, Model model) {
        try {
            Category category = Category.fromDisplayName(categoryName);
            Map<String, Object> minMaxPrice = brandService.getMinMaxPriceByCategory(category);
            model.addAttribute("minMaxPrice", minMaxPrice);
            return "min-max-price-result";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "잘못된 카테고리 이름: " + categoryName);
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
            return "manage-brands";
        } catch (Exception e) {
            model.addAttribute("error", "브랜드 관리 페이지 로드 실패: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/add-brand")
    public String addBrandForm(Model model) {
        List<String> categoryNames = Arrays.stream(Category.values())
                .map(Category::getDisplayName)
                .collect(Collectors.toList());

        model.addAttribute("categoryNames", categoryNames);
        return "add-brand";
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
            model.addAttribute("error", "브랜드 추가 실패: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/edit-brand")
    public String editBrandForm(@RequestParam Long id, Model model) {
        Brand brand = brandService.getBrandById(id);
        if (brand == null) {
            model.addAttribute("error", "브랜드를 찾을 수 없습니다");
            return "error";
        }

        List<String> categoryNames = Arrays.stream(Category.values())
                .map(Category::getDisplayName)
                .collect(Collectors.toList());

        model.addAttribute("brand", brand);
        model.addAttribute("categoryNames", categoryNames);
        return "edit-brand";
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
            model.addAttribute("error", "브랜드 삭제 실패: " + e.getMessage());
            return "error";
        }
    }
}