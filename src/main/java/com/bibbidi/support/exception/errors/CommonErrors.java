package com.bibbidi.support.exception.errors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommonErrors implements Errors {

    INVALID_REQUEST("요청 값이 올바르지 않습니다."),
    INTERNAL_SERVER_ERROR("서버 내부 오류가 발생했습니다.");

    private final String message;

    @Override
    public String getCode() {
        return name();
    }
}
