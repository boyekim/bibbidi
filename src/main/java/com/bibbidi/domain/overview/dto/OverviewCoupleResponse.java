package com.bibbidi.domain.overview.dto;

import com.bibbidi.domain.user.User;
import com.bibbidi.domain.wedding.WeddingProfile;
import java.time.LocalDate;

public record OverviewCoupleResponse(
    String nameA,
    String nameB,
    LocalDate weddingDate,
    Long totalBudget,
    Long spent
) {

    public static OverviewCoupleResponse from(WeddingProfile weddingProfile, User user, long spent) {
        return new OverviewCoupleResponse(
            user.getName(),
            weddingProfile.getPartnerName(),
            weddingProfile.getWeddingDate(),
            weddingProfile.getTotalBudget(),
            spent
        );
    }
}
