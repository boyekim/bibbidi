package com.bibbidi.domain.vendor;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VendorEventRepository extends JpaRepository<VendorEvent, Long> {

    long countByVendorCardIn(List<VendorCard> vendorCards);

    List<VendorEvent> findByVendorCardOrderByEventAtAscIdAsc(VendorCard vendorCard);

    List<VendorEvent> findByVendorCardInOrderByEventAtAscIdAsc(List<VendorCard> vendorCards);
}
