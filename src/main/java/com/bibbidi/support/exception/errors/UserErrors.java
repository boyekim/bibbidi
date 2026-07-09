package com.bibbidi.support.exception.errors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserErrors implements Errors {

    DUPLICATE_EMAIL("이미 가입된 이메일입니다."),
    INVALID_LOGIN("이메일 또는 비밀번호가 올바르지 않습니다."),
    LOGIN_REQUIRED("로그인이 필요합니다.");

    private final String message;

    @Override
    public String getCode() {
        return name();
    }
}
