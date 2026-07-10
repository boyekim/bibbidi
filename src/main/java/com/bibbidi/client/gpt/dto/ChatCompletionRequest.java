package com.bibbidi.client.gpt.dto;

import com.bibbidi.client.gpt.GptMessage;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import tools.jackson.databind.JsonNode;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ChatCompletionRequest(
        String model,
        List<GptMessage> messages,
        @JsonProperty("response_format") ResponseFormat responseFormat
) {

    public static ChatCompletionRequest text(String model, List<GptMessage> messages) {
        return new ChatCompletionRequest(model, messages, null);
    }

    public static ChatCompletionRequest structured(String model, List<GptMessage> messages, String schemaName, JsonNode schema) {
        return new ChatCompletionRequest(model, messages, ResponseFormat.jsonSchema(schemaName, schema));
    }

    public record ResponseFormat(
            String type,
            @JsonProperty("json_schema") JsonSchemaSpec jsonSchema
    ) {

        public static ResponseFormat jsonSchema(String name, JsonNode schema) {
            return new ResponseFormat("json_schema", new JsonSchemaSpec(name, true, schema));
        }
    }

    public record JsonSchemaSpec(String name, boolean strict, JsonNode schema) {
    }
}
