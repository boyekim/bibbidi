package com.bibbidi.client.gpt;

import jakarta.validation.constraints.NotBlank;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "gpt")
public record GptProperties(
        @NotBlank
        String apiKey,
        String baseUrl,
        String model,
        Duration connectTimeout,
        Duration readTimeout
) {

    private static final String DEFAULT_BASE_URL = "https://api.openai.com/v1";
    private static final String DEFAULT_MODEL = "gpt-4o-mini";
    private static final Duration DEFAULT_CONNECT_TIMEOUT = Duration.ofSeconds(5);
    private static final Duration DEFAULT_READ_TIMEOUT = Duration.ofSeconds(60);

    public GptProperties {
        baseUrl = defaultText(baseUrl, DEFAULT_BASE_URL);
        model = defaultText(model, DEFAULT_MODEL);
        connectTimeout = defaultDuration(connectTimeout, DEFAULT_CONNECT_TIMEOUT);
        readTimeout = defaultDuration(readTimeout, DEFAULT_READ_TIMEOUT);
    }

    private static String defaultText(String value, String defaultValue) {
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        return value;
    }

    private static Duration defaultDuration(Duration value, Duration defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        validatePositive(value);
        return value;
    }

    private static void validatePositive(Duration value) {
        if (value.isZero() || value.isNegative()) {
            throw new IllegalArgumentException("GPT timeout must be positive.");
        }
    }
}
