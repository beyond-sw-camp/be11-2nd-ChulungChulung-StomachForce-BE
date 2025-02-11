package com.beyond.StomachForce.Post.service;

import com.beyond.StomachForce.Post.dtos.LikeRabbitDto;
import com.beyond.StomachForce.Post.dtos.LikeToggleDto;
import com.beyond.StomachForce.Post.dtos.PostUpdateReq;
import com.beyond.StomachForce.User.domain.User;
import com.beyond.StomachForce.Post.domain.Post;
import com.beyond.StomachForce.Post.dtos.PostCreateReq;
import com.beyond.StomachForce.Post.repository.PostRepository;
import com.beyond.StomachForce.User.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeService likeService;
    private final LikeRabbitmqService likeRabbitmqService;

    public PostService(PostRepository postRepository, UserRepository userRepository, LikeService likeService, LikeRabbitmqService likeRabbitmqService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.likeService = likeService;
        this.likeRabbitmqService = likeRabbitmqService;
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

    public Long likes(LikeToggleDto likeToggleDto){
        Long postId = likeToggleDto.getPostId();
        Long userId = likeToggleDto.getUserId();
        likeService.toggleLike(postId, String.valueOf(userId));
        Long updateLike = likeService.getLikeCount(postId);
        LikeRabbitDto likeRabbitDto = LikeRabbitDto.builder().postId(postId).likes(updateLike).build();
        likeRabbitmqService.publish(likeRabbitDto);
        return likeService.getLikeCount(postId);

    }


}
