package com.bibbidi.domain.overview;

import com.bibbidi.domain.overview.dto.OverviewResponse;
import com.bibbidi.domain.user.User;
import com.bibbidi.support.auth.Auth;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OverviewApi {

    private final OverviewService overviewService;

    @GetMapping("/api/overview")
    public ResponseEntity<OverviewResponse> overview(@Auth User user) {
        return ResponseEntity.ok(overviewService.getOverview(user));
    }
}
