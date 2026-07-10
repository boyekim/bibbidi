package com.bibbidi.domain.vendor;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentScheduleRepository extends JpaRepository<PaymentSchedule, Long> {

    long countByVendorCardIn(List<VendorCard> vendorCards);

    List<PaymentSchedule> findByVendorCardOrderByDueDateAscIdAsc(VendorCard vendorCard);

    List<PaymentSchedule> findByVendorCardInOrderByDueDateAscIdAsc(List<VendorCard> vendorCards);

    Optional<PaymentSchedule> findFirstByVendorCardAndLabelAndPaidFalseOrderByIdAsc(
        VendorCard vendorCard,
        String label
    );
}
