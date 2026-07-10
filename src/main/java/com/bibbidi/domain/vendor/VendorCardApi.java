package com.bibbidi.domain.vendor;

import com.bibbidi.domain.user.User;
import com.bibbidi.domain.vendor.dto.CardAnalysisApplyRequest;
import com.bibbidi.domain.vendor.dto.CardEventResponse;
import com.bibbidi.domain.vendor.dto.TempCardConfirmRequest;
import com.bibbidi.domain.vendor.dto.TempCardConfirmResponse;
import com.bibbidi.domain.vendor.dto.VendorCardResponse;
import com.bibbidi.domain.vendor.dto.VendorCardDetailResponse;
import com.bibbidi.domain.vendor.dto.VendorCardMemoUpdateRequest;
import com.bibbidi.domain.vendor.dto.VendorCardResponses;
import com.bibbidi.support.auth.Auth;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
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

    @PutMapping("/api/cards/{cardId}/memo")
    public ResponseEntity<VendorCardResponse> updateMemo(
        @Auth User user,
        @PathVariable Long cardId,
        @Valid @RequestBody VendorCardMemoUpdateRequest request
    ) {
        return ResponseEntity.ok(vendorCardService.updateMemo(user, cardId, request));
    }

    @PostMapping("/api/cards/{cardId}/analyze/apply")
    public ResponseEntity<VendorCardDetailResponse> applyAnalysis(
        @Auth User user,
        @PathVariable Long cardId,
        @Valid @RequestBody CardAnalysisApplyRequest request
    ) {
        return ResponseEntity.ok(vendorCardService.applyAnalysis(user, cardId, request));
    }

    @GetMapping("/api/events")
    public ResponseEntity<List<CardEventResponse>> getEvents(
        @Auth User user,
        @RequestParam(required = false) LocalDate from,
        @RequestParam(required = false) LocalDate to
    ) {
        return ResponseEntity.ok(vendorCardService.getEvents(user, from, to));
    }

    @PostMapping("/api/temp-cards/{tempCardId}/confirm")
    public ResponseEntity<TempCardConfirmResponse> confirmTempCard(
        @Auth User user,
        @PathVariable Long tempCardId,
        @Valid @RequestBody TempCardConfirmRequest request
    ) {
        return ResponseEntity.ok(vendorCardService.confirmTempCard(user, tempCardId, request));
    }
}
