package com.bibbidi.support.exception.errors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GptErrors implements Errors {

    GPT_API_ERROR("GPT API 호출에 실패했습니다. (%s)"),
    GPT_INVALID_REQUEST("GPT 요청 값이 올바르지 않습니다."),
    GPT_EMPTY_RESPONSE("GPT 응답이 비어 있습니다."),
    GPT_RESPONSE_REFUSED("GPT가 요청 처리를 거부했습니다."),
    GPT_RESPONSE_PARSE_FAILED("GPT 응답 파싱에 실패했습니다.");

    private final String message;

    @Override
    public String getCode() {
        return name();
    }
}
