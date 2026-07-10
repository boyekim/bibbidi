package com.bibbidi.support.exception.errors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VendorErrors implements Errors {

    CARD_NOT_FOUND("카드를 찾을 수 없습니다."),
    DRAFT_CARD_NOT_FOUND("임시 카드를 찾을 수 없습니다."),
    INVALID_DRAFT_CARD("임시 카드 정보가 올바르지 않습니다."),
    INVALID_CONFIRM_ACTION("확정 액션이 올바르지 않습니다.");

    private final String message;

    @Override
    public String getCode() {
        return name();
    }
}
