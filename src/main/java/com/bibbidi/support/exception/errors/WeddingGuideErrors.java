package com.bibbidi.support.exception.errors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WeddingGuideErrors implements Errors {

    GUIDE_NOT_FOUND("웨딩 준비 가이드를 찾을 수 없습니다.");

    private final String message;

    @Override
    public String getCode() {
        return name();
    }
}
