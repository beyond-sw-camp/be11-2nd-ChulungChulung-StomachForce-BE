package com.beyond.StomachForce.Post.repository;

import com.beyond.StomachForce.Post.domain.Post;
import com.beyond.StomachForce.User.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post,Long> {
    Page<Post> findByUser(User user, Pageable pageable);
}
