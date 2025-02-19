package com.beyond.StomachForce.Post.controller;

import com.beyond.StomachForce.Common.domain.BaseTimeEntity;
import com.beyond.StomachForce.Common.dtos.StatusCode;
import com.beyond.StomachForce.Post.domain.Comment;
import com.beyond.StomachForce.Post.dtos.*;
import com.beyond.StomachForce.Post.service.LikeService;
import com.beyond.StomachForce.Post.service.PostService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.beyond.StomachForce.Post.domain.Post;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/post")
public class PostController extends BaseTimeEntity {
    private final PostService postService;
    private final LikeService likeService;

    public PostController(PostService postService, LikeService likeService) {
        this.postService = postService;
        this.likeService = likeService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> postCreatePost(@Valid  PostCreateReq postCreateReq) throws IOException {
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
                "게시글 삭제가 완료되었습니다.","ok"),HttpStatus.OK);
    }

    @PostMapping("/like/{postId}")
    public ResponseEntity<?> like(@PathVariable Long postId){
        postService.likes(postId);
        return new ResponseEntity<>(new StatusCode(HttpStatus.OK.value(),
                "좋아요","ok"),HttpStatus.OK);
    }
    @PostMapping("/comment/{postId}")
    public ResponseEntity<?> comment(@PathVariable Long postId, @Valid CommentCreateDto commentCreateDto){
        postService.comments(postId,commentCreateDto);
        return new ResponseEntity<>(new StatusCode(HttpStatus.OK.value(),
                "댓글작성이 완료되었습니다.","ok"),HttpStatus.OK);
    }

    @GetMapping("getComments/{postId}")
    public ResponseEntity<?> getComments(@PathVariable Long postId){
        List<CommentListRes> commentList = postService.getComments(postId);
        return new ResponseEntity<>(commentList,HttpStatus.OK);
    }

    @GetMapping("/postDetail/{postId}")
    public ResponseEntity<?> postDetail(@PathVariable Long postId){
        PostDetailRes response = postService.postDetail(postId);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}
