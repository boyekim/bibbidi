package com.bibbidi.domain.chat.dto;

import com.bibbidi.domain.chat.ChatItem;
import tools.jackson.databind.JsonNode;

public record ChatItemResponse(
    String id,
    String role,
    String kind,
    JsonNode payload
) {

    public static ChatItemResponse of(ChatItem chatItem, JsonNode payload) {
        return new ChatItemResponse(
            String.valueOf(chatItem.getId()),
            chatItem.getRole(),
            chatItem.getKind(),
            payload
        );
    }
}
