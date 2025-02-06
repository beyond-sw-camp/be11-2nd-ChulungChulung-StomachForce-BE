package com.beyond.StomachForce.Post.repository;

import com.beyond.StomachForce.Post.domain.Likes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<Likes,Long> {
}
