package com.beyond.StomachForce.User.repository;

import com.beyond.StomachForce.User.domain.Mileage;
import com.beyond.StomachForce.User.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MileageRepository extends JpaRepository<Mileage,Long> {
}
