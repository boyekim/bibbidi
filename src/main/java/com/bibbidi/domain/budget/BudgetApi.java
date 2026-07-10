package com.bibbidi.domain.budget;

import com.bibbidi.domain.budget.dto.BudgetResponse;
import com.bibbidi.domain.user.User;
import com.bibbidi.support.auth.Auth;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BudgetApi {

    private final BudgetService budgetService;

    @GetMapping("/api/budget")
    public ResponseEntity<BudgetResponse> getBudget(@Auth User user) {
        return ResponseEntity.ok(budgetService.getBudget(user));
    }
}
