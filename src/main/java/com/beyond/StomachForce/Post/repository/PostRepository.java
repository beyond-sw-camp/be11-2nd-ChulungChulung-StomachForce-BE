package com.beyond.StomachForce.Post.repository;

import com.beyond.StomachForce.Post.domain.Enum.PostStatus;
import com.beyond.StomachForce.Post.domain.Post;
import com.beyond.StomachForce.User.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post,Long> {
    Page<Post> findByUserAndPostStatus(User user, PostStatus postStatus, Pageable pageable);
    Page<Post> findByPostStatus(PostStatus postStatus, Pageable pageable);
}
