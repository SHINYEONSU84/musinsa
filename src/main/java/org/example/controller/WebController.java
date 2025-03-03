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

/**
 * 클래스 설명 : 웹 인터페이스를 제공하는 컨트롤러 클래스
 * 무신사 코디 서비스의 웹 페이지 요청을 처리하며, BrandService를 통해 비즈니스 로직을 수행한다.
 * Thymeleaf 템플릿 엔진과 함께 작동하여 동적 웹 페이지를 생성한다.
 * 다음 4가지 주요 API 기능에 대한 웹 인터페이스를 제공한다:
 * 1. 카테고리별 최저가격 브랜드와 상품가격, 총액 조회
 * 2. 단일 브랜드 최저 총액 조회
 * 3. 카테고리별 최저/최고 가격 조회
 * 4. 브랜드 관리(추가, 수정, 삭제)
 * 작성자 : sys1021
 * 작성일 : 2025.03.02
 */
@Controller
public class WebController {

    /**
     * 로깅을 위한 Logger 인스턴스
     * 컨트롤러 내의 다양한 메서드에서 로그를 기록하는 데 사용된다.
     */
    private static final Logger logger = LoggerFactory.getLogger(WebController.class);

    /**
     * 브랜드 관련 비즈니스 로직을 수행하는 서비스 클래스
     * Spring의 의존성 주입(DI)을 통해 자동으로 주입된다.
     */
    @Autowired
    private BrandService brandService;

    /**
     * JSON 변환을 위한 ObjectMapper 인스턴스
     * 객체를 JSON으로 또는 JSON을 객체로 변환하는 데 사용된다.
     */
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 메서드 설명 : 메인 페이지를 제공하는 메서드
     * @return 메인 페이지 템플릿 이름 "index"
     * 작성자 : sys1021
     * 작성일 : 2025.03.02
     */
    @GetMapping("/")
    public String home() {
        return "index";
    }

    /**
     * 메서드 설명 : API 1 카테고리별 최저가격 브랜드와 상품가격, 총액을 조회하는 페이지를 제공하는 메서드
     * BrandService를 통해 각 카테고리별 최저가격 브랜드와 가격 정보를 조회하고,
     * 이를 화면에 표시하기 위한 데이터를 Model에 추가한다.
     * @param model 뷰에 데이터를 전달하기 위한 Spring의 Model 객체
     * @return 카테고리별 최저가격 조회 결과 페이지 템플릿 이름 "lowest-price-by-category" 또는 오류 발생 시 "error"
     * 작성자 : sys1021
     * 작성일 : 2025.03.02
     */
    @GetMapping("/lowest-price-by-category")
    public String lowestPriceByCategory(Model model) {

        try {
            // 서비스 계층에서 카테고리별 최저가격 정보 조회
            Map<Category, Map<String, Object>> lowestPriceByCategory = brandService.getLowestPriceByCategory();

            // 응답 형식 준비
            List<Map<String, Object>> categoryList = new ArrayList<>();
            int totalPrice = 0;

            // 각 카테고리에 대한 데이터 처리
            for (Category category : Category.values()) {
                Map<String, Object> categoryData = lowestPriceByCategory.get(category);
                if (categoryData != null) {
                    // 카테고리명, 브랜드명, 가격 정보를 포함한 맵 생성
                    Map<String, Object> data = new HashMap<>();
                    data.put("category", category.getDisplayName());
                    data.put("brand", categoryData.get("brand"));
                    int price = (int) categoryData.get("price");
                    data.put("price", String.format("%,d", price)); // 천 단위 구분자(콤마) 적용
                    categoryList.add(data);

                    // 총액 계산
                    totalPrice += (int) categoryData.get("price");
                }
            }

            // 모델에 데이터 추가
            model.addAttribute("categories", categoryList);
            model.addAttribute("totalPrice", String.format("%,d", totalPrice)); // 천 단위 구분자(콤마) 적용

            return "lowest-price-by-category";
        } catch (Exception e) {
            // 오류 로깅 및 오류 페이지로 리다이렉트
            logger.error("API 1 오류: ", e);
            model.addAttribute("error", "카테고리별 최저가격 조회 실패: " + e.getMessage());

            return "error";
        }

    }

    /**
     * 메서드 설명 : API 2 단일 브랜드로 모든 카테고리 상품을 구매할 때 최저가격 브랜드 조회 페이지를 제공하는 메서드
     * BrandService를 통해 모든 카테고리 상품을 단일 브랜드에서 구매할 때
     * 최저 총액을 제공하는 브랜드와 카테고리별 가격 정보를 조회한다.
     * @param model 뷰에 데이터를 전달하기 위한 Spring의 Model 객체
     * @return 단일 브랜드 최저 총액 조회 결과 페이지 템플릿 이름 "lowest-total-price-brand" 또는 오류 발생 시 "error"
     * 작성자 : sys1021
     * 작성일 : 2025.03.02
     */
    @GetMapping("/lowest-total-price-brand")
    public String lowestTotalPriceBrand(Model model) {

        try {
            // 서비스 계층에서 단일 브랜드 최저 총액 정보 조회
            Map<String, Object> result = brandService.getLowestTotalPriceBrand();

            // 디버그용 로깅
            logger.info("API 2 결과: {}", objectMapper.writeValueAsString(result));

            // 모델에 데이터 추가
            // 중첩된 맵 구조에서 필요한 데이터 추출
            model.addAttribute("brandName", ((Map<String, Object>)result.get("최저가")).get("브랜드"));
            model.addAttribute("categories", ((Map<String, Object>)result.get("최저가")).get("카테고리"));
            model.addAttribute("totalPrice", ((Map<String, Object>)result.get("최저가")).get("총액"));

            return "lowest-total-price-brand";
        } catch (Exception e) {
            // 오류 로깅 및 오류 페이지로 리다이렉트
            logger.error("API 2 오류: ", e);
            model.addAttribute("error", "단일 브랜드 최저 총액 조회 실패: " + e.getMessage());

            return "error";
        }

    }

    /**
     * 메서드 설명 : API 3 카테고리 이름으로 최저, 최고 가격 브랜드와 상품 가격을 조회하는 폼 페이지를 제공하는 메서드
     * 사용자가 카테고리를 선택할 수 있는 폼을 제공한다.
     * @param model 뷰에 데이터를 전달하기 위한 Spring의 Model 객체
     * @return 카테고리 선택 폼 페이지 템플릿 이름 "min-max-price-by-category" 또는 오류 발생 시 "error"
     * 작성자 : sys1021
     * 작성일 : 2025.03.02
     */
    @GetMapping("/min-max-price-by-category")
    public String minMaxPriceByCategory(Model model) {

        try {
            // 모든 카테고리 이름 목록 생성
            List<String> categoryNames = Arrays.stream(Category.values())
                    .map(Category::getDisplayName)
                    .collect(Collectors.toList());

            // 모델에 카테고리 이름 목록 추가
            model.addAttribute("categoryNames", categoryNames);

            return "min-max-price-by-category";
        } catch (Exception e) {
            // 오류 로깅 및 오류 페이지로 리다이렉트
            logger.error("API 3 폼 오류: ", e);
            model.addAttribute("error", "카테고리 조회 실패: " + e.getMessage());

            return "error";
        }

    }

    /**
     * 메서드 설명 : API 3 선택한 카테고리의 최저, 최고 가격 브랜드와 상품 가격 결과를 제공하는 메서드
     * 사용자가 선택한 카테고리에 대해 최저가와 최고가를 제공하는 브랜드 및 가격 정보를 조회한다.
     * @param categoryName 조회할 카테고리 이름
     * @param model 뷰에 데이터를 전달하기 위한 Spring의 Model 객체
     * @return 카테고리별 최저/최고 가격 결과 페이지 템플릿 이름 "min-max-price-result" 또는 오류 발생 시 "error"
     * 작성자 : sys1021
     * 작성일 : 2025.03.02
     */
    @GetMapping("/min-max-price-result")
    public String minMaxPriceResult(@RequestParam String categoryName, Model model) {

        try {
            // 카테고리 이름으로 카테고리 열거형 객체 조회
            Category category = Category.fromDisplayName(categoryName);
            // 서비스 계층에서 해당 카테고리의 최저/최고 가격 정보 조회
            Map<String, Object> result = brandService.getMinMaxPriceByCategory(category);

            // 디버그용 로깅
            logger.info("API 3 결과: {}", objectMapper.writeValueAsString(result));

            // 모델에 데이터 추가
            model.addAttribute("categoryName", result.get("카테고리"));
            model.addAttribute("minPrices", result.get("최저가"));
            model.addAttribute("maxPrices", result.get("최고가"));

            return "min-max-price-result";
        } catch (IllegalArgumentException e) {
            // 잘못된 카테고리 이름 예외 처리
            logger.error("API 3 카테고리 오류: ", e);
            model.addAttribute("error", "잘못된 카테고리 이름: " + categoryName);

            return "error";
        } catch (Exception e) {
            // 기타 예외 처리
            logger.error("API 3 결과 오류: ", e);
            model.addAttribute("error", "카테고리별 최저/최고 가격 조회 실패: " + e.getMessage());

            return "error";
        }

    }

    /**
     * 메서드 설명 : API 4: 브랜드 관리 페이지를 제공하는 메서드
     * 모든 브랜드 목록과 카테고리 정보를 조회하여 브랜드 관리 페이지에 표시한다.
     * @param model 뷰에 데이터를 전달하기 위한 Spring의 Model 객체
     * @return 브랜드 관리 페이지 템플릿 이름 "manage-brands" 또는 오류 발생 시 "error"
     * 작성자 : sys1021
     * 작성일 : 2025.03.02
     */
    @GetMapping("/manage-brands")
    public String manageBrands(Model model) {

        try {
            // 모든 브랜드 목록 조회
            List<Brand> brands = brandService.getAllBrands();
            // 카테고리 이름 목록 생성
            List<String> categoryNames = Arrays.stream(Category.values())
                    .map(Category::getDisplayName)
                    .collect(Collectors.toList());

            // 모델에 데이터 추가
            model.addAttribute("brands", brands);
            model.addAttribute("categoryNames", categoryNames);
            model.addAttribute("categories", Category.values());

            return "manage-brands";
        } catch (Exception e) {
            // 오류 로깅 및 오류 페이지로 리다이렉트
            logger.error("API 4 브랜드 관리 오류: ", e);
            model.addAttribute("error", "브랜드 관리 페이지 로드 실패: " + e.getMessage());

            return "error";
        }

    }

    /**
     * 메서드 설명 : 새 브랜드 추가 폼 페이지를 제공하는 메서드
     * 새 브랜드 생성을 위한 입력 폼을 제공한다.
     * @param model 뷰에 데이터를 전달하기 위한 Spring의 Model 객체
     * @return 브랜드 추가 폼 페이지 템플릿 이름 "add-brand" 또는 오류 발생 시 "error"
     * 작성자 : sys1021
     * 작성일 : 2025.03.02
     */
    @GetMapping("/add-brand")
    public String addBrandForm(Model model) {

        try {
            // 모델에 카테고리 목록 추가
            model.addAttribute("categories", Category.values());

            return "add-brand";
        } catch (Exception e) {
            // 오류 로깅 및 오류 페이지로 리다이렉트
            logger.error("브랜드 추가 폼 오류: ", e);
            model.addAttribute("error", "브랜드 추가 페이지 로드 실패: " + e.getMessage());

            return "error";
        }

    }

    /**
     * 메서드 설명 : 새 브랜드 추가 요청을 처리하는 메서드
     * 폼에서 제출된 데이터를 기반으로 새 브랜드를 생성하고 저장한다.
     * @param brandName 새 브랜드 이름
     * @param formData 폼에서 제출된 카테고리별 가격 정보를 포함한 맵
     * @param model 뷰에 데이터를 전달하기 위한 Spring의 Model 객체
     * @return 처리 성공 시 브랜드 관리 페이지로 리다이렉트, 오류 발생 시 "error" 페이지
     * 작성자 : sys1021
     * 작성일 : 2025.03.02
     */
    @PostMapping("/add-brand")
    public String addBrand(@RequestParam String brandName, @RequestParam Map<String, String> formData, Model model) {

        try {
            // 새 브랜드 객체 생성
            Brand brand = new Brand();
            brand.setName(brandName);

            // 폼 데이터에서 카테고리별 가격 정보 추출 및 설정
            for (Category category : Category.values()) {
                String priceStr = formData.get(category.name());
                if (priceStr != null && !priceStr.isEmpty()) {
                    int price = Integer.parseInt(priceStr);
                    brand.getPrices().put(category, price);
                }
            }

            // 브랜드 저장
            brandService.saveBrand(brand);

            // 브랜드 관리 페이지로 리다이렉트
            return "redirect:/manage-brands";
        } catch (Exception e) {
            // 오류 로깅 및 오류 페이지로 리다이렉트
            logger.error("브랜드 추가 처리 오류: ", e);
            model.addAttribute("error", "브랜드 추가 실패: " + e.getMessage());

            return "error";
        }

    }

    /**
     * 메서드 설명 : 브랜드 수정 폼 페이지를 제공하는 메서드
     * 지정된 ID의 브랜드 정보를 조회하여 수정 폼에 표시한다.
     * @param id 수정할 브랜드의 ID
     * @param model 뷰에 데이터를 전달하기 위한 Spring의 Model 객체
     * @return 브랜드 수정 폼 페이지 템플릿 이름 "edit-brand" 또는 오류 발생 시 "error"
     * 작성자 : sys1021
     * 작성일 : 2025.03.02
     */
    @GetMapping("/edit-brand")
    public String editBrandForm(@RequestParam Long id, Model model) {

        try {
            // ID로 브랜드 조회
            Brand brand = brandService.getBrandById(id);
            if (brand == null) {
                // 브랜드가 없는 경우 오류 처리
                model.addAttribute("error", "브랜드를 찾을 수 없습니다");

                return "error";
            }

            // 모델에 브랜드 정보와 카테고리 목록 추가
            model.addAttribute("brand", brand);
            model.addAttribute("categories", Category.values());

            return "edit-brand";
        } catch (Exception e) {
            // 오류 로깅 및 오류 페이지로 리다이렉트
            logger.error("브랜드 수정 폼 오류: ", e);
            model.addAttribute("error", "브랜드 수정 페이지 로드 실패: " + e.getMessage());

            return "error";
        }

    }

    /**
     * 메서드 설명 : 브랜드 수정 요청을 처리하는 메서드
     * 폼에서 제출된 데이터를 기반으로 기존 브랜드 정보를 수정한다.
     * @param id 수정할 브랜드의 ID
     * @param brandName 수정할 브랜드 이름
     * @param formData 폼에서 제출된 카테고리별 가격 정보를 포함한 맵
     * @param model 뷰에 데이터를 전달하기 위한 Spring의 Model 객체
     * @return 처리 성공 시 브랜드 관리 페이지로 리다이렉트, 오류 발생 시 "error" 페이지
     * 작성자 : sys1021
     * 작성일 : 2025.03.02
     */
    @PostMapping("/edit-brand")
    public String editBrand(@RequestParam Long id, @RequestParam String brandName, @RequestParam Map<String, String> formData, Model model) {

        try {
            // ID로 브랜드 조회
            Brand brand = brandService.getBrandById(id);
            if (brand == null) {
                // 브랜드가 없는 경우 오류 처리
                model.addAttribute("error", "브랜드를 찾을 수 없습니다");
                return "error";
            }

            // 브랜드 이름 업데이트
            brand.setName(brandName);

            // 폼 데이터에서 카테고리별 가격 정보 추출 및 설정
            for (Category category : Category.values()) {
                String priceStr = formData.get(category.name());
                if (priceStr != null && !priceStr.isEmpty()) {
                    int price = Integer.parseInt(priceStr);
                    brand.getPrices().put(category, price);
                }
            }

            // 수정된 브랜드 저장
            brandService.saveBrand(brand);

            // 브랜드 관리 페이지로 리다이렉트
            return "redirect:/manage-brands";
        } catch (Exception e) {
            // 오류 로깅 및 오류 페이지로 리다이렉트
            logger.error("브랜드 수정 처리 오류: ", e);
            model.addAttribute("error", "브랜드 수정 실패: " + e.getMessage());

            return "error";
        }

    }

    /**
     * 메서드 설명 : 브랜드 삭제 요청을 처리하는 메서드
     * 지정된 ID의 브랜드를 삭제한다.
     * @param id 삭제할 브랜드의 ID
     * @param model 뷰에 데이터를 전달하기 위한 Spring의 Model 객체
     * @return 처리 성공 시 브랜드 관리 페이지로 리다이렉트, 오류 발생 시 "error" 페이지
     * 작성자 : sys1021
     * 작성일 : 2025.03.02
     */
    @GetMapping("/delete-brand")
    public String deleteBrand(@RequestParam Long id, Model model) {

        try {
            // ID로 브랜드 조회
            Brand brand = brandService.getBrandById(id);
            if (brand == null) {
                // 브랜드가 없는 경우 오류 처리
                model.addAttribute("error", "브랜드를 찾을 수 없습니다");

                return "error";
            }

            // 브랜드 삭제
            brandService.deleteBrand(id);

            // 브랜드 관리 페이지로 리다이렉트
            return "redirect:/manage-brands";
        } catch (Exception e) {
            // 오류 로깅 및 오류 페이지로 리다이렉트
            logger.error("브랜드 삭제 오류: ", e);
            model.addAttribute("error", "브랜드 삭제 실패: " + e.getMessage());

            return "error";
        }

    }

}