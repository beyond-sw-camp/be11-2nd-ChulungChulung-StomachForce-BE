package com.beyond.StomachForce.Post.service;

import com.beyond.StomachForce.Post.domain.Comment;
import com.beyond.StomachForce.Post.dtos.*;
import com.beyond.StomachForce.Post.repository.CommentRepository;
import com.beyond.StomachForce.User.domain.User;
import com.beyond.StomachForce.Post.domain.Post;
import com.beyond.StomachForce.Post.repository.PostRepository;
import com.beyond.StomachForce.User.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.stream.Collectors;

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
        String identify = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByIdentify(identify).orElseThrow(()->new EntityNotFoundException("없는 회원입니다."));
        String contents = postCreateReq.getContents();
        Post tempPost = postCreateReq.toEntity(user);
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
//            PostPhotos postPhotos = PostPhotos.builder().postPhoto(s3Url).post(post).build();
            post.updatePostImagePath(s3Url);
        }
        return post;
    }

    public void updateByIdentify(PostUpdateReq postUpdateReq){
        Post post = postRepository.findById(postUpdateReq.getId()).orElseThrow(()->new EntityNotFoundException("없는 게시글입니다"));
        post.updatePost(postUpdateReq);
    }

    public void delete(Long postId){
        Post post = postRepository.findById(postId).orElseThrow(()->new EntityNotFoundException("없는 게시글입니다."));
        post.deletePost();
    }

    public LikeResDto getLikes(Long postId){
        String identify = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByIdentify(identify).orElseThrow(()->new EntityNotFoundException("없는 회원입니다."));
        Long userId = user.getId();
        Long Like = likeService.getLikeCount(postId);
        boolean isLiked = likeService.isUserLikedPost(postId, userId);
        return LikeResDto.builder()
                .postId(postId)
                .likes(Like)
                .isLiked(isLiked)
                .build();
    }

    public void postLikes(Long postId){
        String identify = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByIdentify(identify).orElseThrow(()->new EntityNotFoundException("없는 회원입니다."));
        Long userId = user.getId();
        likeService.toggleLike(postId, String.valueOf(userId));
        Long updateLike = likeService.getLikeCount(postId);
        boolean isLiked = likeService.isUserLikedPost(postId, userId);
        LikeRabbitDto likeRabbitDto = LikeRabbitDto.builder().postId(postId).likes(updateLike).build();
        likeRabbitmqService.publish(likeRabbitDto);
    }

    public Comment comments(Long postId,CommentCreateDto commentCreateDto){
        String identify = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByIdentify(identify).orElseThrow(()->new EntityNotFoundException("없는 회원입니다."));
        Post post = postRepository.findById(postId).orElseThrow(()->new EntityNotFoundException("없는 게시글입니다."));
        String contents = commentCreateDto.getContents();
        Comment comment = Comment.builder().contents(contents).post(post).userNickname(user.getNickName()).userProfile(user.getProfilePhoto()).build();
        return commentRepository.save(comment);
    }

    public List<CommentListRes> getComments(Long postId){
        List<Comment> comments = commentRepository.findByPostId(postId);

        return comments.stream()
                .map(c -> CommentListRes.builder()
                        .id(c.getId())
                        .contents(c.getContents())
                        .userNickname(c.getUserNickname())
                        .userProfile(c.getUserProfile())
                        .build())
                .collect(Collectors.toList());
    }

    public PostDetailRes postDetail(Long postId){
        Post post = postRepository.findById(postId).orElseThrow(()->new EntityNotFoundException("없는 게시글입니다."));
        PostDetailRes postDetailRes = post.postDetails(likeService.getLikeCount(postId));
        return postDetailRes;
    }

}
