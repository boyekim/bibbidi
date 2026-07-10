package com.bibbidi.client.gpt;

import com.bibbidi.support.exception.BadRequestException;
import com.bibbidi.support.exception.errors.GptErrors;

public record GptMessage(String role, String content) {

    public GptMessage {
        validateNotBlank(role);
        validateNotBlank(content);
    }

    public static GptMessage system(String content) {
        return new GptMessage("system", content);
    }

    public static GptMessage user(String content) {
        return new GptMessage("user", content);
    }

    public static GptMessage assistant(String content) {
        return new GptMessage("assistant", content);
    }

    private static void validateNotBlank(String value) {
        if (value == null || value.isBlank()) {
            throw new BadRequestException(GptErrors.GPT_INVALID_REQUEST);
        }
    }
}
