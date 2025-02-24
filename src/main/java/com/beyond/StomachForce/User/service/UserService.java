package com.beyond.StomachForce.User.service;

import com.beyond.StomachForce.Post.domain.Post;
import com.beyond.StomachForce.Post.dtos.MyPostDto;
import com.beyond.StomachForce.Post.repository.PostRepository;
import com.beyond.StomachForce.User.domain.Enum.EarnedMileage;
import com.beyond.StomachForce.User.domain.Follower;
import com.beyond.StomachForce.User.domain.Mileage;
import com.beyond.StomachForce.User.domain.User;
import com.beyond.StomachForce.User.domain.UserAddress;
import com.beyond.StomachForce.User.dtos.*;
import com.beyond.StomachForce.User.repository.MileageRepository;
import com.beyond.StomachForce.User.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MileageRepository mileageRepository;
    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public UserService(PostRepository postRepository, UserRepository userRepository, PasswordEncoder passwordEncoder, MileageRepository mileageRepository, S3Client s3Client) {
        this.postRepository = postRepository;
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

    public String follow(FollowReq followReq){
        String identify = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByIdentify(identify).orElseThrow(()->new EntityNotFoundException("없는 회원입니다."));
        User followUser = userRepository.findByNickName(followReq.getNickName()).orElseThrow(()->new EntityNotFoundException("없는 회원입니다."));
        for (Follower f : followUser.getFollowers()) {
            if (f.getFollowerUser().getId().equals(user.getId())) {
                followUser.getFollowers().remove(f);
                user.getFollowing().remove(f);
                return "팔로우가 취소되었습니다.";
            }
        }
        Follower follower = Follower.builder()
                .user(followUser)
                .followerUser(user)
                .build();

        followUser.followerAdd(follower);
        user.followingAdd(follower);
        return "ok";
    }

    public List<FollowerListRes> follwers(){
        String identify = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByIdentify(identify).orElseThrow(()->new EntityNotFoundException("없는 회원입니다."));
        List<FollowerListRes> follwerList = new ArrayList<>();
        return user.followerList();
    }

    public List<FollowingListRes> follwings(){
        String identify = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByIdentify(identify).orElseThrow(()->new EntityNotFoundException("없는 회원입니다."));
        return user.followingList();
    }

    public UserInfoRes userInfo(){
        String identify = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByIdentify(identify).orElseThrow(()->new EntityNotFoundException("없는 회원입니다."));
        UserInfoRes userInfoRes = UserInfoRes.builder().userId(user.getId()).userName(user.getName()).profilePhoto(user.getProfilePhoto()).build();
        return userInfoRes;
    }

    public MypageRes myPage(Pageable pageable) {
        String identify = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByIdentify(identify)
                .orElseThrow(() -> new EntityNotFoundException("없는 회원입니다."));

        Page<Post> postPage = postRepository.findByUser(user, pageable);

        // 게시글 사진 리스트 생성
        List<String> postPhotos = postPage.getContent().stream()
                .map(post -> post.getPostPhotos().isEmpty() ? null : post.getPostPhotos().get(0))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        List<MyPostDto> postIds = postPage.getContent().stream()
                .map(post -> new MyPostDto(post.getId()))
                .collect(Collectors.toList());

        return MypageRes.builder()
                .nickName(user.getNickName())
                .email(user.getEmail())
                .influencer(user.getInfluencer())
                .postPhotos(postPhotos)
                .postIds(postIds)
                .totalPost((int) postPage.getTotalElements())
                .build();
    }


    public Page<UserInfoRes> findUser(Pageable pageable, UserSearchDto searchDto){
        Specification<User> specification = new Specification<User>() {
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if(searchDto.getNickName()!=null){
                    predicates.add(criteriaBuilder.like(root.get("nickName"), "%"+searchDto.getNickName()+"%"));
                }
                Predicate[] predicateArr = new Predicate[predicates.size()];
                for(int i=0; i<predicates.size();i++){
                    predicateArr[i] = predicates.get(i);
                }
                Predicate predicate = criteriaBuilder.and(predicateArr);
                return predicate;
            }
        };
        Page<User> userList = userRepository.findAll(specification,pageable);
        return userList.map(u->u.userInfoRes());
    }

    public YourPageRes yourPage(Pageable pageable,UserSearchDto userSearchDto){
        String nickName = userSearchDto.getNickName();
        User user = userRepository.findByNickName(nickName).orElseThrow(()->new EntityNotFoundException("없는 회원입니다."));
        String currentIdentify = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isFollowing = user.getFollowers().stream()
                .anyMatch(f -> f.getFollowerUser().getIdentify().equals(currentIdentify));
        List<Post> myPost = user.getPosts();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), myPost.size());
        List<Post> pagedPosts = new ArrayList<>();
        if(start < end) {
            pagedPosts = myPost.subList(start, end);
        }

        List<String> myPostPhotos = new ArrayList<>();
        for (Post p : pagedPosts) {
            List<String> photos = p.getPostPhotos();
            if (photos != null && !photos.isEmpty()) {
                myPostPhotos.add(photos.get(0));
            }
        }
        YourPageRes yourpageRes = YourPageRes.builder()
                .profile(user.getProfilePhoto())
                .followings(user.followingList().size())
                .follwers(user.followerList().size())
                .nickName(user.getNickName())
                .email(user.getEmail())
                .influencer(user.getInfluencer())
                .postPhotos(myPostPhotos)
                .totalPost(myPost.size())
                .isFollowing(isFollowing)
                .build();
        return yourpageRes;
    }

    public List<TopInfluencerRes> getTopInfluencers(int limit) {
        List<User> topUsers = userRepository.findTopInfluencers(PageRequest.of(0, limit));

        return topUsers.stream()
                .map(user -> TopInfluencerRes.builder()
                        .userId(user.getId())
                        .profileImage(user.getProfilePhoto())
                        .nickname(user.getNickName())
                        .followersCount(user.getFollowers().size())//유저 팔로워로 팔로워수 체크
                        .build())
                .collect(Collectors.toList());
    }
    public UserInfoRes getUserInfo(User user) {
        if (user == null) {
            throw new IllegalArgumentException("로그인한 유저가 존재하지 않습니다.");
        }

        return UserInfoRes.builder()
                .userId(user.getId())
                .userName(user.getName())
                .userNickName(user.getNickName())
                .role(user.getRole().toString()) // 🔹 유저 역할 반환
                .profilePhoto(user.getProfilePhoto())
                .build();
    }
}
