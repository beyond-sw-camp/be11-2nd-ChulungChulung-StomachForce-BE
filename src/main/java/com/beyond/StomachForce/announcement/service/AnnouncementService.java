package com.beyond.StomachForce.announcement.service;


import com.beyond.StomachForce.User.domain.User;
import com.beyond.StomachForce.User.repository.UserRepository;
import com.beyond.StomachForce.accouncementImage.domain.AnnouncementImage;
import com.beyond.StomachForce.accouncementImage.repository.AnnouncementImageRepository;
import com.beyond.StomachForce.announcement.domain.AnnounceStatus;
import com.beyond.StomachForce.announcement.domain.Announcement;
import com.beyond.StomachForce.announcement.domain.Type;
import com.beyond.StomachForce.announcement.dtos.*;
import com.beyond.StomachForce.announcement.repository.AnnouncementRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@Service
public class AnnouncementService {
    private final AnnouncementRepository announcementRepository;
    private final AnnouncementImageRepository announcementImageRepository;
    private final S3Client s3Client;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    private final UserRepository userRepository;


    public AnnouncementService(AnnouncementRepository announcementRepository, AnnouncementImageRepository announcementImageRepository, S3Client s3Client, UserRepository userRepository) {
        this.announcementRepository = announcementRepository;
        this.announcementImageRepository = announcementImageRepository;
        this.s3Client = s3Client;
        this.userRepository = userRepository;
    }
    public Announcement createAnnouncement(AnnouncementCreateReq dto) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByIdentify(authentication.getName()).orElseThrow(()-> new EntityNotFoundException("user is not found"));
        LocalDateTime parsedEndDate = null;
        if (dto.getEndDate() != null && !dto.getEndDate().isBlank()) {
            parsedEndDate = LocalDateTime.parse(dto.getEndDate(), DateTimeFormatter.ISO_DATE_TIME);
        }
        Announcement announcement = Announcement.builder()
                .title(dto.getTitle())
                .endTime(parsedEndDate)
                .contents(dto.getContents())
                .user(user)
                .type(Type.valueOf(dto.getType()))
                .build();
        announcementRepository.save(announcement);
        List<AnnouncementImage> imageList = new ArrayList<>();

        if (dto.getImagePaths() != null && !dto.getImagePaths().isEmpty()){
            try{
                for (MultipartFile image : dto.getImagePaths()) {
//
                    byte[] bytes = image.getBytes();
                    String fileName = image.getOriginalFilename();
                    //      먼저 local에 저장
                    Path path = Paths.get("C:/Users/Playdata/Desktop/announcement" , fileName);
                    Files.write(path, bytes, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
                    //      저장을 위한 request 객체(s3 업로드 요청)
                    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(fileName)
                            .build();
                    //      저장 실행(s3업로드)
                    s3Client.putObject(putObjectRequest, RequestBody.fromFile(path));

                    //      저장된 s3url 갖고오기
                    String s3Url = s3Client.utilities().getUrl(a->a.bucket(bucket).key(fileName)).toExternalForm();

//                restaurantPhotos.add(s3Url); 이렇게 하면 안되고 객체 생성해서,,,주입해야함
                    //  레스토랑포토 객체 생성 후에 리스트에 담기
                    AnnouncementImage announcementImage = AnnouncementImage.builder()
                            .imagePath(s3Url)
                            .announcement(announcement)
                            .build();
                    imageList.add(announcementImage);

                }
            }catch (RuntimeException e){
                throw new RemoteException("이미지저장실패");
            }
        }





        announcementImageRepository.saveAll(imageList);
        announcement.setImages(imageList);
        // 4. 이미지 리스트 저장

        return announcement;
    }

    public Announcement updateAnnouncement(AnnouncementUpdateReq dto, Long id) throws IOException {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("없는 게시글 입니다."));

        List<AnnouncementImage> imageList = new ArrayList<>();

        // 기존 이미지 유지 여부 확인
        if (Boolean.parseBoolean(dto.getKeepExistingImages())) {
            // 기존 이미지 유지
            imageList.addAll(announcement.getImages());
        } else {
            // 새 이미지 업로드 처리
            if (dto.getImages() != null && !dto.getImages().isEmpty()) {
                for (MultipartFile image : dto.getImages()) {
                    byte[] bytes = image.getBytes();
                    String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();

                    // 로컬 저장
                    Path path = Paths.get("C:/Users/Playdata/Desktop/announcement", fileName);
                    Files.write(path, bytes, StandardOpenOption.CREATE, StandardOpenOption.WRITE);

                    // S3 업로드
                    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(fileName)
                            .build();

                    s3Client.putObject(putObjectRequest, RequestBody.fromFile(path));

                    String s3Url = s3Client.utilities().getUrl(a -> a.bucket(bucket).key(fileName)).toExternalForm();

                    AnnouncementImage announcementImage = AnnouncementImage.builder()
                            .imagePath(s3Url)
                            .announcement(announcement)
                            .build();
                    imageList.add(announcementImage);
                }
            }
        }

        LocalDateTime parsedEndDate = null;
        if (dto.getEndDate() != null && !dto.getEndDate().isBlank()) {
            try {
                parsedEndDate = LocalDateTime.parse(dto.getEndDate(), DateTimeFormatter.ISO_DATE_TIME);
            } catch (Exception e) {
                System.err.println("❌ LocalDateTime 변환 오류: " + e.getMessage());
            }
        }

        // 기존 이미지 삭제 (새 이미지를 업로드하는 경우에만)
        if (!Boolean.parseBoolean(dto.getKeepExistingImages())) {
            announcementImageRepository.deleteAllByAnnouncement(announcement);
        }

        announcement.updateAnnouncement(dto.getTitle(), dto.getContents(), dto.getStatus(), imageList, parsedEndDate);

        if (!imageList.isEmpty()) {
            announcementImageRepository.saveAll(imageList);
        }

        return announcementRepository.save(announcement);
    }
    public void deleteAnnouncement(Long id) {
        // 1. 공지사항 찾기
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("없는 게시글 입니다."));

        // 2. S3에서 이미지 삭제 (S3에 저장된 이미지가 있다면)
        if (announcement.getImages() != null && !announcement.getImages().isEmpty()) {
            for (AnnouncementImage image : announcement.getImages()) {
                String fileName = image.getImagePath().substring(image.getImagePath().lastIndexOf("/") + 1);

                DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                        .bucket(bucket)
                        .key(fileName)
                        .build();
                s3Client.deleteObject(deleteObjectRequest);
            }
        }

        // 3. DB에서 공지사항 삭제 (Cascade 설정으로 이미지도 함께 삭제됨)
        announcementRepository.delete(announcement);
    }
    public List<AnnouncementListRes> getAnnouncements() {
        return announcementRepository.findAll().stream()
                .map(announcement -> AnnouncementListRes.builder()
                        .id(announcement.getId())
                        .title(announcement.getTitle())
                        .createdDate(LocalDate.from(announcement.getCreatedTime())) // 엔티티에서 생성 날짜 가져오기
                        .announcementType(announcement.getType().name()) // Enum 타입을 문자열로 변환
                        .endDate(Optional.ofNullable(announcement.getEndTime())
                                .map(LocalDate::from)
                                .orElse(null))
                        .images(announcement.getImages())
                        .build())
                .collect(Collectors.toList());
    }

    public List<EventBannerRes> getOngoingEvents() {
        LocalDateTime now = LocalDateTime.now(); // 현재 날짜 및 시간 가져오기

        List<Announcement> eventAnnouncements = announcementRepository.findByTypeAndStatus(Type.EVENT, AnnounceStatus.ON)
                .stream()
                .filter(event -> event.getEndTime() == null || event.getEndTime().isAfter(now)) // ✅ 현재 시간 이후인 경우만 필터링
                .collect(Collectors.toList());

        // 🔍 필터링된 이벤트 목록 출력 (디버깅용)
        System.out.println("✅ 필터링된 이벤트 개수: " + eventAnnouncements.size());
        for (Announcement event : eventAnnouncements) {
            System.out.println("📌 진행 중인 이벤트: " + event.getTitle() + " | 종료 날짜: " + event.getEndTime());
        }

        return eventAnnouncements.stream().map(event -> EventBannerRes.builder()
                .eventId(event.getId())
                .eventTitle(event.getTitle())
                .eventImage(event.getImages().isEmpty() ? null : event.getImages().get(0).getImagePath()) // 첫 번째 이미지 기준
                .build()
        ).collect(Collectors.toList());
    }
    public AnnouncementDetailRes getAnnouncementDetail(Long id) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID의 공지사항이 존재하지 않습니다."));

        return AnnouncementDetailRes.builder()
                .id(announcement.getId())
                .title(announcement.getTitle())
                .createdDate(LocalDate.from(announcement.getCreatedTime())) // 생성 날짜
                .endDate(Optional.ofNullable(announcement.getEndTime())
                .map(LocalDate::from)
                .orElse(null))
                .announcementType(announcement.getType().name()) // 공지 or 이벤트
                .contents(announcement.getContents()) // 본문 내용
                .images(announcement.getImages()) // 이미지 리스트
                .build();
    }
}
