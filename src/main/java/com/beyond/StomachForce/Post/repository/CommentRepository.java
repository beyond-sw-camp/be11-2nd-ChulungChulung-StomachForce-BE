package com.beyond.StomachForce.Post.repository;

import com.beyond.StomachForce.Post.domain.Comment;
import com.beyond.StomachForce.Post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Long> {
    List<Comment> findByPostId(Long postId);
}
