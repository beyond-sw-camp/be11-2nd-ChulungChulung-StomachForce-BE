package com.beyond.StomachForce.report.service;

import com.beyond.StomachForce.Common.Auth.JwtTokenProvider;
import com.beyond.StomachForce.User.domain.Enum.Role;
import com.beyond.StomachForce.User.domain.User;
import com.beyond.StomachForce.User.repository.UserRepository;
import com.beyond.StomachForce.report.domain.Report;
import com.beyond.StomachForce.report.domain.ReportAnswer;
import com.beyond.StomachForce.report.domain.ReportPhoto;
import com.beyond.StomachForce.report.dtos.*;
import com.beyond.StomachForce.report.repository.ReportAnswerRepository;
import com.beyond.StomachForce.report.repository.ReportPhotoRepository;
import com.beyond.StomachForce.report.repository.ReportRepository;
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
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReportService {

    private final ReportRepository reportRepository;
    private final ReportAnswerRepository reportAnswerRepository;
    private final UserRepository userRepository;
    private final ReportPhotoRepository reportPhotoRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    private final S3Client s3Client;

    public ReportService(ReportRepository reportRepository, ReportAnswerRepository reportAnswerRepository, UserRepository userRepository, ReportPhotoRepository reportPhotoRepository, S3Client s3Client) {
        this.reportRepository = reportRepository;
        this.reportAnswerRepository = reportAnswerRepository;
        this.userRepository = userRepository;
        this.reportPhotoRepository = reportPhotoRepository;
        this.s3Client = s3Client;
    }

    public ReportResDto createReport(ReportCreateReq req, String userIdentify) {
        // userIdentify(예: 이메일)로 사용자 조회
        User reporter = userRepository.findByIdentify(userIdentify)
                .orElseThrow(() -> new EntityNotFoundException("사용자가 존재하지 않습니다."));
        User reported = userRepository.findByIdentify(userIdentify)
                .orElseThrow(() -> new EntityNotFoundException("사용자가 존재하지 않습니다."));

        // 새로운 게시글 생성
        Report report = Report.builder()
                .reporter(reporter)
                .reported(req.getReportedId())
                .reportClass(req.getReportClass())
                .contents(req.getContents())
                .build();

        Report savedReport = reportRepository.save(report); // 게시글 저장 (postId 생성됨)

        // 🔹 사진 업로드 (각 postId별 폴더 생성)
        List<ReportPhoto> photoList = req.getPhotos().stream()
                .map(photo -> {
                    String photoUrl = uploadImageToS3(photo, savedReport.getId()); // postId를 폴더로 활용!
                    return ReportPhoto.builder()
                            .photo(photoUrl)
                            .report(savedReport)
                            .build();
                })
                .collect(Collectors.toList());

        // 저장된 post에 사진 추가
        savedReport.getReportPhotos().addAll(photoList);
        reportPhotoRepository.saveAll(photoList); // 사진 DB 저장

        return new ReportResDto(savedReport);
    }


    private String uploadImageToS3(MultipartFile image, Long reportId) {
        try {
            byte[] bytes = image.getBytes();
            String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename(); // 🔹 파일명에 타임스탬프 추가

            // S3에 저장할 경로 (각 게시글의 폴더를 생성)
            String s3Key = "reports/" + reportId + "/" + fileName;

            // S3 업로드 요청
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(s3Key)
                    .build();

            // 로컬 파일 경로 생성 (테스트용)
            Path dirPath = Paths.get("C:/Users/Playdata/Desktop/testFolder/reports/" + reportId);
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


    public ReportResDto updateReport(Long reportId, ReportUpdateReq req) {
        // 현재 로그인한 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userIdentify = authentication.getName(); // JWT에서 userIdentify 추출

        // userIdentify로 userId 조회
        User user = userRepository.findByIdentify(userIdentify)
                .orElseThrow(() -> new EntityNotFoundException("사용자가 존재하지 않습니다."));

        // 수정할 게시글 가져오기
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다."));

        // 본인이 작성한 글인지 확인
        if (!report.getReporter().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "해당 게시글을 수정할 권한이 없습니다.");
        }

        // 게시글 정보 업데이트
        if (req.getReportClass() != null) report.setReportClass(req.getReportClass());
        if (req.getContents() != null) report.setContents(req.getContents());

        // 기존 사진 S3 및 DB에서 삭제
        List<ReportPhoto> existingPhotos = report.getReportPhotos();
        for (ReportPhoto photo : existingPhotos) {
            deleteImageFromS3(photo.getPhoto(), reportId); // postId 기반으로 삭제
        }
        reportPhotoRepository.deleteAll(existingPhotos);
        report.getReportPhotos().clear();

        // 새로운 사진 업로드
        if (req.getPhotos() != null && !req.getPhotos().isEmpty()) {
            List<ReportPhoto> newPhotos = req.getPhotos().stream()
                    .map(photo -> {
                        String photoUrl = uploadImageToS3(photo, reportId); // postId 폴더에 저장
                        return ReportPhoto.builder()
                                .report(report)
                                .photo(photoUrl)
                                .build();
                    })
                    .collect(Collectors.toList());

            reportPhotoRepository.saveAll(newPhotos);
            report.getReportPhotos().addAll(newPhotos);
        }

        return new ReportResDto(report);
    }

    public void deleteReport(Long reportId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userIdentify = authentication.getName();

        User user = userRepository.findByIdentify(userIdentify)
                .orElseThrow(() -> new EntityNotFoundException("사용자가 존재하지 않습니다."));

        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다."));

        if (!report.getReporter().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "해당 게시글을 삭제할 권한이 없습니다.");
        }

        // 해당 게시글의 사진들 가져오기
        List<ReportPhoto> photos = report.getReportPhotos();
        for (ReportPhoto photo : photos) {
            deleteImageFromS3(photo.getPhoto(), reportId); // 🔹 postId 폴더 내의 파일만 삭제!
        }
        reportPhotoRepository.deleteAll(photos); // DB에서 삭제

        reportRepository.delete(report);
    }


    // S3에서 이미지 삭제하는 메서드 추가
    private void deleteImageFromS3(String photoUrl, Long reportId) {
        try {
            // URL에서 정확한 키 추출 (폴더 구조 포함)
            String keyPrefix = "reports/" + reportId + "/";
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


    public ReportResDto getReportById(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다."));
        return new ReportResDto(report);
    }

    public List<ReportResDto> getAllPosts() {
        return reportRepository.findAll().stream()
                .map(ReportResDto::new)
                .collect(Collectors.toList());
    }

    //answer

    public ReportAnswerResDto getAnswerByReportId(Long reportId) {
        return reportAnswerRepository.findByReportId(reportId)
                .map(ReportAnswerResDto::new)
                .orElse(new ReportAnswerResDto()); // 답변이 없으면 빈 DTO 반환
    }

    public ReportAnswerResDto createAnswer(ReportAnswerCreateReq req) {
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
        Report report = reportRepository.findById(req.getReportId())
                .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다."));

        // 답변 생성
        ReportAnswer answer = ReportAnswer.builder()
                .report(report)
                .contents(req.getContents())
                .build();

        reportAnswerRepository.save(answer);
        return new ReportAnswerResDto(answer);
    }


    public ReportAnswerResDto updateAnswer(Long answerId, ReportAnswerUpdateReq req) {
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
        ReportAnswer answer = reportAnswerRepository.findById(answerId)
                .orElseThrow(() -> new EntityNotFoundException("답변이 존재하지 않습니다."));

        // 답변 내용 업데이트
        if (req.getContents() != null) {
            answer.setContents(req.getContents());
        }

        return new ReportAnswerResDto(answer);
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
        ReportAnswer answer = reportAnswerRepository.findById(answerId)
                .orElseThrow(() -> new EntityNotFoundException("답변이 존재하지 않습니다."));

        reportAnswerRepository.delete(answer);
    }

}
