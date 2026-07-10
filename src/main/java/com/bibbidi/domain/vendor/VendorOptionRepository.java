package com.bibbidi.domain.vendor;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VendorOptionRepository extends JpaRepository<VendorOption, Long> {

    long countByVendorCardIn(List<VendorCard> vendorCards);

    List<VendorOption> findByVendorCardOrderByIdAsc(VendorCard vendorCard);

    List<VendorOption> findByVendorCardInOrderByIdAsc(List<VendorCard> vendorCards);
}
