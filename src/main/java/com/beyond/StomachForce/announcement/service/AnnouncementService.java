package com.beyond.StomachForce.announcement.service;


import com.beyond.StomachForce.User.domain.User;
import com.beyond.StomachForce.User.repository.UserRepository;
import com.beyond.StomachForce.accouncementImage.domain.AnnouncementImage;
import com.beyond.StomachForce.accouncementImage.repository.AnnouncementImageRepository;
import com.beyond.StomachForce.announcement.domain.Announcement;
import com.beyond.StomachForce.announcement.dtos.AnnouncementCreateReq;
import com.beyond.StomachForce.announcement.repository.AnnouncementRepository;
import com.beyond.StomachForce.restaurant.domain.RestaurantPhoto;
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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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
}
