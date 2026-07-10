package com.bibbidi.domain.wedding;

import com.bibbidi.domain.user.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeddingProfileRepository extends JpaRepository<WeddingProfile, Long> {

    Optional<WeddingProfile> findFirstByOwnerOrderByIdAsc(User owner);
}
