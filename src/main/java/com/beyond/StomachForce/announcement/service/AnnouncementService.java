package com.beyond.StomachForce.announcement.service;


import com.beyond.StomachForce.User.domain.User;
import com.beyond.StomachForce.User.repository.UserRepository;
import com.beyond.StomachForce.accouncementImage.domain.AnnouncementImage;
import com.beyond.StomachForce.accouncementImage.repository.AnnouncementImageRepository;
import com.beyond.StomachForce.announcement.domain.Announcement;
import com.beyond.StomachForce.announcement.dtos.AnnouncementCreateReq;
import com.beyond.StomachForce.announcement.dtos.AnnouncementListRes;
import com.beyond.StomachForce.announcement.dtos.AnnouncementUpdateReq;
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
import java.util.ArrayList;
import java.util.List;
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

        Announcement announcement = Announcement.builder()
                .title(dto.getTitle())
                .contents(dto.getContents())
                .user(user)
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
        Announcement announcement = announcementRepository.findById(id).orElseThrow(()->new EntityNotFoundException("없는 게시글 입니다."));
        // 2. 새로운 이미지 리스트 생성 (S3 업로드 후 URL 리스트 생성)
        List<AnnouncementImage> newImageList = new ArrayList<>();
        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            for (MultipartFile image : dto.getImages()) {
                byte[] bytes = image.getBytes();
                String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename(); // 중복 방지

                // (1) 먼저 로컬에 저장
                Path path = Paths.get("C:/Users/Playdata/Desktop/announcement", fileName);
                Files.write(path, bytes, StandardOpenOption.CREATE, StandardOpenOption.WRITE);

                // (2) S3 업로드 요청 객체 생성
                PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(fileName)
                        .build();

                // (3) S3에 업로드 실행
                s3Client.putObject(putObjectRequest, RequestBody.fromFile(path));

                // (4) 저장된 S3 URL 가져오기
                String s3Url = s3Client.utilities().getUrl(a -> a.bucket(bucket).key(fileName)).toExternalForm();

                // (5) 새 이미지 객체 생성 후 리스트에 추가
                AnnouncementImage announcementImage = AnnouncementImage.builder()
                        .imagePath(s3Url)
                        .announcement(announcement) // 연관관계 설정
                        .build();
                newImageList.add(announcementImage);
            }
        }
        announcement.updateAnnouncement(dto.getTitle(), dto.getContents(), dto.getStatus(), newImageList);

        System.out.println("이미지에용 : " + newImageList);
        announcementImageRepository.saveAll(newImageList);
        announcementRepository.save(announcement);

        return announcement;

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
                        .title(announcement.getTitle())
                        .creaetedDate(LocalDate.from(announcement.getCreatedTime())) // 엔티티에서 생성 날짜 가져오기
                        .announcementType(announcement.getType().name()) // Enum 타입을 문자열로 변환
                        .build())
                .collect(Collectors.toList());
    }
}
