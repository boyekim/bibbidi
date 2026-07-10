package com.bibbidi.domain.chat;

import com.bibbidi.domain.chat.dto.WeddingGuideResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WeddingGuideApi {

    private final WeddingGuideService weddingGuideService;

    @GetMapping("/api/chat/guides/{category}")
    public ResponseEntity<WeddingGuideResponse> getGuide(@PathVariable String category) {
        return ResponseEntity.ok(weddingGuideService.getGuide(category));
    }
}
