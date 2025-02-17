package com.beyond.StomachForce.serviceCenter.repository;

import com.beyond.StomachForce.serviceCenter.domain.ServicePost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServicePostRepository extends JpaRepository<ServicePost, Long> {
}
