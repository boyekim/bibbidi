package com.bibbidi.domain.vendor;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest(properties = "gpt.api-key=test-key")
@AutoConfigureMockMvc
class CardAnalysisApplyIntegrationTest {

    private static final String TOKEN = "analysis-apply-user";
    private static final String OTHER_TOKEN = "other-analysis-user";
    private static final String APPLY_REQUEST = """
        {
          "events": [{
            "title": "드레스 가봉",
            "date": "2026-07-12",
            "time": "14:00",
            "quote": "가봉 7/12 오후 2시"
          }],
          "payments": [{
            "label": "추가금",
            "amount": 360000,
            "dueDate": null,
            "quote": "추가금 36만원"
          }]
        }
        """;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void 수락한_일정과_납부_제안을_저장하고_재시도에도_중복_생성하지_않는다() throws Exception {
        // given
        String cardId = dressCardId(TOKEN);

        // when
        apply(TOKEN, cardId).andExpect(status().isOk());

        // then
        assertAppliedItems(cardId);
    }

    private void assertAppliedItems(String cardId) throws Exception {
        apply(TOKEN, cardId)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.events[?(@.title == '드레스 가봉')]").value(hasSize(1)))
            .andExpect(jsonPath("$.events[?(@.title == '드레스 가봉')].time").value("14:00"))
            .andExpect(jsonPath("$.payments[?(@.label == '추가금')]").value(hasSize(1)))
            .andExpect(jsonPath("$.payments[?(@.label == '추가금')].dueDate").value("2026-10-24"))
            .andExpect(jsonPath("$.payments[?(@.label == '추가금')].memo").value("추가금 36만원"));
    }

    @Test
    void 다른_사용자의_카드에는_분석_결과를_저장할_수_없다() throws Exception {
        // given
        String cardId = dressCardId(TOKEN);

        // when
        ResultActions response = apply(OTHER_TOKEN, cardId);

        // then
        response.andExpect(status().isNotFound());
    }

    private String dressCardId(String token) throws Exception {
        String response = mockMvc.perform(get("/api/overview").header("token", token))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        List<String> cardIds = JsonPath.read(response, "$.cards[?(@.category == 'dress')].id");
        return cardIds.getFirst();
    }

    private ResultActions apply(String token, String cardId) throws Exception {
        return mockMvc.perform(post("/api/cards/{id}/analyze/apply", cardId)
            .header("token", token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(APPLY_REQUEST));
    }
}
