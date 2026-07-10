package com.bibbidi.domain.vendor;

import com.bibbidi.domain.user.User;
import com.bibbidi.domain.vendor.dto.VendorCardDetailResponse;
import com.bibbidi.domain.vendor.dto.VendorCardResponses;
import com.bibbidi.support.auth.Auth;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class VendorCardApi {

    private final VendorCardService vendorCardService;

    @GetMapping("/api/cards")
    public ResponseEntity<VendorCardResponses> getCards(@Auth User user) {
        return ResponseEntity.ok(vendorCardService.getCards(user));
    }

    @GetMapping("/api/cards/{cardId}")
    public ResponseEntity<VendorCardDetailResponse> getCard(
        @Auth User user,
        @PathVariable Long cardId
    ) {
        return ResponseEntity.ok(vendorCardService.getCard(user, cardId));
    }
}
