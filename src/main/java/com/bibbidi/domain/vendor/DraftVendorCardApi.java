package com.bibbidi.domain.vendor;

import com.bibbidi.domain.user.User;
import com.bibbidi.domain.vendor.dto.DraftVendorCardRequest;
import com.bibbidi.domain.vendor.dto.DraftVendorCardResponse;
import com.bibbidi.support.auth.Auth;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DraftVendorCardApi {

    private final DraftVendorCardService draftVendorCardService;

    @PostMapping("/api/temp-cards")
    public ResponseEntity<DraftVendorCardResponse> create(
        @Auth User user,
        @Valid @RequestBody DraftVendorCardRequest request
    ) {
        DraftVendorCardResponse response = draftVendorCardService.create(user, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/api/temp-cards/{draftId}")
    public ResponseEntity<DraftVendorCardResponse> update(
        @Auth User user,
        @PathVariable Long draftId,
        @Valid @RequestBody DraftVendorCardRequest request
    ) {
        return ResponseEntity.ok(draftVendorCardService.update(user, draftId, request));
    }
}
