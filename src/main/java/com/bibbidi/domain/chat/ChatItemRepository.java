package com.bibbidi.domain.chat;

import com.bibbidi.domain.wedding.WeddingProfile;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatItemRepository extends JpaRepository<ChatItem, Long> {

    List<ChatItem> findByWeddingProfileOrderByCreatedAtAscIdAsc(WeddingProfile weddingProfile);
}
