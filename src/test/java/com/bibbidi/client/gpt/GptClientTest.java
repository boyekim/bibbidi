package com.bibbidi.client.gpt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.bibbidi.support.exception.BadRequestException;
import com.bibbidi.support.exception.InternalServerException;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.json.JsonMapper;

class GptClientTest {

    private static final String CHAT_COMPLETIONS_URL = "https://api.openai.test/v1/chat/completions";
    private static final String SCHEMA = """
            {
              "type": "object",
              "properties": {
                "intent": {
                  "type": "string"
                },
                "vendorName": {
                  "type": "string"
                }
              },
              "required": [
                "intent",
                "vendorName"
              ],
              "additionalProperties": false
            }
            """;
    private static final String STRUCTURED_REQUEST = """
            {
              "model": "gpt-test",
              "messages": [
                {
                  "role": "system",
                  "content": "발화 분석기"
                },
                {
                  "role": "user",
                  "content": "AAA드레스 계약했어"
                }
              ],
              "response_format": {
                "type": "json_schema",
                "json_schema": {
                  "name": "extraction",
                  "strict": true,
                  "schema": {
                    "type": "object",
                    "properties": {
                      "intent": {
                        "type": "string"
                      },
                      "vendorName": {
                        "type": "string"
                      }
                    },
                    "required": [
                      "intent",
                      "vendorName"
                    ],
                    "additionalProperties": false
                  }
                }
              }
            }
            """;
    private static final String STRUCTURED_SUCCESS_RESPONSE = """
            {"choices":[{"message":{"content":"{\\"intent\\":\\"REGISTER\\",\\"vendorName\\":\\"AAA드레스\\"}"}}]}
            """;
    private static final String REFUSAL_RESPONSE = """
            {"choices":[{"message":{"refusal":"I'm sorry, I cannot assist with that request."}}]}
            """;
    private static final String INVALID_JSON_RESPONSE = """
            {"choices":[{"message":{"content":"not-json"}}]}
            """;

    record Extraction(String intent, String vendorName) {
    }

    private MockRestServiceServer server;
    private GptClient gptClient;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder()
                .baseUrl("https://api.openai.test/v1")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer test-key");
        server = MockRestServiceServer.bindTo(builder).build();
        gptClient = new GptClient(builder.build(), JsonMapper.builder().build(), "gpt-test");
    }

    @Test
    void 구조화_요청을_OpenAI_계약에_맞게_보내고_응답을_파싱한다() {
        expectStructuredSuccess();
        Extraction extraction = requestExtraction();

        assertThat(extraction.intent()).isEqualTo("REGISTER");
        assertThat(extraction.vendorName()).isEqualTo("AAA드레스");
        server.verify();
    }

    @Test
    void 거부_응답은_BadRequestException_으로_변환한다() {
        expectResponse(REFUSAL_RESPONSE);

        assertThatThrownBy(() -> gptClient.complete(List.of(GptMessage.user("위험한 요청"))))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("거부");
    }

    @Test
    void 빈_응답은_InternalServerException_으로_변환한다() {
        expectResponse("{\"choices\":[]}");

        assertThatThrownBy(() -> gptClient.complete(List.of(GptMessage.user("안녕"))))
                .isInstanceOf(InternalServerException.class)
                .hasMessageContaining("비어");
    }

    @Test
    void 구조화_응답_파싱_실패는_InternalServerException_으로_변환한다() {
        expectResponse(INVALID_JSON_RESPONSE);

        assertThatThrownBy(this::requestExtraction)
                .isInstanceOf(InternalServerException.class)
                .hasMessageContaining("파싱");
    }

    @Test
    void API_호출_실패는_InternalServerException_으로_변환한다() {
        server.expect(requestTo(CHAT_COMPLETIONS_URL))
                .andRespond(withServerError());

        assertThatThrownBy(() -> gptClient.complete(List.of(GptMessage.user("안녕"))))
                .isInstanceOf(InternalServerException.class)
                .hasMessageContaining("GPT API 호출에 실패했습니다");
    }

    @Test
    void 메시지가_비어있으면_BadRequestException_으로_변환한다() {
        assertThatThrownBy(() -> gptClient.complete(List.of()))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("GPT 요청 값");
    }

    @Test
    void 메시지에_null이_있으면_BadRequestException_으로_변환한다() {
        assertThatThrownBy(() -> gptClient.complete(Collections.singletonList(null)))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("GPT 요청 값");
    }

    private Extraction requestExtraction() {
        return gptClient.completeStructured(
                List.of(GptMessage.system("발화 분석기"), GptMessage.user("AAA드레스 계약했어")),
                "extraction",
                SCHEMA,
                Extraction.class
        );
    }

    private void expectStructuredSuccess() {
        server.expect(requestTo(CHAT_COMPLETIONS_URL))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer test-key"))
                .andExpect(content().json(STRUCTURED_REQUEST))
                .andRespond(withSuccess(STRUCTURED_SUCCESS_RESPONSE, MediaType.APPLICATION_JSON));
    }

    private void expectResponse(String response) {
        server.expect(requestTo(CHAT_COMPLETIONS_URL))
                .andRespond(withSuccess(response, MediaType.APPLICATION_JSON));
    }
}
