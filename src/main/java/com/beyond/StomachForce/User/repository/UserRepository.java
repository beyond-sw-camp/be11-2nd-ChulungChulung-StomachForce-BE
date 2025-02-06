package com.beyond.StomachForce.User.repository;

import com.beyond.StomachForce.User.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByName(String name);
    Optional<User> findByBirth(String birth);
    Optional<User> findByIdentify(String identify);
}
