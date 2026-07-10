package com.bibbidi.domain.chat;

import com.bibbidi.domain.chat.dto.ChatHistoryResponse;
import com.bibbidi.domain.chat.dto.ChatMessagesRequest;
import com.bibbidi.domain.user.User;
import com.bibbidi.support.auth.Auth;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatApi {

    private final ChatService chatService;

    @GetMapping("/api/chat/history")
    public ResponseEntity<ChatHistoryResponse> getHistory(@Auth User user) {
        return ResponseEntity.ok(chatService.getHistory(user));
    }

    @PostMapping("/api/chat/messages")
    public ResponseEntity<Void> recordMessages(
        @Auth User user,
        @Valid @RequestBody ChatMessagesRequest request
    ) {
        chatService.recordMessages(user, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
