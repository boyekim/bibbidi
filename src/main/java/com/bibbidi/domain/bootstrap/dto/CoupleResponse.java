package com.bibbidi.domain.bootstrap.dto;

import com.bibbidi.domain.user.User;
import com.bibbidi.domain.wedding.WeddingProfile;
import java.time.LocalDate;

public record CoupleResponse(
    String id,
    String nameA,
    String nameB,
    LocalDate weddingDate,
    Long totalBudget,
    Long spent
) {

    public static CoupleResponse from(WeddingProfile weddingProfile, User user, long spent) {
        return new CoupleResponse(
            String.valueOf(weddingProfile.getId()),
            user.getName(),
            weddingProfile.getPartnerName(),
            weddingProfile.getWeddingDate(),
            weddingProfile.getTotalBudget(),
            spent
        );
    }
}
