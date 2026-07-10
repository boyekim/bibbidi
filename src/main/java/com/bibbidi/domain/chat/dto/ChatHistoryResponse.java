package com.bibbidi.domain.chat.dto;

import java.util.List;

public record ChatHistoryResponse(
    List<ChatItemResponse> items
) {
}
