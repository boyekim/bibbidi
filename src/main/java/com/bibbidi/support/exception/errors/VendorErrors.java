package com.bibbidi.support.exception.errors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VendorErrors implements Errors {

    CARD_NOT_FOUND("카드를 찾을 수 없습니다.");

    private final String message;

    @Override
    public String getCode() {
        return name();
    }
}
