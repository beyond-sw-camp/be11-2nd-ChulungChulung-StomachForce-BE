package com.beyond.StomachForce.Post.controller;

import com.beyond.StomachForce.Common.domain.BaseTimeEntity;
import com.beyond.StomachForce.Common.dtos.StatusCode;
import com.beyond.StomachForce.Post.dtos.PostCreateReq;
import com.beyond.StomachForce.Post.dtos.PostUpdateReq;
import com.beyond.StomachForce.Post.service.PostService;
import com.beyond.StomachForce.User.dtos.UserUpdateReq;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.beyond.StomachForce.Post.domain.Post;

@RestController
@RequestMapping("/post")
public class PostController extends BaseTimeEntity {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> postCreatePost(@Valid @RequestBody PostCreateReq postCreateReq){
        Post post = postService.save(postCreateReq);
        return new ResponseEntity<>(new StatusCode(HttpStatus.CREATED.value(),
                "게시글 작성이 완료되었습니다",post.getId()),HttpStatus.CREATED);
    }

    @PatchMapping("/update")
    public ResponseEntity<?> postUpdate(@Valid @RequestBody PostUpdateReq postUpdateReq){
        postService.updateByIdentify(postUpdateReq);
        return new ResponseEntity<>(new StatusCode(HttpStatus.OK.value(),
                "게시글이 수정되었습니다.","ok"),HttpStatus.OK);
    }

    @PatchMapping("/delete")
    public ResponseEntity<?> delete(@Valid Long id){
        postService.delete(id);
        return new ResponseEntity<>(new StatusCode(HttpStatus.OK.value(),
                "회원탈퇴가 완료되었습니다.","ok"),HttpStatus.OK);
    }

}
