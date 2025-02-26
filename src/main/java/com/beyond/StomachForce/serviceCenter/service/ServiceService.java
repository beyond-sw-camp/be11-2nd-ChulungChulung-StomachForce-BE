package com.beyond.StomachForce.serviceCenter.service;

import com.beyond.StomachForce.Common.Auth.JwtTokenProvider;
import com.beyond.StomachForce.User.domain.Enum.Role;
import com.beyond.StomachForce.User.domain.User;
import com.beyond.StomachForce.User.repository.UserRepository;
import com.beyond.StomachForce.serviceCenter.domain.ServiceAnswer;
import com.beyond.StomachForce.serviceCenter.domain.ServicePost;
import com.beyond.StomachForce.serviceCenter.domain.ServicePostPhoto;
import com.beyond.StomachForce.serviceCenter.dtos.*;
import com.beyond.StomachForce.serviceCenter.repository.ServiceAnswerRepository;
import com.beyond.StomachForce.serviceCenter.repository.ServicePostPhotoRepository;
import com.beyond.StomachForce.serviceCenter.repository.ServicePostRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ServiceService {

    private final ServicePostRepository servicePostRepository;
    private final ServiceAnswerRepository serviceAnswerRepository;
    private final UserRepository userRepository;
    private final ServicePostPhotoRepository servicePostPhotoRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    private final S3Client s3Client;

    public ServiceService(ServicePostRepository servicePostRepository, ServiceAnswerRepository serviceAnswerRepository, UserRepository userRepository, ServicePostPhotoRepository servicePostPhotoRepository, JwtTokenProvider jwtTokenProvider, S3Client s3Client) {
        this.servicePostRepository = servicePostRepository;
        this.serviceAnswerRepository = serviceAnswerRepository;
        this.userRepository = userRepository;
        this.servicePostPhotoRepository = servicePostPhotoRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.s3Client = s3Client;
    }

    public ServicePostResDto createPost(ServicePostCreateReq req, String userIdentify) {
        // userIdentify(예: 이메일)로 사용자 조회
        User user = userRepository.findByIdentify(userIdentify)
                .orElseThrow(() -> new EntityNotFoundException("사용자가 존재하지 않습니다."));

        // 새로운 게시글 생성
        ServicePost post = ServicePost.builder()
                .user(user)
                .category(req.getCategory())
                .title(req.getTitle())
                .contents(req.getContents())
                .visibility(req.getVisibility())
                .build();

        ServicePost savedPost = servicePostRepository.save(post); // 게시글 저장 (postId 생성됨)

        // 사진이 있는 경우에만 사진 업로드 처리
        if (req.getPhotos() != null && !req.getPhotos().isEmpty()) {
            List<ServicePostPhoto> photoList = req.getPhotos().stream()
                    .map(photo -> {
                        String photoUrl = uploadImageToS3(photo, savedPost.getId());
                        return ServicePostPhoto.builder()
                                .photo(photoUrl)
                                .servicePost(savedPost)
                                .build();
                    })
                    .collect(Collectors.toList());

            // 저장된 post에 사진 추가
            savedPost.getServicePostPhotos().addAll(photoList);
            servicePostPhotoRepository.saveAll(photoList);
        }

        return new ServicePostResDto(savedPost);
    }


    private String uploadImageToS3(MultipartFile image, Long postId) {
        try {
            byte[] bytes = image.getBytes();
            String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename(); // 파일명에 타임스탬프 추가

            // S3에 저장할 경로 (각 게시글의 폴더를 생성)
            String s3Key = "service_posts/" + postId + "/" + fileName;

            // S3 업로드 요청
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(s3Key)
                    .build();

            // 로컬 파일 경로 생성 (테스트용)
            Path dirPath = Paths.get("C:/Users/Playdata/Desktop/testFolder/service_posts/" + postId);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath); // 폴더가 없으면 생성
            }
            Path filePath = dirPath.resolve(fileName);
            Files.write(filePath, bytes, StandardOpenOption.CREATE, StandardOpenOption.WRITE);

            // S3에 업로드 실행
            s3Client.putObject(putObjectRequest, RequestBody.fromFile(filePath.toFile()));

            // 업로드된 S3 URL 반환
            return s3Client.utilities().getUrl(a -> a.bucket(bucket).key(s3Key)).toExternalForm();
        } catch (IOException e) {
            throw new RuntimeException("이미지 업로드 실패", e);
        }
    }


    public ServicePostResDto updatePost(Long postId, ServicePostUpdateReq req) {
        // 현재 로그인한 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userIdentify = authentication.getName();

        // userIdentify로 userId 조회
        User user = userRepository.findByIdentify(userIdentify)
                .orElseThrow(() -> new EntityNotFoundException("사용자가 존재하지 않습니다."));

        // 수정할 게시글 가져오기
        ServicePost post = servicePostRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다."));

        // 본인이 작성한 글인지 확인
        if (!post.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "해당 게시글을 수정할 권한이 없습니다.");
        }

        // 게시글 정보 업데이트
        if (req.getTitle() != null) post.setTitle(req.getTitle());
        if (req.getContents() != null) post.setContents(req.getContents());
        if (req.getCategory() != null) post.setCategory(req.getCategory());
        if (req.getVisibility() != null) post.setVisibility(req.getVisibility());

        // 새로운 사진이 있는 경우에만 사진 관련 처리 수행
        if (req.getPhotos() != null && !req.getPhotos().isEmpty()) {

            // 기존 사진 S3 및 DB에서 삭제
            List<ServicePostPhoto> existingPhotos = post.getServicePostPhotos();
            for (ServicePostPhoto photo : existingPhotos) {
                deleteImageFromS3(photo.getPhoto(), postId);
            }
            servicePostPhotoRepository.deleteAll(existingPhotos);
            post.getServicePostPhotos().clear();

            // 새로운 사진 업로드
            List<ServicePostPhoto> newPhotos = req.getPhotos().stream()
                    .map(photo -> {
                        String photoUrl = uploadImageToS3(photo, postId);
                        return ServicePostPhoto.builder()
                                .servicePost(post)
                                .photo(photoUrl)
                                .build();
                    })
                    .collect(Collectors.toList());

            servicePostPhotoRepository.saveAll(newPhotos);
            post.getServicePostPhotos().addAll(newPhotos);
        } else {
            // 새로운 사진이 없는 경우 기존 사진 유지
            // 아무 작업도 하지 않음
        }

        ServicePost savedPost = servicePostRepository.save(post);

        return new ServicePostResDto(savedPost);
    }

    public void deletePost(Long postId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userIdentify = authentication.getName();

        User user = userRepository.findByIdentify(userIdentify)
                .orElseThrow(() -> new EntityNotFoundException("사용자가 존재하지 않습니다."));

        ServicePost post = servicePostRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다."));

        if (!post.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "해당 게시글을 삭제할 권한이 없습니다.");
        }

        // 해당 게시글의 사진들 가져오기
        List<ServicePostPhoto> photos = post.getServicePostPhotos();
        for (ServicePostPhoto photo : photos) {
            deleteImageFromS3(photo.getPhoto(), postId); // 🔹 postId 폴더 내의 파일만 삭제!
        }
        servicePostPhotoRepository.deleteAll(photos); // DB에서 삭제

        servicePostRepository.delete(post);
    }


    // S3에서 이미지 삭제하는 메서드 추가
    private void deleteImageFromS3(String photoUrl, Long postId) {
        try {
            // URL에서 정확한 키 추출 (폴더 구조 포함)
            String keyPrefix = "service_posts/" + postId + "/";
            String key = photoUrl.substring(photoUrl.indexOf(keyPrefix) + keyPrefix.length());

            // S3 객체 삭제 요청
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(keyPrefix + key) // postId 폴더 안의 파일만 삭제!
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
        } catch (Exception e) {
            throw new RuntimeException("S3 이미지 삭제 실패", e);
        }
    }


    public ServicePostResDto getPostById(Long postId) {
        ServicePost post = servicePostRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다."));
        return new ServicePostResDto(post);
    }

    public List<ServicePostResDto> getAllPosts() {
        return servicePostRepository.findAll().stream()
                .map(ServicePostResDto::new)
                .collect(Collectors.toList());
    }

    //answer

    public ServiceAnswerResDto getAnswerByPostId(Long postId) {
        return serviceAnswerRepository.findByServicePostId(postId)
                .map(ServiceAnswerResDto::new)
                .orElse(new ServiceAnswerResDto()); // 답변이 없으면 빈 DTO 반환
    }

    public ServiceAnswerResDto createAnswer(ServiceAnswerCreateReq req) {
        // 현재 로그인한 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userIdentify = authentication.getName(); // JWT에서 userIdentify(identify 값) 추출

        // userIdentify로 user 정보 조회
        User user = userRepository.findByIdentify(userIdentify)
                .orElseThrow(() -> new EntityNotFoundException("사용자가 존재하지 않습니다."));

        // 관리자 권한 확인
        if (!user.getRole().equals(Role.ADMIN)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "관리자만 답변을 등록할 수 있습니다.");
        }

        // 게시글 존재 여부 확인
        ServicePost post = servicePostRepository.findById(req.getPostId())
                .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다."));

        // 답변 생성
        ServiceAnswer answer = ServiceAnswer.builder()
                .servicePost(post)
                .contents(req.getContents())
                .build();

        serviceAnswerRepository.save(answer);
        return new ServiceAnswerResDto(answer);
    }


    public ServiceAnswerResDto updateAnswer(Long answerId, ServiceAnswerUpdateReq req) {
        // 현재 로그인한 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userIdentify = authentication.getName();

        // userIdentify로 user 정보 조회
        User user = userRepository.findByIdentify(userIdentify)
                .orElseThrow(() -> new EntityNotFoundException("사용자가 존재하지 않습니다."));

        // 관리자 권한 확인
        if (!user.getRole().equals(Role.ADMIN)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "관리자만 답변을 수정할 수 있습니다.");
        }

        // 수정할 답변 조회
        ServiceAnswer answer = serviceAnswerRepository.findById(answerId)
                .orElseThrow(() -> new EntityNotFoundException("답변이 존재하지 않습니다."));

        // 답변 내용 업데이트
        if (req.getContents() != null) {
            answer.setContents(req.getContents());
        }

        return new ServiceAnswerResDto(answer);
    }


    public void deleteAnswer(Long answerId) {
        // 현재 로그인한 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userIdentify = authentication.getName();

        // userIdentify로 user 정보 조회
        User user = userRepository.findByIdentify(userIdentify)
                .orElseThrow(() -> new EntityNotFoundException("사용자가 존재하지 않습니다."));

        // 관리자 권한 확인
        if (!user.getRole().equals(Role.ADMIN)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "관리자만 답변을 삭제할 수 있습니다.");
        }

        // 삭제할 답변 조회
        ServiceAnswer answer = serviceAnswerRepository.findById(answerId)
                .orElseThrow(() -> new EntityNotFoundException("답변이 존재하지 않습니다."));

        serviceAnswerRepository.delete(answer);
    }

}
