package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 클래스 설명 : API 오류 응답을 위한 데이터 전송 객체(DTO)
 * API 요청 처리 중 발생한 오류 정보를 클라이언트에게 일관된 형식으로 전달하기 위한 클래스입니다.
 * 오류 유형과 상세 메시지를 포함합니다.
 * 작성자 : sys1021
 * 작성일 : 2025.03.03
 */
@Data               // Lombok: getter, setter, equals, hashCode, toString 자동 생성
@NoArgsConstructor  // Lombok: 파라미터 없는 기본 생성자 자동 생성
@AllArgsConstructor // Lombok: 모든 필드를 파라미터로 받는 생성자 자동 생성
@Builder            // Lombok: 빌더 패턴 구현을 자동으로 생성
public class ErrorResponse {

    /**
     * 오류 유형
     * 발생한 오류의 종류를 나타내는 문자열입니다.
     * 예: "요청 형식 오류", "잘못된 인자", "서버 오류" 등
     */
    private String error;

    /**
     * 오류 메시지
     * 오류에 대한 상세 설명을 제공하는 문자열입니다.
     * 클라이언트가 문제를 이해하고 해결하는 데 도움이 되는 정보를 포함합니다.
     */
    private String message;

}