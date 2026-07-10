package com.bibbidi.domain.chat.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import tools.jackson.databind.JsonNode;

public record ChatMessageRequest(
    @NotBlank
    String role,
    @NotBlank
    String kind,
    @NotNull
    JsonNode payload
) {

    private static final Set<String> USER_KINDS = Set.of("text", "image");
    private static final Set<String> ASSISTANT_KINDS = Set.of("text", "temp-card", "choices");

    @AssertTrue(message = "지원하지 않는 채팅 항목입니다.")
    public boolean isSupported() {
        return hasSupportedRoleAndKind() && hasSupportedPayload();
    }

    private boolean hasSupportedRoleAndKind() {
        if ("user".equals(role)) {
            return USER_KINDS.contains(kind);
        }
        if ("assistant".equals(role)) {
            return ASSISTANT_KINDS.contains(kind);
        }
        return false;
    }

    private boolean hasSupportedPayload() {
        if ("text".equals(kind)) {
            return hasSingleText("text", 10_000);
        }
        if ("image".equals(kind)) {
            return hasSingleText("fileName", 255);
        }
        if ("temp-card".equals(kind)) {
            return hasTempCard();
        }
        if ("choices".equals(kind)) {
            return hasChoices();
        }
        return false;
    }

    private boolean hasSingleText(String fieldName, int maximumLength) {
        if (!hasSingleField(fieldName)) {
            return false;
        }
        JsonNode value = payload.get(fieldName);
        return value.isString() && !value.stringValue().isBlank() && value.stringValue().length() <= maximumLength;
    }

    private boolean hasTempCard() {
        if (!hasSingleField("tempCard")) {
            return false;
        }
        return payload.get("tempCard").isObject();
    }

    private boolean hasChoices() {
        if (!hasSingleField("choices")) {
            return false;
        }
        JsonNode choices = payload.get("choices");
        return choices.isArray() && !choices.isEmpty() && choices.size() <= 20
            && choices.valueStream().allMatch(this::isValidChoice);
    }

    private boolean isValidChoice(JsonNode choice) {
        return choice.isString() && !choice.stringValue().isBlank() && choice.stringValue().length() <= 100;
    }

    private boolean hasSingleField(String fieldName) {
        return payload != null && payload.isObject() && payload.size() == 1 && payload.has(fieldName);
    }
}
