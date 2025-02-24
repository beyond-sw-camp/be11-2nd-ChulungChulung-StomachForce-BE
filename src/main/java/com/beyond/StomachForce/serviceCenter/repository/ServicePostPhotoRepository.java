package com.beyond.StomachForce.serviceCenter.repository;

import com.beyond.StomachForce.serviceCenter.domain.ServicePostPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServicePostPhotoRepository extends JpaRepository<ServicePostPhoto,Long> {
}
