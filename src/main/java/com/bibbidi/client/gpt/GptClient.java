package com.bibbidi.client.gpt;

import com.bibbidi.client.gpt.dto.ChatCompletionRequest;
import com.bibbidi.client.gpt.dto.ChatCompletionResponse;
import com.bibbidi.support.exception.BadRequestException;
import com.bibbidi.support.exception.InternalServerException;
import com.bibbidi.support.exception.errors.GptErrors;
import java.util.List;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

public class GptClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String model;

    public GptClient(RestClient restClient, ObjectMapper objectMapper, String model) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
        this.model = model;
    }

    public String complete(List<GptMessage> messages) {
        validateMessages(messages);
        return call(ChatCompletionRequest.text(model, messages));
    }

    public <T> T completeStructured(
            List<GptMessage> messages,
            String schemaName,
            String jsonSchema,
            Class<T> responseType
    ) {
        validateMessages(messages);
        JsonNode schema = parseSchema(jsonSchema);
        String content = call(ChatCompletionRequest.structured(model, messages, schemaName, schema));
        return parseContent(content, responseType);
    }

    private void validateMessages(List<GptMessage> messages) {
        if (messages == null || messages.isEmpty()) {
            throw new BadRequestException(GptErrors.GPT_INVALID_REQUEST);
        }
        validateMessageElements(messages);
    }

    private void validateMessageElements(List<GptMessage> messages) {
        for (GptMessage message : messages) {
            validateMessage(message);
        }
    }

    private void validateMessage(GptMessage message) {
        if (message == null) {
            throw new BadRequestException(GptErrors.GPT_INVALID_REQUEST);
        }
    }

    private <T> T parseContent(String content, Class<T> responseType) {
        try {
            return objectMapper.readValue(content, responseType);
        } catch (JacksonException exception) {
            throw new InternalServerException(GptErrors.GPT_RESPONSE_PARSE_FAILED);
        }
    }

    private String call(ChatCompletionRequest request) {
        return extractContent(request(request));
    }

    private ChatCompletionResponse request(ChatCompletionRequest request) {
        try {
            return restClient.post()
                    .uri("/chat/completions")
                    .body(request)
                    .retrieve()
                    .body(ChatCompletionResponse.class);
        } catch (RestClientResponseException exception) {
            String status = "status: " + exception.getStatusCode().value();
            throw new InternalServerException(GptErrors.GPT_API_ERROR, status);
        } catch (RestClientException exception) {
            throw new InternalServerException(GptErrors.GPT_API_ERROR, "connection");
        }
    }

    private String extractContent(ChatCompletionResponse response) {
        if (response == null) {
            throw new InternalServerException(GptErrors.GPT_EMPTY_RESPONSE);
        }
        validateRefusal(response);
        String content = response.firstContent();
        if (content == null || content.isBlank()) {
            throw new InternalServerException(GptErrors.GPT_EMPTY_RESPONSE);
        }
        return content;
    }

    private void validateRefusal(ChatCompletionResponse response) {
        String refusal = response.firstRefusal();
        if (refusal == null || refusal.isBlank()) {
            return;
        }
        throw new BadRequestException(GptErrors.GPT_RESPONSE_REFUSED);
    }

    private JsonNode parseSchema(String jsonSchema) {
        try {
            return objectMapper.readTree(jsonSchema);
        } catch (JacksonException exception) {
            throw new IllegalStateException("Invalid GPT JSON schema.", exception);
        }
    }
}
