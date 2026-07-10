package com.bibbidi.domain.overview.dto;

import java.util.List;

public record OverviewResponse(
    OverviewCoupleResponse couple,
    List<OverviewCardResponse> cards,
    List<OverviewEventResponse> upcomingEvents
) {
}
