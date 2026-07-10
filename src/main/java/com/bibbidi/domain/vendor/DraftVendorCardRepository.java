package com.bibbidi.domain.vendor;

import com.bibbidi.domain.wedding.WeddingProfile;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DraftVendorCardRepository extends JpaRepository<DraftVendorCard, Long> {

    Optional<DraftVendorCard> findByIdAndWeddingProfile(Long id, WeddingProfile weddingProfile);
}
