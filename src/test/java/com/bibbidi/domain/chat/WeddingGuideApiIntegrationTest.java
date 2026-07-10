package com.bibbidi.domain.chat;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = "gpt.api-key=test-key")
@AutoConfigureMockMvc
class WeddingGuideApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void 네_카테고리의_질의응답_가이드를_제공한다() throws Exception {
        // when & then
        assertGuide("hall", "웨딩홀");
        assertGuide("studio", "스튜디오");
        assertGuide("dress", "드레스");
        assertGuide("makeup", "메이크업");
    }

    @Test
    void 지원하지_않는_카테고리는_조회할_수_없다() throws Exception {
        // when & then
        mockMvc.perform(get("/api/chat/guides/catering"))
            .andExpect(status().isNotFound());
    }

    private void assertGuide(String category, String title) throws Exception {
        mockMvc.perform(get("/api/chat/guides/{category}", category))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.category").value(category))
            .andExpect(jsonPath("$.content").value(containsString(title)))
            .andExpect(jsonPath("$.content").value(containsString("후속 질문")))
            .andExpect(jsonPath("$.content").value(containsString("응답 규칙")));
    }
}
