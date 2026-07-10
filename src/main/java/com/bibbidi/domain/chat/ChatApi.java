package com.bibbidi.domain.chat;

import com.bibbidi.domain.chat.dto.ChatHistoryResponse;
import com.bibbidi.domain.user.User;
import com.bibbidi.support.auth.Auth;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatApi {

    private final ChatService chatService;

    @GetMapping("/api/chat/history")
    public ResponseEntity<ChatHistoryResponse> getHistory(@Auth User user) {
        return ResponseEntity.ok(chatService.getHistory(user));
    }
}
