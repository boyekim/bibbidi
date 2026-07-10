package com.bibbidi.client.gpt;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.bibbidi.support.exception.BadRequestException;
import org.junit.jupiter.api.Test;

class GptMessageTest {

    @Test
    void 역할이_비어있으면_생성에_실패한다() {
        assertThatThrownBy(() -> new GptMessage(" ", "안녕"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("GPT 요청 값");
    }

    @Test
    void 내용이_비어있으면_생성에_실패한다() {
        assertThatThrownBy(() -> GptMessage.user(" "))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("GPT 요청 값");
    }
}
