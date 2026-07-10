package com.bibbidi.domain.wedding.dto;

import com.bibbidi.domain.user.User;
import com.bibbidi.domain.wedding.WeddingProfile;
import java.time.LocalDate;

public record CoupleUpdateResponse(
    String nameA,
    String nameB,
    LocalDate weddingDate,
    Long totalBudget,
    Long spent
) {

    public static CoupleUpdateResponse from(WeddingProfile weddingProfile, User user, long spent) {
        return new CoupleUpdateResponse(
            user.getName(),
            weddingProfile.getPartnerName(),
            weddingProfile.getWeddingDate(),
            weddingProfile.getTotalBudget(),
            spent
        );
    }
}
