# 무신사 코디 서비스

8개의 카테고리에서 상품을 하나씩 구매하여 코디를 완성하는 서비스입니다. 각 브랜드별 상품 가격 정보를 기반으로 최저가 조회 및 브랜드 관리 기능을 제공합니다.

## 구현 범위에 대한 설명

### 1. 구현된 API

본 프로젝트에서는 요구사항에 명시된 4개의 API를 모두 구현하였습니다:

#### API 1: 카테고리별 최저가격 브랜드와 상품가격, 총액을 조회하는 API
- 엔드포인트: `GET /api/lowest-price-by-category`
- 기능: 8개 카테고리 각각에 대해 최저가격을 제공하는 브랜드와 가격을 조회하고 총액을 계산
- 특이사항: 같은 최저가격을 제공하는 브랜드가 여러 개인 경우 모두 표시 (예: 스니커즈 카테고리의 A,G 브랜드)

#### API 2: 단일 브랜드로 모든 카테고리 상품을 구매할 때 최저가격 브랜드 조회 API
- 엔드포인트: `GET /api/lowest-total-price-brand`
- 기능: 단일 브랜드에서 모든 카테고리 제품을 구매할 때 가장 저렴한 브랜드와 각 카테고리별 가격, 총액을 조회

#### API 3: 카테고리 이름으로 최저, 최고 가격 브랜드와 상품 가격을 조회하는 API
- 엔드포인트: `GET /api/min-max-price-by-category?categoryName={카테고리명}`
- 기능: 특정 카테고리에 대해 최저가와 최고가를 제공하는 브랜드 및 가격 정보 조회
- 특이사항: 동일한 가격을 제공하는 브랜드가 여러 개인 경우 모두 표시

#### API 4: 브랜드 및 상품을 추가/업데이트/삭제하는 API
- 엔드포인트:
    - 브랜드 생성: `POST /api/brand`
    - 브랜드 수정: `PUT /api/brand/{id}`
    - 브랜드 삭제: `DELETE /api/brand/{id}`
    - 브랜드 가격 수정: `PUT /api/brand/price`
- 기능: 운영자가 새로운 브랜드를 등록하고, 모든 브랜드의 상품을 추가, 변경, 삭제할 수 있는 기능 제공

### 2. 프론트엔드 구현

요구사항의 가산점 항목인 프론트엔드를 Thymeleaf를 활용하여 구현하였습니다:

- **메인 페이지**: 4개의 API에 접근할 수 있는 링크 제공
- **카테고리별 최저가격 조회 페이지**: API 1의 결과를 표형태로 시각화
- **단일 브랜드 최저 총액 조회 페이지**: API 2의 결과를 표형태로 시각화
- **카테고리별 최저/최고 가격 조회 페이지**: API 3의 입력 폼 및 결과 화면
- **브랜드 관리 페이지**: 브랜드 목록 조회, 추가, 수정, 삭제 기능 제공

### 3. 테스트 작성

요구사항의 가산점 항목인 테스트를 구현하였습니다:

- **단위 테스트**: 서비스 및 컨트롤러 계층에 대한 단위 테스트 구현
    - `BrandServiceTest.java`: 서비스 계층 단위 테스트
    - `ApiControllerTest.java`: API 컨트롤러 단위 테스트
- **통합 테스트**: 전체 애플리케이션에 대한 통합 테스트 구현
    - `BrandShoppingIntegrationTest.java`: API 및 데이터 접근 통합 테스트

## 코드 빌드, 테스트, 실행 방법

### 필요 환경

- JDK 17 이상
- Gradle 7.0 이상
- 웹 브라우저 (Chrome, Firefox, Safari 등)

### 빌드 방법

프로젝트 루트 디렉토리에서 다음 명령어를 실행합니다:

```bash
# Windows
gradlew clean build

# Linux/Mac
./gradlew clean build
```

### 테스트 실행 방법

프로젝트 루트 디렉토리에서 다음 명령어를 실행합니다:

```bash
# Windows
gradlew test

# Linux/Mac
./gradlew test
```

개별 테스트 실행:

```bash
# Windows
gradlew test --tests "com.example.service.BrandServiceTest"

# Linux/Mac
./gradlew test --tests "com.example.service.BrandServiceTest"
```

### 애플리케이션 실행 방법

프로젝트 루트 디렉토리에서 다음 명령어를 실행합니다:

```bash
# Windows
gradlew bootRun

# Linux/Mac
./gradlew bootRun
```

또는 빌드 후 생성된 JAR 파일을 직접 실행할 수 있습니다:

```bash
java -jar build/libs/musinsa-coord-service-1.0.0.jar
```

애플리케이션이 실행되면 웹 브라우저에서 `http://localhost:8080`으로 접속하여 서비스를 이용할 수 있습니다.

### API 테스트 방법

Postman이나 curl을 사용하여 API를 직접 테스트할 수 있습니다:

```bash
# API 1 테스트
curl -X GET http://localhost:8080/api/lowest-price-by-category

# API 2 테스트
curl -X GET http://localhost:8080/api/lowest-total-price-brand

# API 3 테스트
curl -X GET http://localhost:8080/api/min-max-price-by-category?categoryName=상의

# API 4 테스트 (브랜드 추가)
curl -X POST http://localhost:8080/api/brand \
  -H "Content-Type: application/json" \
  -d '{"name":"TestBrand","prices":{"TOP":10000,"OUTER":6000,"PANTS":3500,"SNEAKERS":9200,"BAG":2100,"HAT":1600,"SOCKS":2000,"ACCESSORY":2200}}'
```

## 기타 추가 정보

### 프로젝트 구조

```
org.example
├── BrandShoppingApplication.java  # 애플리케이션 시작점
├── model                          # 데이터 모델
│   ├── Brand.java                 # 브랜드 엔티티
│   └── Category.java              # 카테고리 열거형
├── repository                     # 데이터 접근 계층
│   └── BrandRepository.java       # 브랜드 리포지토리
├── service                        # 비즈니스 로직 계층
│   └── BrandService.java          # 브랜드 서비스
├── dto                            # 데이터 전송 객체
│   ├── BrandDto.java
│   ├── BrandPriceUpdateDto.java
│   ├── CategoryPriceDto.java
│   ├── LowestPriceResponseDto.java
│   ├── LowestTotalPriceResponseDto.java
│   └── MinMaxPriceResponseDto.java
├── controller                     # 컨트롤러 계층
│   ├── ApiController.java         # REST API 컨트롤러
│   └── WebController.java         # 웹 컨트롤러
└── resources                      # 리소스 파일
    ├── application.properties     # 애플리케이션 설정
    └── templates                  # Thymeleaf 템플릿
        ├── index.html             # 메인 페이지
        ├── lowest-price-by-category.html
        ├── lowest-total-price-brand.html
        ├── min-max-price-by-category.html
        ├── min-max-price-result.html
        ├── manage-brands.html
        ├── add-brand.html
        ├── edit-brand.html
        └── error.html
```

### 기술 스택

- **Backend**:
    - Spring Boot 3.1.0
    - Spring Data JPA
    - H2 Database (인메모리)
    - Lombok
    - JUnit 5

- **Frontend**:
    - Thymeleaf
    - Bootstrap 5
    - Bootstrap Icons

### 데이터베이스 접근

H2 콘솔은 `http://localhost:8080/h2-console`로 접속할 수 있습니다.
- JDBC URL: `jdbc:h2:mem:testdb`
- 사용자명: `sa`
- 비밀번호: (빈 값)

### 추가 기능

- **콤마 포맷팅**: 모든 가격 정보에 천 단위 구분자(콤마) 적용
- **다중 최저가/최고가 브랜드 표시**: 동일한 가격을 제공하는 모든 브랜드 표시
- **예외 처리**: 모든 API 및 웹 요청에 대한 상세한 예외 처리 및 에러 페이지 제공
- **로깅**: 디버깅 및 문제 해결을 위한 상세 로깅 구현

### 향후 개선 사항

- 브랜드 및 상품 검색 기능
- 사용자 인증 및 권한 관리
- 실시간 가격 비교 기능
- 상품 이미지 및 상세 정보 추가
- 모바일 최적화 UI 개선