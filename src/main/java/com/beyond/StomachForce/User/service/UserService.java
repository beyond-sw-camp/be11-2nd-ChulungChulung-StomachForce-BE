package com.beyond.StomachForce.User.service;

import com.beyond.StomachForce.User.domain.Enum.EarnedMileage;
import com.beyond.StomachForce.User.domain.Follower;
import com.beyond.StomachForce.User.domain.Mileage;
import com.beyond.StomachForce.User.domain.User;
import com.beyond.StomachForce.User.domain.UserAddress;
import com.beyond.StomachForce.User.dtos.*;
import com.beyond.StomachForce.User.repository.MileageRepository;
import com.beyond.StomachForce.User.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MileageRepository mileageRepository;
    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, MileageRepository mileageRepository, S3Client s3Client) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mileageRepository = mileageRepository;
        this.s3Client = s3Client;
    }

    public User save(UserSaveReq userSaveReq) throws IllegalArgumentException {
        if(userRepository.findByName(userSaveReq.getName()).isPresent()){
            if(userRepository.findByBirth(userSaveReq.getBirth()).isPresent()){
                throw new IllegalArgumentException("이미 가입된 회원입니다.");
            }
        }
        String password = passwordEncoder.encode(userSaveReq.getPassword());
        User user = userSaveReq.toEntity(password);
        String state = userSaveReq.getUserAddress().getState();
        String city = userSaveReq.getUserAddress().getCity();
        String village = userSaveReq.getUserAddress().getVillage();
        UserAddress userAddress  = UserAddress.builder().state(state).city(city).village(village).user(user).build();
        user.getUserAddresses().add(userAddress);
        User finalUser = userRepository.save(user);
        return finalUser;
    }

    public String profile(ProfileReq profileReq) throws IOException {
        String identify = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByIdentify(identify).orElseThrow(()->new EntityNotFoundException("없는 회원입니다."));
        MultipartFile image = profileReq.getProfilePhoto();
        byte[] bytes = image.getBytes();
        String fileName = user.getId()+"_"+ image.getOriginalFilename();
        Path path = Paths.get("C:/Users/Playdata/Desktop/tmp/",fileName);
        Files.write(path,bytes, StandardOpenOption.CREATE,StandardOpenOption.WRITE);
        PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(bucket).key(fileName).build();
        s3Client.putObject(putObjectRequest, RequestBody.fromFile(path));
        String s3Url = s3Client.utilities().getUrl(a->a.bucket(bucket).key(fileName)).toExternalForm();
        user.updateImagePath(s3Url);
        return "프로필이 등록되었습니다.";
    }
    public void updateByIdentify(UserUpdateReq userUpdateReq){
        String identify = userUpdateReq.getIdentify();
        User user = userRepository.findByIdentify(identify).orElseThrow(()->new EntityNotFoundException("없는 id입니다"));
        user.updateUser(userUpdateReq);
    }

    public void quit(String identify){
        User user = userRepository.findByIdentify(identify).orElseThrow(()->new EntityNotFoundException("없는 사람입니다."));
        user.userStop();
    }

    public User login(LoginDto dto){
        boolean check = true;
        Optional<User> optionalUser = userRepository.findByIdentify(dto.getIdentify());
        if(!optionalUser.isPresent()){
            check = false;
        }
        if(!passwordEncoder.matches(dto.getPassword(), optionalUser.get().getPassword())){
            check =false;
        }
        if(!check){
            throw new IllegalArgumentException("ID 또는 비밀번호가 일치하지 않습니다.");
        }
        return optionalUser.get();
    }

    public Mileage mangeMileage(ManageMileageDto manageMileageDto){
        String identify = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByIdentify(identify).orElseThrow(()->new EntityNotFoundException("없는 회원입니다."));
        if(manageMileageDto.getEarnedMileage().equals(EarnedMileage.USE)){
            user.mileageUpdate(user.getMileageBalance()-manageMileageDto.getMileageAmount());
        }else{
            user.mileageUpdate(user.getMileageBalance()+manageMileageDto.getMileageAmount());
        }
        Mileage mileage = Mileage.builder().userId(user.getId()).earnedMileage(manageMileageDto.getEarnedMileage()).mileageAmount(manageMileageDto.getMileageAmount()).build();
        Mileage saveMileage = mileageRepository.save(mileage);
        return saveMileage;
    }

    public String follower(Long userId){
        String identify = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByIdentify(identify).orElseThrow(()->new EntityNotFoundException("없는 회원입니다."));
        User followUser = userRepository.findById(userId).orElseThrow(()->new EntityNotFoundException("없는 회원입니다."));
        if(!followUser.getFollowers().isEmpty()){
            for(Follower f : followUser.getFollowers()){
                if(f.getFollowerId().equals(user.getId())){
                    followUser.getFollowers().remove(f);
                    return "팔로우가 취소되었습니다.";
                }
            }
        }
        Follower follower = Follower.builder().user(followUser).followerId(user.getId()).build();
        user.followerAdd(follower);
        return "ok";
    }

    public List<FollowerListRes> follwers(Long userId){
        User user = userRepository.findById(userId).orElseThrow(()->new EntityNotFoundException("없는 회원입니다."));
        List<FollowerListRes> follwerList = new ArrayList<>();
        return user.list();
    }

    public UserInfoRes userInfo(){
        String identify = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByIdentify(identify).orElseThrow(()->new EntityNotFoundException("없는 회원입니다."));
        UserInfoRes userInfoRes = UserInfoRes.builder().userName(user.getName()).profilePhoto(user.getProfilePhoto()).build();
        return userInfoRes;
    }
}
