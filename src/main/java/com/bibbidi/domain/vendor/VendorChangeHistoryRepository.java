package com.bibbidi.domain.vendor;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VendorChangeHistoryRepository extends JpaRepository<VendorChangeHistory, Long> {

    List<VendorChangeHistory> findByPreviousCardOrNewCardOrderByChangedAtDescIdDesc(
        VendorCard previousCard,
        VendorCard newCard
    );
}
