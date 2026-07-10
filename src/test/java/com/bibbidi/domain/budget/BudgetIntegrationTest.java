package com.bibbidi.domain.budget;

import static org.hamcrest.Matchers.hasSize;
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
class BudgetIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void 예산_보드에_지출_요약과_납부_항목을_반환한다() throws Exception {
        mockMvc.perform(get("/api/budget").header("token", "budget-user"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.couple.totalBudget").value(45_000_000))
            .andExpect(jsonPath("$.couple.spent").value(3_000_000))
            .andExpect(jsonPath("$.items", hasSize(2)))
            .andExpect(jsonPath("$.items[0].categoryLabel").value("웨딩홀"))
            .andExpect(jsonPath("$.items[0].payments", hasSize(2)))
            .andExpect(jsonPath("$.items[0].payments[0].paidBy").doesNotExist());
    }
}
