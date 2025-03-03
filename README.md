# 무신사 코디 서비스

무신사 코디 서비스는 8개의 카테고리(상의, 아우터, 바지, 스니커즈, 가방, 모자, 양말, 액세서리)에서 상품을 하나씩 구매하여 코디를 완성하는 서비스입니다. 이 프로젝트는 고객이 최저가로 상품을 구매할 수 있도록 다양한 조회 기능과 관리 기능을 제공합니다.

## 구현 범위

### 1. REST API 구현
- **API 1**: 카테고리별 최저가격 브랜드와 상품가격, 총액을 조회 (`GET /api/lowest-price-by-category`)
  - 각 카테고리별로 최저가격을 제공하는 브랜드와 가격 정보 조회
  - 동일한 최저가격을 제공하는 브랜드가 여러 개인 경우 모두 표시
  - 모든 카테고리의 최저가 합산 총액 계산

- **API 2**: 단일 브랜드로 모든 카테고리 상품 구매 시 최저가격 브랜드 조회 (`GET /api/lowest-total-price-brand`)
  - 모든 카테고리 상품을 단일 브랜드에서 구매할 때 총액이 가장 저렴한 브랜드 조회
  - 해당 브랜드의 카테고리별 가격과 총액 제공

- **API 3**: 카테고리별 최저/최고 가격 브랜드 조회 (`GET /api/min-max-price-by-category`)
  - 특정 카테고리에서 최저가와 최고가를 제공하는 브랜드 및 가격 정보 조회
  - 동일한 가격을 제공하는 브랜드가 여러 개인 경우 모두 표시

- **API 4**: 브랜드 및 상품 관리 API
  - 브랜드 및 상품 생성 (`POST /api/brand`)
  - 브랜드 및 상품 수정 (`PUT /api/brand/{id}`)
  - 브랜드 및 상품 삭제 (`DELETE /api/brand/{id}`)
  - 특정 브랜드의 특정 카테고리 가격 업데이트 (`PUT /api/brand/price`)

### 2. 웹 인터페이스 구현
- 모든 API 기능을 웹 UI로 접근할 수 있는 인터페이스 구현
- Thymeleaf 템플릿 엔진을 활용한 동적 웹 페이지 구현
- Bootstrap을 활용한 반응형 디자인 적용

### 3. 데이터 관리
- H2 인메모리 데이터베이스를 사용하여 애플리케이션 재시작 시에도 데이터 유지
- JPA를 활용한 객체 관계 매핑 구현
- 초기 브랜드 데이터 자동 설정 기능 구현 (A~I 브랜드)

### 4. 테스트 구현
- 단위 테스트: 서비스 계층과 컨트롤러 계층의 개별 기능 테스트
- 통합 테스트: 전체 애플리케이션 흐름 테스트
- 예외 처리 및 에러 응답 테스트

## 코드 빌드, 테스트, 실행 방법

### 필수 요구사항
- JDK 17 이상
- Gradle 7.0 이상

### 빌드 방법
```bash
# 프로젝트 루트 디렉토리에서 실행
./gradlew build
```

### 테스트 실행 방법
```bash
# 모든 테스트 실행
./gradlew test

# 특정 테스트 클래스만 실행 (예시)
./gradlew test --tests BrandServiceTest
./gradlew test --tests ApiControllerTest
./gradlew test --tests WebControllerTest
./gradlew test --tests BrandShoppingIntegrationTest
```

### 애플리케이션 실행 방법
```bash
# 프로젝트 루트 디렉토리에서 실행
./gradlew bootRun

# 또는 빌드 후 JAR 파일 실행
java -jar build/libs/brand-shopping-1.0.0.jar
```

### 웹 애플리케이션 접속
- 기본 접속 URL: http://localhost:8080/
- H2 콘솔 접속: http://localhost:8080/h2-console
  - JDBC URL: jdbc:h2:mem:testdb
  - 사용자명: sa
  - 비밀번호: (없음)

## API 엔드포인트

### 1. 카테고리별 최저가격 조회
- URL: `/api/lowest-price-by-category`
- Method: GET
- 응답 예시:
```json
{
  "categories": [
    {"category": "상의", "brand": "C", "price": "10,000"},
    {"category": "아우터", "brand": "E", "price": "5,000"},
    {"category": "바지", "brand": "D", "price": "3,000"},
    {"category": "스니커즈", "brand": "A,G", "price": "9,000"},
    {"category": "가방", "brand": "A", "price": "2,000"},
    {"category": "모자", "brand": "D", "price": "1,500"},
    {"category": "양말", "brand": "I", "price": "1,700"},
    {"category": "액세서리", "brand": "F", "price": "1,900"}
  ],
  "totalPrice": "34,100"
}
```

### 2. 단일 브랜드 최저 총액 조회
- URL: `/api/lowest-total-price-brand`
- Method: GET
- 응답 예시:
```json
{
  "최저가": {
    "브랜드": "D",
    "카테고리": [
      {"카테고리": "상의", "가격": "10,100"},
      {"카테고리": "아우터", "가격": "5,100"},
      {"카테고리": "바지", "가격": "3,000"},
      {"카테고리": "스니커즈", "가격": "9,500"},
      {"카테고리": "가방", "가격": "2,500"},
      {"카테고리": "모자", "가격": "1,500"},
      {"카테고리": "양말", "가격": "2,400"},
      {"카테고리": "액세서리", "가격": "2,000"}
    ],
    "총액": "36,100"
  }
}
```

### 3. 카테고리별 최저/최고 가격 조회
- URL: `/api/min-max-price-by-category?categoryName=상의`
- Method: GET
- 응답 예시:
```json
{
  "카테고리": "상의",
  "최저가": [{"브랜드": "C", "가격": "10,000"}],
  "최고가": [{"브랜드": "I", "가격": "11,400"}]
}
```

### 4. 브랜드 관리 API
- 브랜드 생성: `POST /api/brand`
- 브랜드 수정: `PUT /api/brand/{id}`
- 브랜드 삭제: `DELETE /api/brand/{id}`
- 브랜드 가격 업데이트: `PUT /api/brand/price`

## 기타 추가 정보

### 프로젝트 구조
```
src/
├── main/
│   ├── java/
│   │   └── org/
│   │       └── example/
│   │           ├── BrandShoppingApplication.java
│   │           ├── controller/
│   │           │   ├── ApiController.java
│   │           │   └── WebController.java
│   │           ├── dto/
│   │           │   ├── BrandDto.java
│   │           │   ├── BrandPriceUpdateDto.java
│   │           │   ├── CategoryPriceDto.java
│   │           │   ├── ErrorResponse.java
│   │           │   ├── LowestPriceResponseDto.java
│   │           │   ├── LowestTotalPriceResponseDto.java
│   │           │   └── MinMaxPriceResponseDto.java
│   │           ├── exception/
│   │           │   └── GlobalExceptionHandler.java
│   │           ├── model/
│   │           │   ├── Brand.java
│   │           │   └── Category.java
│   │           ├── repository/
│   │           │   └── BrandRepository.java
│   │           └── service/
│   │               └── BrandService.java
│   └── resources/
│       ├── application.properties
│       └── templates/
│           ├── add-brand.html
│           ├── edit-brand.html
│           ├── error.html
│           ├── index.html
│           ├── lowest-price-by-category.html
│           ├── lowest-total-price-brand.html
│           ├── manage-brands.html
│           ├── min-max-price-by-category.html
│           └── min-max-price-result.html
└── test/
    └── java/
        └── org/
            └── example/
                ├── BrandShoppingIntegrationTest.java
                ├── controller/
                │   ├── ApiControllerTest.java
                │   └── WebControllerTest.java
                └── service/
                    └── BrandServiceTest.java
```

### 기술 스택
- **Backend**: Java 17, Spring Boot 3.1.0
- **Frontend**: Thymeleaf, Bootstrap 5
- **Database**: H2 (인메모리 데이터베이스)
- **ORM**: Spring Data JPA
- **Build Tool**: Gradle
- **Testing**: JUnit 5, Mockito

### 초기 데이터 설정
애플리케이션 시작 시 `BrandService.initializeBrands()` 메서드를 통해 9개의 브랜드(A부터 I까지)와 각 브랜드의 카테고리별 가격 정보가 자동으로 설정됩니다. 데이터베이스가 비어있을 경우에만 초기 데이터가 로드됩니다.