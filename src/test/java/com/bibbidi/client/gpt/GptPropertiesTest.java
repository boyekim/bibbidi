package com.bibbidi.client.gpt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.time.Duration;
import org.junit.jupiter.api.Test;

class GptPropertiesTest {

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void API_키가_비어있으면_검증에_실패한다() {
        GptProperties properties = new GptProperties("", null, null, null, null);

        assertThat(VALIDATOR.validate(properties)).isNotEmpty();
    }

    @Test
    void 선택_설정값이_없으면_기본값을_사용한다() {
        GptProperties properties = new GptProperties("test-key", null, null, null, null);

        assertThat(properties.baseUrl()).isEqualTo("https://api.openai.com/v1");
        assertThat(properties.model()).isEqualTo("gpt-4o-mini");
        assertThat(properties.connectTimeout()).isEqualTo(Duration.ofSeconds(5));
        assertThat(properties.readTimeout()).isEqualTo(Duration.ofSeconds(60));
    }

    @Test
    void 타임아웃은_양수여야_한다() {
        assertThatThrownBy(() -> new GptProperties("test-key", null, null, Duration.ZERO, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("timeout");
    }
}
