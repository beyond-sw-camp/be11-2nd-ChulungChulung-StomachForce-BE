package com.beyond.StomachForce.allergyInfo.repository;

import com.beyond.StomachForce.allergyInfo.domain.AllergyInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AllergyInfoRepository extends JpaRepository<AllergyInfo,Long> {
}
