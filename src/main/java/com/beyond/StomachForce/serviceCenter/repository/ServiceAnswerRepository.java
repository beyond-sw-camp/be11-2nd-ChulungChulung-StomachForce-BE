package com.beyond.StomachForce.serviceCenter.repository;

import com.beyond.StomachForce.serviceCenter.domain.ServiceAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServiceAnswerRepository extends JpaRepository<ServiceAnswer, Long> {
    Optional<ServiceAnswer> findByServicePostId(Long postId);
}
