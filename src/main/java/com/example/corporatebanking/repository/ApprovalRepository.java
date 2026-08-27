package com.example.corporatebanking.repository;

import com.example.corporatebanking.entity.Approval;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApprovalRepository extends JpaRepository<Approval, Long> {
}
