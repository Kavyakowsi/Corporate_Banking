package com.example.corporatebanking.repository;

import com.example.corporatebanking.entity.FundTransfer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FundTransferRepository extends JpaRepository<FundTransfer, Long> {
    Optional<FundTransfer> findByTransactionRef(String transactionRef);
}
