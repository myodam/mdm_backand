package com.ux.mbm.global.exception;

import com.ux.mbm.global.code.ErrorCode;
import lombok.Getter;

/**
 * 백엔드 비즈니스 로직 예외
 * ErrorCode를 담아 GlobalExceptionHandler에서 통합 처리합니다.
 */
@Getter
public class CustomException extends RuntimeException {

    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
