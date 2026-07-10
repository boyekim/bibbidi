package com.bibbidi.domain.vendor;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VendorEventRepository extends JpaRepository<VendorEvent, Long> {

    long countByVendorCardIn(List<VendorCard> vendorCards);

    List<VendorEvent> findByVendorCardOrderByEventAtAscIdAsc(VendorCard vendorCard);

    List<VendorEvent> findByVendorCardInOrderByEventAtAscIdAsc(List<VendorCard> vendorCards);

    boolean existsByVendorCardAndTitleAndEventAt(
        VendorCard vendorCard,
        String title,
        LocalDateTime eventAt
    );
}
