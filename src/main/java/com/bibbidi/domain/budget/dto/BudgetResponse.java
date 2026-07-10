package com.bibbidi.domain.budget.dto;

import com.bibbidi.domain.overview.dto.OverviewCoupleResponse;
import java.util.List;

public record BudgetResponse(
    OverviewCoupleResponse couple,
    List<BudgetItemResponse> items
) {
}
