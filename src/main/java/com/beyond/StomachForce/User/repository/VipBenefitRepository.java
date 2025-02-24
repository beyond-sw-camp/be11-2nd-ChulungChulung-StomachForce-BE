package com.beyond.StomachForce.User.repository;

import com.beyond.StomachForce.User.domain.Enum.VipGrade;
import com.beyond.StomachForce.User.domain.VipBenefit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VipBenefitRepository extends JpaRepository<VipBenefit,Long> {
    Page<VipBenefit> findByVipGrade(VipGrade vipGrade, Pageable pageable);
}
