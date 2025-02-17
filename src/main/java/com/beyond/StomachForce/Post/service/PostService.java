package com.beyond.StomachForce.Post.service;

import com.beyond.StomachForce.Post.domain.Comment;
import com.beyond.StomachForce.Post.domain.PostPhotos;
import com.beyond.StomachForce.Post.domain.Tag;
import com.beyond.StomachForce.Post.dtos.*;
import com.beyond.StomachForce.Post.repository.CommentRepository;
import com.beyond.StomachForce.User.domain.User;
import com.beyond.StomachForce.Post.domain.Post;
import com.beyond.StomachForce.Post.repository.PostRepository;
import com.beyond.StomachForce.User.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

@Service
@Transactional
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeService likeService;
    private final LikeRabbitmqService likeRabbitmqService;
    private final CommentRepository commentRepository;
    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public PostService(PostRepository postRepository, UserRepository userRepository, LikeService likeService, LikeRabbitmqService likeRabbitmqService, CommentRepository commentRepository, S3Client s3Client) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.likeService = likeService;
        this.likeRabbitmqService = likeRabbitmqService;
        this.commentRepository = commentRepository;
        this.s3Client = s3Client;
    }
    public Post save(PostCreateReq postCreateReq) throws IOException {
        User user = userRepository.findById(postCreateReq.getUserId()).orElseThrow(()->new EntityNotFoundException());
        String contents = postCreateReq.getContents();
        Post tempPost = postCreateReq.toEntity(user);
        List<String> tags = postCreateReq.getTags();
        for(String t : tags){
            Tag tag = Tag.builder().post(tempPost).tagName(t).build();
            tempPost.getTags().add(tag);
        }
        Post post = postRepository.save(tempPost);
        List<MultipartFile> images = postCreateReq.getPostPhotos();
        for(int i=0; i<images.size();i++){
            byte[] bytes = images.get(i).getBytes();
            String fileName = user.getId()+"_"+ images.get(i).getOriginalFilename();
            Path path = Paths.get("C:/Users/Playdata/Desktop/tmp/",fileName);
            Files.write(path,bytes, StandardOpenOption.CREATE,StandardOpenOption.WRITE);
            PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(bucket).key(fileName).build();
            s3Client.putObject(putObjectRequest, RequestBody.fromFile(path));
            String s3Url = s3Client.utilities().getUrl(a->a.bucket(bucket).key(fileName)).toExternalForm();
            PostPhotos postPhotos = PostPhotos.builder().postPhoto(s3Url).post(post).build();
            post.updatePostImagePath(postPhotos);
        }
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

    public Comment comments(Long postId,CommentCreateDto commentCreateDto){
        String identify = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByIdentify(identify).orElseThrow(()->new EntityNotFoundException("없는 회원입니다."));
        String contents = commentCreateDto.getContents();
        Comment comment = Comment.builder().contents(contents).userId(user.getId()).build();
        return commentRepository.save(comment);
    }

}
