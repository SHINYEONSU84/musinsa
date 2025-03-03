package org.example.service;

import org.example.model.Brand;
import org.example.model.Category;
import org.example.repository.BrandRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 클래스 설명 : 브랜드와 관련된 비즈니스 로직을 처리하는 서비스 클래스
 * 무신사 코디 서비스의 핵심 비즈니스 로직을 구현하며, 브랜드 관리와 관련된 기능을 제공한다.
 * 데이터 접근을 위해 BrandRepository와 상호작용하고, 컨트롤러 계층에 필요한 데이터를 가공한다.
 * 주요 기능:
 * 1. 브랜드 CRUD 작업 (생성, 조회, 수정, 삭제)
 * 2. 카테고리별 최저가격 브랜드와 상품가격, 총액 조회
 * 3. 단일 브랜드 최저 총액 조회
 * 4. 카테고리별 최저/최고 가격 브랜드 조회
 * 5. 초기 브랜드 데이터 설정
 * 작성자 : sys1021
 * 작성일 : 2025.03.02
 */
@Service
public class BrandService {

    /**
     * 브랜드 데이터에 접근하기 위한 리포지토리 인스턴스
     * Spring의 의존성 주입(DI)을 통해 자동으로 주입된다.
     */
    @Autowired
    private BrandRepository brandRepository;

    /**
     * 메서드 설명 : 애플리케이션 시작 시 초기 브랜드 데이터를 설정하는 메서드
     * 데이터베이스에 브랜드가 없는 경우에만 기본 브랜드 데이터를 생성한다.
     * 브랜드 A부터 I까지 9개의 브랜드를 생성하고, 각 브랜드별로 8개 카테고리의 가격 정보를 설정한다.
     * @Transactional 어노테이션으로 트랜잭션 처리를 보장한다.
     * 작성자 : sys1021
     * 작성일 : 2025.03.02
     */
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

    /**
     * 메서드 설명 : 모든 브랜드 목록을 조회하는 메서드
     * @return 모든 브랜드 목록
     * 작성자 : sys1021
     * 작성일 : 2025.03.02
     */
    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

    /**
     * 메서드 설명 : 특정 ID에 해당하는 브랜드를 조회하는 메서드
     * @param id 조회할 브랜드의 ID
     * @return 해당 ID의 브랜드 (존재하지 않는 경우 null)
     * 작성자 : sys1021
     * 작성일 : 2025.03.02
     */
    public Brand getBrandById(Long id) {
        return brandRepository.findById(id).orElse(null);
    }

    /**
     * 메서드 설명 : 특정 이름에 해당하는 브랜드를 조회하는 메서드
     * @param name 조회할 브랜드의 이름
     * @return 해당 이름의 브랜드 (존재하지 않는 경우 null)
     * 작성자 : sys1021
     * 작성일 : 2025.03.02
     */
    public Brand getBrandByName(String name) {
        return brandRepository.findByName(name);
    }

    /**
     * 메서드 설명 : 브랜드를 저장하는 메서드 (생성 또는 수정)
     * @param brand 저장할 브랜드 객체
     * @return 저장된 브랜드 객체
     * 작성자 : sys1021
     * 작성일 : 2025.03.02
     */
    @Transactional
    public Brand saveBrand(Brand brand) {
        return brandRepository.save(brand);
    }

    /**
     * 메서드 설명 : 브랜드를 삭제하는 메서드
     * @param id 삭제할 브랜드의 ID
     * 작성자 : sys1021
     * 작성일 : 2025.03.02
     */
    @Transactional
    public void deleteBrand(Long id) {
        brandRepository.deleteById(id);
    }

    /**
     * 메서드 설명 : 특정 카테고리의 브랜드들을 가격 오름차순으로 조회하는 메서드
     * @param category 조회할 카테고리
     * @return 해당 카테고리의 브랜드들을 가격 오름차순으로 정렬한 목록
     * 작성자 : sys1021
     * 작성일 : 2025.03.02
     */
    public List<Brand> getBrandsByCategoryOrderByPriceAsc(Category category) {
        return brandRepository.findAllByCategoryOrderByPriceAsc(category);
    }

    /**
     * 메서드 설명 : 특정 카테고리의 브랜드들을 가격 내림차순으로 조회하는 메서드
     * @param category 조회할 카테고리
     * @return 해당 카테고리의 브랜드들을 가격 내림차순으로 정렬한 목록
     * 작성자 : sys1021
     * 작성일 : 2025.03.02
     */
    public List<Brand> getBrandsByCategoryOrderByPriceDesc(Category category) {
        return brandRepository.findAllByCategoryOrderByPriceDesc(category);
    }

    /**
     * 메서드 설명 : API 1 카테고리별 최저가격 브랜드와 상품가격, 총액을 조회하는 메서드
     * 각 카테고리별로 최저 가격을 제공하는 브랜드와 가격 정보를 맵 형태로 반환한다.
     * 같은 최저가격을 제공하는 브랜드가 여러 개인 경우, 모든 브랜드를 콤마로 구분하여 표시한다.
     * @return 카테고리를 키로, 브랜드와 가격 정보를 값으로 하는 맵
     * 작성자 : sys1021
     * 작성일 : 2025.03.02
     */
    public Map<Category, Map<String, Object>> getLowestPriceByCategory() {
        Map<Category, Map<String, Object>> result = new HashMap<>();

        // 각 카테고리별로 최저가격 브랜드 조회
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

                // 결과 맵에 브랜드와 가격 정보 저장
                Map<String, Object> categoryData = new HashMap<>();
                categoryData.put("brand", String.join(",", lowestPriceBrands));
                categoryData.put("price", lowestPrice);

                result.put(category, categoryData);
            }
        }

        return result;

    }

    /**
     * 메서드 설명 : API 2 단일 브랜드로 모든 카테고리 상품을 구매할 때 최저가격 브랜드를 조회하는 메서드
     * 모든 브랜드를 검사하여 각 브랜드의 총 가격을 계산하고,
     * 그 중 최저 총액을 제공하는 브랜드와 카테고리별 가격, 총액 정보를 반환한다.
     * @return 최저 총액 브랜드 정보를 포함한 맵
     * 작성자 : sys1021
     * 작성일 : 2025.03.02
     */
    public Map<String, Object> getLowestTotalPriceBrand() {

        List<Brand> brands = getAllBrands();
        Brand lowestTotalPriceBrand = null;
        int lowestTotalPrice = Integer.MAX_VALUE;

        // 모든 브랜드의 총 가격을 계산하고 최저가 브랜드 찾기
        for (Brand brand : brands) {
            int totalPrice = calculateTotalPrice(brand);
            if (totalPrice < lowestTotalPrice) {
                lowestTotalPrice = totalPrice;
                lowestTotalPriceBrand = brand;
            }
        }

        // 결과 맵 구성
        Map<String, Object> result = new HashMap<>();
        if (lowestTotalPriceBrand != null) {
            // LinkedHashMap을 사용해 필드 순서 유지
            Map<String, Object> brandInfo = new LinkedHashMap<>();
            // 요구사항 순서대로 추가: 브랜드, 카테고리, 총액
            brandInfo.put("브랜드", lowestTotalPriceBrand.getName());

            // 카테고리별 가격 정보 목록 생성
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

    /**
     * 메서드 설명 : 브랜드의 전체 카테고리 상품 가격 총액을 계산하는 내부 메서드
     * @param brand 총액을 계산할 브랜드
     * @return 해당 브랜드의 모든 카테고리 상품 가격 총액
     * 작성자 : sys1021
     * 작성일 : 2025.03.02
     */
    private int calculateTotalPrice(Brand brand) {
        // 스트림 API를 사용하여 모든 카테고리의 가격을 합산
        return brand.getPrices().values().stream().mapToInt(Integer::intValue).sum();
    }

    /**
     * 메서드 설명 : API 3 카테고리 이름으로 최저, 최고 가격 브랜드와 상품 가격을 조회하는 메서드
     * 지정된 카테고리에 대해 최저가 및 최고가 브랜드와 가격 정보를 조회한다.
     * 같은 가격을 제공하는 브랜드가 여러 개인 경우 모두 포함한다.
     * @param category 조회할 카테고리
     * @return 카테고리, 최저가, 최고가 정보를 포함한 맵
     * 작성자 : sys1021
     * 작성일 : 2025.03.02
     */
    public Map<String, Object> getMinMaxPriceByCategory(Category category) {

        // 해당 카테고리의 브랜드를 가격 오름차순과 내림차순으로 조회
        List<Brand> brandsAsc = getBrandsByCategoryOrderByPriceAsc(category);
        List<Brand> brandsDesc = getBrandsByCategoryOrderByPriceDesc(category);

        Map<String, Object> result = new HashMap<>();
        // 카테고리 이름 설정
        result.put("카테고리", category.getDisplayName());

        // 최저가 브랜드 정보 추가
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

        // 최고가 브랜드 정보 추가
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

    /**
     * 메서드 설명 : API 4 브랜드 가격을 업데이트하는 메서드
     * 지정된 브랜드명과 카테고리에 해당하는 상품의 가격을 업데이트한다.
     * @param brandName 업데이트할 브랜드의 이름
     * @param category 업데이트할 카테고리
     * @param price 새로운 가격
     * @return 업데이트된 브랜드 객체 (브랜드가 존재하지 않는 경우 null)
     * 작성자 : sys1021
     * 작성일 : 2025.03.02
     */
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