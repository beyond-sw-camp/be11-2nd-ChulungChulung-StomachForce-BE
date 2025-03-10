package com.beyond.StomachForce.User.repository;

import com.beyond.StomachForce.User.domain.BlockUser;
import com.beyond.StomachForce.User.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlockingRepository extends JpaRepository<BlockUser,Long> {
    Optional<List<BlockUser>> findByBlocker(User blocker);
    Optional<List<BlockUser>> findByBlocked(User blocked);
    Optional<BlockUser> findByBlockerAndBlocked(User blocker, User blocked);
}
