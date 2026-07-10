package com.bibbidi.domain.chat.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

public record ChatMessagesRequest(
    @NotEmpty
    @Size(max = 20)
    List<@Valid ChatMessageRequest> items
) {
}
