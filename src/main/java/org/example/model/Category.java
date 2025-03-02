package org.example.model;

/**
 * 상품 카테고리 열거형(Enum) 클래스
 *
 * 무신사 코디 서비스에서 사용되는 8가지 상품 카테고리를 정의한다.
 * 각 카테고리는 내부적으로 사용되는 영문 이름(enum 상수)과 사용자에게 표시되는 한글 이름(displayName)을 가진다.
 *
 * 이 열거형은 Brand 엔티티의 가격 맵에서 키로 사용되며, 데이터베이스에는 STRING 타입으로 저장된다.
 */
public enum Category {
    /**
     * 상의 카테고리
     */
    TOP("상의"),

    /**
     * 아우터 카테고리
     */
    OUTER("아우터"),

    /**
     * 바지 카테고리
     */
    PANTS("바지"),

    /**
     * 스니커즈 카테고리
     */
    SNEAKERS("스니커즈"),

    /**
     * 가방 카테고리
     */
    BAG("가방"),

    /**
     * 모자 카테고리
     */
    HAT("모자"),

    /**
     * 양말 카테고리
     */
    SOCKS("양말"),

    /**
     * 액세서리 카테고리
     */
    ACCESSORY("액세서리");

    /**
     * 카테고리의 표시 이름(한글)
     *
     * UI에 표시되거나 사용자 입력 처리에 사용되는 사용자 친화적인 카테고리 이름
     */
    private final String displayName;

    /**
     * Category 열거형 생성자
     *
     * @param displayName 카테고리의 표시 이름(한글)
     */
    Category(String displayName) {
        this.displayName = displayName;
    }

    /**
     * 카테고리의 표시 이름(한글)을 반환하는 메서드
     *
     * @return 카테고리의 표시 이름(한글)
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * 표시 이름(한글)으로 해당하는 Category 열거형 상수를 찾아 반환하는 정적 메서드
     *
     * 주로 사용자 입력이나 URL 파라미터에서 카테고리를 찾을 때 사용된다.
     * 일치하는 카테고리가 없을 경우 IllegalArgumentException을 발생시킨다.
     *
     * @param displayName 찾고자 하는 카테고리의 표시 이름(한글)
     * @return 표시 이름과 일치하는 Category 열거형 상수
     * @throws IllegalArgumentException 일치하는 카테고리가 없을 경우 발생
     */
    public static Category fromDisplayName(String displayName) {
        // 모든 카테고리를 순회하며 일치하는 표시 이름을 가진 카테고리 찾기
        for (Category category : values()) {
            if (category.getDisplayName().equals(displayName)) {
                return category;
            }
        }
        // 일치하는 카테고리가 없을 경우 예외 발생
        throw new IllegalArgumentException("잘못된 카테고리 이름: " + displayName);
    }
}