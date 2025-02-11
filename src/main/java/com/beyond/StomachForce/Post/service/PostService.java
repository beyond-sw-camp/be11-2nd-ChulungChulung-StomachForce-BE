package com.beyond.StomachForce.Post.service;

import com.beyond.StomachForce.Post.dtos.PostUpdateReq;
import com.beyond.StomachForce.User.domain.User;
import com.beyond.StomachForce.Post.domain.Post;
import com.beyond.StomachForce.Post.dtos.PostCreateReq;
import com.beyond.StomachForce.Post.repository.PostRepository;
import com.beyond.StomachForce.User.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }
    public Post save(PostCreateReq postCreateReq) {
        User user = userRepository.findById(postCreateReq.getUserId()).orElseThrow(()->new EntityNotFoundException());
        Post post = postRepository.save(postCreateReq.toEntity(user));
        return post;
    }

    public void updateByIdentify(PostUpdateReq postUpdateReq){
        Post post = postRepository.findById(postUpdateReq.getId()).orElseThrow(()->new EntityNotFoundException("없는 게시글입니다"));
        post.updatePost(postUpdateReq);
    }

    public void delete(Long id){
        Post post = postRepository.findById(id).orElseThrow(()->new EntityNotFoundException("없는 게시글입니다."));
        post.deletePost();
    }

}
