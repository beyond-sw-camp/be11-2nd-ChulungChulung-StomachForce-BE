package com.beyond.StomachForce.menu.repository;

import com.beyond.StomachForce.menu.domain.AllergyInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AllergyInfoRepository extends JpaRepository<AllergyInfo,Long> {
}
