package com.bibbidi.domain.bootstrap;

import com.bibbidi.domain.bootstrap.dto.BootstrapResponse;
import com.bibbidi.domain.user.User;
import com.bibbidi.support.auth.Auth;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BootstrapApi {

    private final BootstrapService bootstrapService;

    @GetMapping("/api/bootstrap")
    public ResponseEntity<BootstrapResponse> bootstrap(@Auth User user) {
        return ResponseEntity.ok(bootstrapService.bootstrap(user));
    }
}
