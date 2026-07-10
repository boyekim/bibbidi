package com.bibbidi.domain.chat;

import com.bibbidi.domain.chat.dto.ChatHistoryResponse;
import com.bibbidi.domain.chat.dto.ChatItemResponse;
import com.bibbidi.domain.user.User;
import com.bibbidi.domain.vendor.DemoWeddingDataService;
import com.bibbidi.domain.wedding.WeddingProfile;
import com.bibbidi.support.exception.InternalServerException;
import com.bibbidi.support.exception.errors.CommonErrors;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatService {

    private final DemoWeddingDataService demoWeddingDataService;
    private final ChatItemRepository chatItemRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public ChatHistoryResponse getHistory(User user) {
        WeddingProfile weddingProfile = demoWeddingDataService.ensureDefaultData(user);
        List<ChatItemResponse> items = chatItemRepository.findByWeddingProfileOrderByCreatedAtAscIdAsc(weddingProfile)
            .stream()
            .map(this::toResponse)
            .toList();

        return new ChatHistoryResponse(items);
    }

    @Transactional
    public void recordAssistantText(WeddingProfile weddingProfile, String text) {
        chatItemRepository.save(new ChatItem(weddingProfile, "assistant", "text", payloadJson(new TextPayload(text))));
    }

    private ChatItemResponse toResponse(ChatItem chatItem) {
        return ChatItemResponse.of(chatItem, readPayload(chatItem));
    }

    private JsonNode readPayload(ChatItem chatItem) {
        try {
            return objectMapper.readTree(chatItem.getPayloadJson());
        } catch (JacksonException exception) {
            throw new InternalServerException(CommonErrors.INTERNAL_SERVER_ERROR);
        }
    }

    private String payloadJson(Object payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JacksonException exception) {
            throw new InternalServerException(CommonErrors.INTERNAL_SERVER_ERROR);
        }
    }

    private record TextPayload(String text) {
    }
}
