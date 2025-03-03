package org.example.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 클래스 설명 : 전역 예외 처리를 담당하는 컨트롤러 어드바이스
 * 애플리케이션 전체에서 발생하는 예외를 중앙에서 처리하고,
 * 클라이언트에게 일관된 형식의 오류 응답을 제공합니다.
 * 각 예외 유형에 따라 적절한 HTTP 상태 코드와 오류 메시지를 반환합니다.
 * 작성자 : sys1021
 * 작성일 : 2025.03.03
 */
@ControllerAdvice  // 모든 컨트롤러에 적용되는 글로벌 예외 처리기 설정
public class GlobalExceptionHandler {

    /**
     * 로깅을 위한 Logger 인스턴스
     * 예외 발생 시 로그를 기록하는 데 사용됩니다.
     */
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 메서드 설명 : 요청 본문 파싱 오류(JSON 파싱 오류 등) 처리
     * HTTP 요청 본문을 읽는 과정에서 발생하는 오류를 처리합니다.
     * 주로 잘못된 JSON 형식, 필수 필드 누락, 타입 불일치 등의 문제를 다룹니다.
     * @param ex 발생한 HttpMessageNotReadableException 예외 객체
     * @return 오류 정보를 담은 ResponseEntity 객체
     * 작성자 : sys1021
     * 작성일 : 2025.03.02
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {

        logger.error("요청 본문 파싱 오류:", ex);

        String errorMessage = "요청 본문 파싱 오류";
        Throwable cause = ex.getCause();

        // JSON 처리 관련 예외의 경우 더 상세한 메시지 제공
        if (cause instanceof JsonProcessingException) {
            errorMessage = "JSON 형식 오류: " + cause.getMessage();
        }

        ErrorResponse errorResponse = ErrorResponse.builder()
                .error("요청 형식 오류")
                .message(errorMessage)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);

    }

    /**
     * 메서드 설명 : 잘못된 인자 예외 처리
     * 메서드에 전달된 인자가 유효하지 않을 때 발생하는 예외를 처리합니다.
     * 주로 유효하지 않은 카테고리명, 브랜드명 등의 문제를 다룹니다.
     * @param ex 발생한 IllegalArgumentException 예외 객체
     * @return 오류 정보를 담은 ResponseEntity 객체
     * 작성자 : sys1021
     * 작성일 : 2025.03.02
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {

        logger.error("잘못된 인자 예외:", ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .error("잘못된 인자")
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);

    }

    /**
     * 메서드 설명 : 일반 예외 처리
     * 다른 예외 핸들러에서 처리되지 않은 모든 예외를 처리하는 폴백 핸들러입니다.
     * 예상치 못한 서버 오류나 내부 처리 문제 등을 다룹니다.
     * @param ex 발생한 Exception 예외 객체
     * @return 오류 정보를 담은 ResponseEntity 객체
     * 작성자 : sys1021
     * 작성일 : 2025.03.02
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {

        logger.error("서버 오류:", ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .error("서버 오류")
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);

    }

}