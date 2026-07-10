package com.bibbidi.domain.wedding;

import com.bibbidi.domain.user.User;
import com.bibbidi.domain.wedding.dto.CoupleUpdateRequest;
import com.bibbidi.domain.wedding.dto.CoupleUpdateResponse;
import com.bibbidi.support.auth.Auth;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CoupleApi {

    private final CoupleService coupleService;

    @PutMapping("/api/couple")
    public ResponseEntity<CoupleUpdateResponse> update(
        @Auth User user,
        @Valid @RequestBody CoupleUpdateRequest request
    ) {
        return ResponseEntity.ok(coupleService.update(user, request));
    }
}
