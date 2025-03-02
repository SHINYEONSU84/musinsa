package org.example.service;

import org.example.model.Brand;
import org.example.model.Category;
import org.example.repository.BrandRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BrandService {
    @Autowired
    private BrandRepository brandRepository;

    @Transactional
    public void initializeBrands() {
        // 초기 브랜드 데이터가 없을 경우에만 초기화
        if (brandRepository.count() == 0) {
            // 브랜드 A
            Brand brandA = new Brand();
            brandA.setName("A");
            brandA.getPrices().put(Category.TOP, 11200);
            brandA.getPrices().put(Category.OUTER, 5500);
            brandA.getPrices().put(Category.PANTS, 4200);
            brandA.getPrices().put(Category.SNEAKERS, 9000);
            brandA.getPrices().put(Category.BAG, 2000);
            brandA.getPrices().put(Category.HAT, 1700);
            brandA.getPrices().put(Category.SOCKS, 1800);
            brandA.getPrices().put(Category.ACCESSORY, 2300);
            brandRepository.save(brandA);

            // 브랜드 B
            Brand brandB = new Brand();
            brandB.setName("B");
            brandB.getPrices().put(Category.TOP, 10500);
            brandB.getPrices().put(Category.OUTER, 5900);
            brandB.getPrices().put(Category.PANTS, 3800);
            brandB.getPrices().put(Category.SNEAKERS, 9100);
            brandB.getPrices().put(Category.BAG, 2100);
            brandB.getPrices().put(Category.HAT, 2000);
            brandB.getPrices().put(Category.SOCKS, 2000);
            brandB.getPrices().put(Category.ACCESSORY, 2200);
            brandRepository.save(brandB);

            // 브랜드 C
            Brand brandC = new Brand();
            brandC.setName("C");
            brandC.getPrices().put(Category.TOP, 10000);
            brandC.getPrices().put(Category.OUTER, 6200);
            brandC.getPrices().put(Category.PANTS, 3300);
            brandC.getPrices().put(Category.SNEAKERS, 9200);
            brandC.getPrices().put(Category.BAG, 2200);
            brandC.getPrices().put(Category.HAT, 1900);
            brandC.getPrices().put(Category.SOCKS, 2200);
            brandC.getPrices().put(Category.ACCESSORY, 2100);
            brandRepository.save(brandC);

            // 브랜드 D
            Brand brandD = new Brand();
            brandD.setName("D");
            brandD.getPrices().put(Category.TOP, 10100);
            brandD.getPrices().put(Category.OUTER, 5100);
            brandD.getPrices().put(Category.PANTS, 3000);
            brandD.getPrices().put(Category.SNEAKERS, 9500);
            brandD.getPrices().put(Category.BAG, 2500);
            brandD.getPrices().put(Category.HAT, 1500);
            brandD.getPrices().put(Category.SOCKS, 2400);
            brandD.getPrices().put(Category.ACCESSORY, 2000);
            brandRepository.save(brandD);

            // 브랜드 E
            Brand brandE = new Brand();
            brandE.setName("E");
            brandE.getPrices().put(Category.TOP, 10700);
            brandE.getPrices().put(Category.OUTER, 5000);
            brandE.getPrices().put(Category.PANTS, 3800);
            brandE.getPrices().put(Category.SNEAKERS, 9900);
            brandE.getPrices().put(Category.BAG, 2300);
            brandE.getPrices().put(Category.HAT, 1800);
            brandE.getPrices().put(Category.SOCKS, 2100);
            brandE.getPrices().put(Category.ACCESSORY, 2100);
            brandRepository.save(brandE);

            // 브랜드 F
            Brand brandF = new Brand();
            brandF.setName("F");
            brandF.getPrices().put(Category.TOP, 11200);
            brandF.getPrices().put(Category.OUTER, 7200);
            brandF.getPrices().put(Category.PANTS, 4000);
            brandF.getPrices().put(Category.SNEAKERS, 9300);
            brandF.getPrices().put(Category.BAG, 2100);
            brandF.getPrices().put(Category.HAT, 1600);
            brandF.getPrices().put(Category.SOCKS, 2300);
            brandF.getPrices().put(Category.ACCESSORY, 1900);
            brandRepository.save(brandF);

            // 브랜드 G
            Brand brandG = new Brand();
            brandG.setName("G");
            brandG.getPrices().put(Category.TOP, 10500);
            brandG.getPrices().put(Category.OUTER, 5800);
            brandG.getPrices().put(Category.PANTS, 3900);
            brandG.getPrices().put(Category.SNEAKERS, 9000);
            brandG.getPrices().put(Category.BAG, 2200);
            brandG.getPrices().put(Category.HAT, 1700);
            brandG.getPrices().put(Category.SOCKS, 2100);
            brandG.getPrices().put(Category.ACCESSORY, 2000);
            brandRepository.save(brandG);

            // 브랜드 H
            Brand brandH = new Brand();
            brandH.setName("H");
            brandH.getPrices().put(Category.TOP, 10800);
            brandH.getPrices().put(Category.OUTER, 6300);
            brandH.getPrices().put(Category.PANTS, 3100);
            brandH.getPrices().put(Category.SNEAKERS, 9700);
            brandH.getPrices().put(Category.BAG, 2100);
            brandH.getPrices().put(Category.HAT, 1600);
            brandH.getPrices().put(Category.SOCKS, 2000);
            brandH.getPrices().put(Category.ACCESSORY, 2000);
            brandRepository.save(brandH);

            // 브랜드 I
            Brand brandI = new Brand();
            brandI.setName("I");
            brandI.getPrices().put(Category.TOP, 11400);
            brandI.getPrices().put(Category.OUTER, 6700);
            brandI.getPrices().put(Category.PANTS, 3200);
            brandI.getPrices().put(Category.SNEAKERS, 9500);
            brandI.getPrices().put(Category.BAG, 2400);
            brandI.getPrices().put(Category.HAT, 1700);
            brandI.getPrices().put(Category.SOCKS, 1700);
            brandI.getPrices().put(Category.ACCESSORY, 2400);
            brandRepository.save(brandI);
        }
    }

    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

    public Brand getBrandById(Long id) {
        return brandRepository.findById(id).orElse(null);
    }

    public Brand getBrandByName(String name) {
        return brandRepository.findByName(name);
    }

    @Transactional
    public Brand saveBrand(Brand brand) {
        return brandRepository.save(brand);
    }

    @Transactional
    public void deleteBrand(Long id) {
        brandRepository.deleteById(id);
    }

    public List<Brand> getBrandsByCategoryOrderByPriceAsc(Category category) {
        return brandRepository.findAllByCategoryOrderByPriceAsc(category);
    }

    public List<Brand> getBrandsByCategoryOrderByPriceDesc(Category category) {
        return brandRepository.findAllByCategoryOrderByPriceDesc(category);
    }

    // API 1: 카테고리별 최저가격 브랜드와 상품가격, 총액을 조회
    public Map<Category, Map<String, Object>> getLowestPriceByCategory() {
        Map<Category, Map<String, Object>> result = new HashMap<>();

        for (Category category : Category.values()) {
            List<Brand> brands = getBrandsByCategoryOrderByPriceAsc(category);
            if (!brands.isEmpty()) {
                int lowestPrice = brands.get(0).getPrices().get(category);

                // 같은 최저가 가진 모든 브랜드 수집
                List<String> lowestPriceBrands = new ArrayList<>();
                for (Brand brand : brands) {
                    int price = brand.getPrices().get(category);
                    if (price == lowestPrice) {
                        lowestPriceBrands.add(brand.getName());
                    } else {
                        // 이미 가격순 정렬이므로 첫 번째 다른 가격이 나오면 중단
                        break;
                    }
                }

                Map<String, Object> categoryData = new HashMap<>();
                categoryData.put("brand", String.join(",", lowestPriceBrands));
                categoryData.put("price", lowestPrice);

                result.put(category, categoryData);
            }
        }
        return result;
    }

    // API 2: 단일 브랜드로 모든 카테고리 상품을 구매할 때 최저가격 브랜드 조회
    public Map<String, Object> getLowestTotalPriceBrand() {
        List<Brand> brands = getAllBrands();
        Brand lowestTotalPriceBrand = null;
        int lowestTotalPrice = Integer.MAX_VALUE;

        for (Brand brand : brands) {
            int totalPrice = calculateTotalPrice(brand);
            if (totalPrice < lowestTotalPrice) {
                lowestTotalPrice = totalPrice;
                lowestTotalPriceBrand = brand;
            }
        }

        Map<String, Object> result = new HashMap<>();
        if (lowestTotalPriceBrand != null) {
            // LinkedHashMap을 사용해 필드 순서 유지
            Map<String, Object> brandInfo = new LinkedHashMap<>();
            // 요구사항 순서대로 추가: 브랜드, 카테고리, 총액
            brandInfo.put("브랜드", lowestTotalPriceBrand.getName());

            List<Map<String, String>> categoryPrices = new ArrayList<>();
            for (Category category : Category.values()) {
                Map<String, String> categoryPrice = new LinkedHashMap<>();
                categoryPrice.put("카테고리", category.getDisplayName());
                // 가격에 콤마 포맷 적용
                categoryPrice.put("가격", String.format("%,d", lowestTotalPriceBrand.getPrices().get(category)));
                categoryPrices.add(categoryPrice);
            }

            brandInfo.put("카테고리", categoryPrices);
            // 총액에 콤마 포맷 적용
            brandInfo.put("총액", String.format("%,d", lowestTotalPrice));

            result.put("최저가", brandInfo);
        }

        return result;
    }

    private int calculateTotalPrice(Brand brand) {
        return brand.getPrices().values().stream().mapToInt(Integer::intValue).sum();
    }

    // API 3: 카테고리 이름으로 최저, 최고 가격 브랜드와 상품 가격을 조회
    public Map<String, Object> getMinMaxPriceByCategory(Category category) {
        List<Brand> brandsAsc = getBrandsByCategoryOrderByPriceAsc(category);
        List<Brand> brandsDesc = getBrandsByCategoryOrderByPriceDesc(category);

        Map<String, Object> result = new HashMap<>();
        result.put("카테고리", category.getDisplayName());

        if (!brandsAsc.isEmpty()) {
            Brand minPriceBrand = brandsAsc.get(0);
            int minPrice = minPriceBrand.getPrices().get(category);

            // 같은 최저가 가진 모든 브랜드 수집
            List<Map<String, String>> minPriceList = new ArrayList<>();

            for (Brand brand : brandsAsc) {
                int price = brand.getPrices().get(category);
                if (price == minPrice) {
                    Map<String, String> minPriceInfo = new HashMap<>();
                    minPriceInfo.put("브랜드", brand.getName());
                    minPriceInfo.put("가격", String.format("%,d", price));
                    minPriceList.add(minPriceInfo);
                } else {
                    // 이미 가격순 정렬이므로 첫 번째 다른 가격이 나오면 중단
                    break;
                }
            }

            result.put("최저가", minPriceList);
        }

        if (!brandsDesc.isEmpty()) {
            Brand maxPriceBrand = brandsDesc.get(0);
            int maxPrice = maxPriceBrand.getPrices().get(category);

            // 같은 최고가 가진 모든 브랜드 수집
            List<Map<String, String>> maxPriceList = new ArrayList<>();

            for (Brand brand : brandsDesc) {
                int price = brand.getPrices().get(category);
                if (price == maxPrice) {
                    Map<String, String> maxPriceInfo = new HashMap<>();
                    maxPriceInfo.put("브랜드", brand.getName());
                    maxPriceInfo.put("가격", String.format("%,d", price));
                    maxPriceList.add(maxPriceInfo);
                } else {
                    // 이미 가격순 정렬이므로 첫 번째 다른 가격이 나오면 중단
                    break;
                }
            }

            result.put("최고가", maxPriceList);
        }

        return result;
    }

    // API 4: 브랜드 가격 업데이트
    @Transactional
    public Brand updateBrandPrice(String brandName, Category category, int price) {
        Brand brand = getBrandByName(brandName);
        if (brand != null) {
            brand.getPrices().put(category, price);
            return brandRepository.save(brand);
        }
        return null;
    }
}