package com.bibbidi.client.gpt.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ChatCompletionResponse(List<Choice> choices) {

    public String firstContent() {
        Message message = firstMessage();
        if (message == null) {
            return null;
        }
        return message.content();
    }

    public String firstRefusal() {
        Message message = firstMessage();
        if (message == null) {
            return null;
        }
        return message.refusal();
    }

    private Message firstMessage() {
        if (choices == null || choices.isEmpty() || choices.getFirst() == null) {
            return null;
        }
        return choices.getFirst().message();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Choice(Message message) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Message(String content, String refusal) {
    }
}
