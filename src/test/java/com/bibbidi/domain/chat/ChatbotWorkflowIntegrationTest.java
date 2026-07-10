package com.bibbidi.domain.chat;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest(properties = "gpt.api-key=test-key")
@AutoConfigureMockMvc
class ChatbotWorkflowIntegrationTest {

    private static final String TOKEN = "chatbot-workflow-user";
    private static final String OTHER_TOKEN = "other-chatbot-user";
    private static final String DRAFT_REQUEST = """
        {
          "category": "hall",
          "name": "그랜드 힐 컨벤션",
          "status": "scheduled",
          "sourceMessage": "8월 3일 오후 2시 30분에 그랜드 힐 투어 가",
          "scheduleDate": "2026-08-03",
          "scheduleTime": "14:30",
          "scheduleTitle": "그랜드 힐 투어"
        }
        """;
    private static final String UPDATED_DRAFT_REQUEST = """
        {
          "category": "HALL",
          "name": "그랜드 힐 컨벤션",
          "status": "CONTRACTED",
          "contractDate": "2026-08-03",
          "totalAmount": 12000000,
          "depositAmount": 2000000,
          "balanceAmount": 10000000,
          "balanceDueDate": "2026-11-20",
          "sourceMessage": "웨딩홀 계약했어"
        }
        """;
    private static final String CHAT_MESSAGES_REQUEST = """
        {
          "items": [
            {"role":"user","kind":"text","payload":{"text":"웨딩홀 계약했어"}},
            {"role":"assistant","kind":"temp-card","payload":{"tempCard":{"id":"1","vendorName":"그랜드 힐 컨벤션"}}},
            {"role":"assistant","kind":"choices","payload":{"choices":["이대로 등록","취소"]}}
          ]
        }
        """;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void 임시_카드를_확정하면_채팅에서_추출한_일정을_저장한다() throws Exception {
        Long draftId = createDraft(TOKEN, DRAFT_REQUEST);

        confirmDraft(TOKEN, draftId).andExpect(status().isOk());

        mockMvc.perform(get("/api/events").header("token", TOKEN))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[?(@.title == '그랜드 힐 투어')].time").value("14:30"));
    }

    @Test
    void 진행_중인_임시_카드의_추출_정보를_갱신한다() throws Exception {
        Long draftId = createDraft(TOKEN, DRAFT_REQUEST);

        mockMvc.perform(put("/api/temp-cards/{id}", draftId)
                .header("token", TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(UPDATED_DRAFT_REQUEST))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(draftId));

        confirmDraft(TOKEN, draftId)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.card.totalAmount").value(12_000_000));
    }

    @Test
    void 다른_사용자의_임시_카드는_갱신할_수_없다() throws Exception {
        Long draftId = createDraft(TOKEN, DRAFT_REQUEST);

        mockMvc.perform(put("/api/temp-cards/{id}", draftId)
                .header("token", OTHER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(UPDATED_DRAFT_REQUEST))
            .andExpect(status().isNotFound());
    }

    @Test
    void 채팅_항목을_기록하고_시간순으로_불러온다() throws Exception {
        recordChatMessages(CHAT_MESSAGES_REQUEST);

        mockMvc.perform(get("/api/chat/history").header("token", TOKEN))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.items[-3].payload.text").value("웨딩홀 계약했어"))
            .andExpect(jsonPath("$.items[-2].payload.tempCard.vendorName").value("그랜드 힐 컨벤션"))
            .andExpect(jsonPath("$.items[-1].payload.choices[0]").value("이대로 등록"));
    }

    @Test
    void 역할과_종류가_맞지_않는_채팅_항목은_거부한다() throws Exception {
        String request = """
            {"items":[{"role":"user","kind":"choices","payload":{"choices":["취소"]}}]}
            """;

        mockMvc.perform(post("/api/chat/messages").header("token", TOKEN)
                .contentType(MediaType.APPLICATION_JSON).content(request))
            .andExpect(status().isBadRequest());
    }

    @Test
    void 진행_중인_업체_상태는_프론트_계약인_drafting으로_응답한다() throws Exception {
        mockMvc.perform(get("/api/overview").header("token", "status-contract-user"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.cards[?(@.category == 'studio')].status").value("drafting"));
    }

    private Long createDraft(String token, String request) throws Exception {
        String response = mockMvc.perform(post("/api/temp-cards").header("token", token)
                .contentType(MediaType.APPLICATION_JSON).content(request))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();
        Number draftId = JsonPath.read(response, "$.id");
        return draftId.longValue();
    }

    private ResultActions confirmDraft(String token, Long draftId) throws Exception {
        return mockMvc.perform(post("/api/temp-cards/{id}/confirm", draftId).header("token", token)
            .contentType(MediaType.APPLICATION_JSON).content("{\"action\":\"register\"}"));
    }

    private void recordChatMessages(String request) throws Exception {
        mockMvc.perform(post("/api/chat/messages").header("token", TOKEN)
                .contentType(MediaType.APPLICATION_JSON).content(request))
            .andExpect(status().isCreated());
    }
}
