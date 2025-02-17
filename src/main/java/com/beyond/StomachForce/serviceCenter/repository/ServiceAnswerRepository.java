package com.beyond.StomachForce.serviceCenter.repository;

import com.beyond.StomachForce.serviceCenter.domain.ServiceAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceAnswerRepository extends JpaRepository<ServiceAnswer, Long> {
}
