package com.beyond.StomachForce.serviceCenter.service;

import com.beyond.StomachForce.User.domain.User;
import com.beyond.StomachForce.User.repository.UserRepository;
import com.beyond.StomachForce.serviceCenter.domain.ServiceAnswer;
import com.beyond.StomachForce.serviceCenter.domain.ServicePost;
import com.beyond.StomachForce.serviceCenter.dtos.*;
import com.beyond.StomachForce.serviceCenter.repository.ServiceAnswerRepository;
import com.beyond.StomachForce.serviceCenter.repository.ServicePostRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ServiceService {

    private final ServicePostRepository servicePostRepository;
    private final ServiceAnswerRepository serviceAnswerRepository;
    private final UserRepository userRepository;

    public ServiceService(ServicePostRepository servicePostRepository, ServiceAnswerRepository serviceAnswerRepository, UserRepository userRepository) {
        this.servicePostRepository = servicePostRepository;
        this.serviceAnswerRepository = serviceAnswerRepository;
        this.userRepository = userRepository;
    }

    public ServicePostResDto createPost(ServicePostCreateReq req) {
        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("사용자가 존재하지 않습니다."));

        ServicePost post = ServicePost.builder()
                .user(user)
                .category(req.getCategory())
                .title(req.getTitle())
                .contents(req.getContents())
                .visibility(req.getVisibility())
                .isAnswered(null) // 초기에는 답변 없음
                .build();

        servicePostRepository.save(post);
        return new ServicePostResDto(post);
    }

    public ServicePostResDto updatePost(Long postId, ServicePostUpdateReq req) {
        ServicePost post = servicePostRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다."));

        if (req.getTitle() != null) post.setTitle(req.getTitle());
        if (req.getContents() != null) post.setContents(req.getContents());
        if (req.getCategory() != null) post.setCategory(req.getCategory());
        if (req.getVisibility() != null) post.setVisibility(req.getVisibility());

        return new ServicePostResDto(post);
    }

    public void deletePost(Long postId) {
        ServicePost post = servicePostRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다."));
        servicePostRepository.delete(post);
    }

//    public ServicePostResDto getPostById(Long postId) {
//        ServicePost post = servicePostRepository.findById(postId)
//                .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다."));
//        return new ServicePostResDto(post);
//    }

    public List<ServicePostResDto> getAllPosts() {
        return servicePostRepository.findAll().stream()
                .map(ServicePostResDto::new)
                .collect(Collectors.toList());
    }

    //answer

    public ServiceAnswerResDto createAnswer(ServiceAnswerCreateReq req) {
        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("사용자가 존재하지 않습니다."));
        ServicePost post = servicePostRepository.findById(req.getPostId())
                .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다."));

        ServiceAnswer answer = ServiceAnswer.builder()
                .servicePost(post)
                .user(user)
                .contents(req.getContents())
                .build();

        serviceAnswerRepository.save(answer);
        return new ServiceAnswerResDto(answer);
    }

    public ServiceAnswerResDto updateAnswer(Long answerId, ServiceAnswerUpdateReq req) {
        ServiceAnswer answer = serviceAnswerRepository.findById(answerId)
                .orElseThrow(() -> new EntityNotFoundException("답변이 존재하지 않습니다."));

        if (req.getContents() != null) answer.setContents(req.getContents());

        return new ServiceAnswerResDto(answer);
    }

    public void deleteAnswer(Long answerId) {
        ServiceAnswer answer = serviceAnswerRepository.findById(answerId)
                .orElseThrow(() -> new EntityNotFoundException("답변이 존재하지 않습니다."));
        serviceAnswerRepository.delete(answer);
    }
}
