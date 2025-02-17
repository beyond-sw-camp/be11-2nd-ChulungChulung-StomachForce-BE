package com.beyond.StomachForce.serviceCenter.controller;

import com.beyond.StomachForce.Post.service.PostService;
import com.beyond.StomachForce.serviceCenter.dtos.*;
import com.beyond.StomachForce.serviceCenter.service.ServiceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/service")
public class ServiceController {
    private final ServiceService serviceService;

    public ServiceController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    @PostMapping("/post/create")
    public ResponseEntity<ServicePostResDto> createPost(@RequestBody ServicePostCreateReq req) {
        return ResponseEntity.ok(serviceService.createPost(req));
    }

    @PatchMapping("/post/update/{postId}")
    public ResponseEntity<ServicePostResDto> updatePost(@PathVariable Long postId, @RequestBody ServicePostUpdateReq req) {
        return ResponseEntity.ok(serviceService.updatePost(postId, req));
    }

    @DeleteMapping("/post/delete/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable Long postId) {
        serviceService.deletePost(postId);
        return ResponseEntity.ok("게시글이 삭제되었습니다.");
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<ServicePostResDto> getPostById(@PathVariable Long postId) {
        return ResponseEntity.ok(serviceService.getPostById(postId));
    }

    @GetMapping("/list")
    public ResponseEntity<List<ServicePostResDto>> getAllPosts() {
        return ResponseEntity.ok(serviceService.getAllPosts());
    }

    //answer

    @PostMapping("/answer")
    public ResponseEntity<ServiceAnswerResDto> createAnswer(@RequestBody ServiceAnswerCreateReq req) {
        return ResponseEntity.ok(serviceService.createAnswer(req));
    }

    @PatchMapping("/answer/update/{answerId}")
    public ResponseEntity<ServiceAnswerResDto> updateAnswer(@PathVariable Long answerId, @RequestBody ServiceAnswerUpdateReq req) {
        return ResponseEntity.ok(serviceService.updateAnswer(answerId, req));
    }

    @DeleteMapping("/answer/delete/{answerId}")
    public ResponseEntity<String> deleteAnswer(@PathVariable Long answerId) {
        serviceService.deleteAnswer(answerId);
        return ResponseEntity.ok("답변이 삭제되었습니다.");
    }
}
