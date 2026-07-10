package com.bibbidi.domain.vendor;

import com.bibbidi.domain.wedding.WeddingProfile;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VendorCardRepository extends JpaRepository<VendorCard, Long> {

    List<VendorCard> findByWeddingProfileOrderByIdAsc(WeddingProfile weddingProfile);
}
