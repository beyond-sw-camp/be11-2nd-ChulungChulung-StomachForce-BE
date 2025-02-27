package com.beyond.StomachForce.User.service;

import com.beyond.StomachForce.Post.domain.Enum.PostStatus;
import com.beyond.StomachForce.Post.domain.Post;
import com.beyond.StomachForce.Post.dtos.MyPostDto;
import com.beyond.StomachForce.Post.repository.PostRepository;
import com.beyond.StomachForce.Post.service.LikeService;
import com.beyond.StomachForce.User.domain.*;
import com.beyond.StomachForce.User.domain.Enum.*;
import com.beyond.StomachForce.User.dtos.*;
import com.beyond.StomachForce.User.repository.*;
import com.beyond.StomachForce.restaurant.domain.Restaurant;
import com.beyond.StomachForce.restaurant.repository.RestaurantRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.validation.constraints.Null;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.Block;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MileageRepository mileageRepository;
    private final VipBenefitRepository vipBenefitRepository;
    private final BlockingRepository blockingRepository;
    private final S3Client s3Client;
    private final LikeService likeService;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public UserService(PostRepository postRepository, UserRepository userRepository, PasswordEncoder passwordEncoder, MileageRepository mileageRepository, VipBenefitRepository vipBenefitRepository, BlockingRepository blockingRepository, S3Client s3Client, LikeService likeService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mileageRepository = mileageRepository;
        this.vipBenefitRepository = vipBenefitRepository;
        this.blockingRepository = blockingRepository;
        this.s3Client = s3Client;
        this.likeService = likeService;
    }



    public User save(UserSaveReq userSaveReq) throws IllegalArgumentException{
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
        String s3Url = s3Client.utilities().getUrl(a->a.bucket(bucket).key("basicProfile.jpg")).toExternalForm();
        user.updateImagePath(s3Url);
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
    public void updateByIdentify(UserUpdateReq userUpdateReq) throws IOException {
        String identify = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByIdentify(identify).orElseThrow(()->new EntityNotFoundException("없는 회원입니다."));
        String s3Url = "";
        if(userUpdateReq.getProfilePhoto()!= null){
            MultipartFile image = userUpdateReq.getProfilePhoto();
            byte[] bytes = image.getBytes();
            String fileName =image.getOriginalFilename();
            Path path = Paths.get("C:/Users/Playdata/Desktop/tmp/",fileName);
            Files.write(path,bytes, StandardOpenOption.CREATE,StandardOpenOption.WRITE);
            PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(bucket).key(fileName).build();
            s3Client.putObject(putObjectRequest, RequestBody.fromFile(path));
            s3Url = s3Client.utilities().getUrl(a->a.bucket(bucket).key(fileName)).toExternalForm();
        }else{
            s3Url = user.getProfilePhoto();
        }
        user.updateUser(userUpdateReq,s3Url);
    }

    public void quit(){
        String identify = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByIdentify(identify).orElseThrow(()->new EntityNotFoundException("없는 사람입니다."));
        likeService.removeUserLikes(String.valueOf(user.getId()));
        user.userStop();
    }

    public User login(LoginDto dto){
        boolean check = true;
        Optional<User> optionalUser = userRepository.findByIdentify(dto.getIdentify());
        if(!optionalUser.get().getUserStatus().equals(UserStatus.S)) {
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
        }else{
            throw new IllegalArgumentException("ID 또는 비밀번호가 일치하지 않습니다.");
        }
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

    public boolean isFollowing(String userNickName){
        String identify = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByIdentify(identify).orElseThrow(()->new EntityNotFoundException("없는 회원입니다."));
        return user.isFollowing(userNickName);
    }

    public List<FollowingListRes> follwings(){
        String identify = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByIdentify(identify).orElseThrow(()->new EntityNotFoundException("없는 회원입니다."));
        return user.followingList();
    }

    public UserInfoRes userInfo(){
        String identify = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByIdentify(identify).orElseThrow(()->new EntityNotFoundException("없는 회원입니다."));
        UserInfoRes userInfoRes = UserInfoRes.builder()
                .userId(user.getId())
                .role(user.getRole().toString())
                .identify(user.getIdentify())
                .userStatus(user.getUserStatus())
                .userNickName(user.getNickName())
                .userName(user.getName())
                .userEmail(user.getEmail())
                .userPhoneNumber(user.getPhoneNumber())
                .gender(user.getGender())
                .role(user.getRole().toString())
                .profilePhoto(user.getProfilePhoto())
                .build();
        return userInfoRes;
    }

    public MypageRes myPage(Pageable pageable) {
        String identify = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByIdentify(identify)
                .orElseThrow(() -> new EntityNotFoundException("없는 회원입니다."));

        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "id")
        );
        Page<Post> postPage = postRepository.findByUserAndPostStatus(user, PostStatus.Y, sortedPageable);

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

    public YourPageRes yourPage(Pageable pageable, UserSearchDto userSearchDto) {
        String nickName = userSearchDto.getNickName();
        User user = userRepository.findByNickName(nickName)
                .orElseThrow(() -> new EntityNotFoundException("없는 회원입니다."));
        String currentIdentify = SecurityContextHolder.getContext().getAuthentication().getName();

        boolean isFollowing = user.getFollowers().stream()
                .anyMatch(f -> f.getFollowerUser().getIdentify().equals(currentIdentify));

        // 페이징에 내림차순 정렬 조건 추가 (postId가 큰 순서대로)
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "id"));
        Page<Post> postPage = postRepository.findByUserAndPostStatus(user,PostStatus.Y ,sortedPageable);

        List<String> postPhotos = postPage.getContent().stream()
                .map(post -> post.getPostPhotos().isEmpty() ? null : post.getPostPhotos().get(0))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        List<MyPostDto> postIds = postPage.getContent().stream()
                .map(post -> new MyPostDto(post.getId()))
                .collect(Collectors.toList());

        YourPageRes yourpageRes = YourPageRes.builder()
                .profile(user.getProfilePhoto())
                .followings(user.followingList().size())
                .follwers(user.followerList().size())
                .nickName(user.getNickName())
                .email(user.getEmail())
                .influencer(user.getInfluencer())
                .postPhotos(postPhotos)
                .totalPost((int) postPage.getTotalElements())
                .postIds(postIds)
                .isFollowing(isFollowing)
                .build();
        return yourpageRes;
    }


    public Page<UserInfoRes> findUser(Pageable pageable, UserSearchDto searchDto){
        Specification<User> specification = new Specification<User>() {
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if(searchDto.getNickName() != null){
                    predicates.add(criteriaBuilder.like(root.get("nickName"), "%" + searchDto.getNickName() + "%"));
                }
                // 항상 탈퇴 상태(S)가 아닌 회원만 검색 결과에 포함
                predicates.add(criteriaBuilder.notEqual(root.get("userStatus"), UserStatus.S));
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }
        };
        Page<User> userList = userRepository.findAll(specification, pageable);
        return userList.map(u -> u.userInfoRes());
    }


    public Page<VipBenefitRes> myVip(Pageable pageable){
        String identify = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByIdentify(identify).orElseThrow(()->new EntityNotFoundException("없는 회원입니다."));
        VipGrade myGrade = user.getVipGrade();

        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "id")
        );

        Page<VipBenefit> vipBenefits = vipBenefitRepository.findByVipGrade(myGrade,sortedPageable);
        return vipBenefits.map(vb -> VipBenefitRes.builder()
                .vipGrade(myGrade)
                .title(vb.getTitle())
                .contents(vb.getContents())
                .benefitPhoto(vb.getBenefitPhoto())
                .build());
    }

    public VipBenefit vipBenefitRegist(VipBenefitRegistDto vipBenefitRegistDto) throws IOException {
        MultipartFile image = vipBenefitRegistDto.getBenefitPhoto();
        byte[] bytes = image.getBytes();
        String fileName = image.getOriginalFilename();
        Path path = Paths.get("C:/Users/Playdata/Desktop/tmp/",fileName);
        Files.write(path,bytes, StandardOpenOption.CREATE,StandardOpenOption.WRITE);
        PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(bucket).key(fileName).build();
        s3Client.putObject(putObjectRequest, RequestBody.fromFile(path));
        String s3Url = s3Client.utilities().getUrl(a->a.bucket(bucket).key(fileName)).toExternalForm();
        VipBenefit vipBenefitRegist = VipBenefit.builder()
                .vipGrade(vipBenefitRegistDto.getVipGrade())
                .title(vipBenefitRegistDto.getTitle())
                .contents(vipBenefitRegistDto.getContents())
                .benefitPhoto(s3Url)
                .build();
        VipBenefit vipBenefit = vipBenefitRepository.save(vipBenefitRegist);
        return vipBenefit;
    }

    public BlockUser blocking(UserBlockingDto userBlockingDto){
        String identify = SecurityContextHolder.getContext().getAuthentication().getName();
        User blockingUser = userRepository.findByIdentify(identify).orElseThrow(()->new EntityNotFoundException("없는 회원입니다."));
        String blockedUserNickName = userBlockingDto.getBlockedUserNickName();
        User blockedUser = userRepository.findByNickName(blockedUserNickName).orElseThrow(()->new EntityNotFoundException("없는 회원입니다."));
        BlockUser blockUser = blockingRepository.save(BlockUser.builder().blocker(blockingUser).blocked(blockedUser).build());
        return blockUser;
    }

    public boolean[] isBlockedBy(UserBlockingDto userBlockingDto){
        String currentIdentify = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByIdentify(currentIdentify)
                .orElseThrow(() -> new EntityNotFoundException("없는 회원입니다."));
        String otherUserNickName = userBlockingDto.getBlockedUserNickName();
        User otherUser = userRepository.findByNickName(otherUserNickName)
                .orElseThrow(() -> new EntityNotFoundException("없는 회원입니다."));

        List<BlockUser> blocks = blockingRepository.findByBlocker(otherUser).orElse(Collections.emptyList());
        List<BlockUser> blocked = blockingRepository.findByBlocker(currentUser).orElse(Collections.emptyList());
        boolean[] result = new boolean[2];
        result[0] = blocks.stream().anyMatch(block -> block.getBlocked().getId().equals(currentUser.getId()));
        result[1] = blocked.stream().anyMatch(block -> block.getBlocked().getId().equals(otherUser.getId()));
        return result;
    }

    public BlockUser unblockUser(UserBlockingDto userBlockingDto) {
        String identify = SecurityContextHolder.getContext().getAuthentication().getName();
        User blocker = userRepository.findByIdentify(identify)
                .orElseThrow(() -> new EntityNotFoundException("차단하는 회원을 찾을 수 없습니다."));
        User blocked = userRepository.findByNickName(userBlockingDto.getBlockedUserNickName())
                .orElseThrow(() -> new EntityNotFoundException("차단당한 회원을 찾을 수 없습니다."));

        // 차단 관계가 존재하는지 확인 후 삭제
        BlockUser blockOpt = blockingRepository.findByBlockerAndBlocked(blocker, blocked).orElseThrow(()->new EntityNotFoundException("해당 차단 내역이 없습니다."));
        blockingRepository.delete(blockOpt);
        return blockOpt;
    }

    public List<BlockedUserRes> blockedUsers(){
        String identify = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByIdentify(identify).orElseThrow(()->new EntityNotFoundException("없는 회원입니다."));
        List<BlockUser> blocks = blockingRepository.findByBlocker(user).orElse(Collections.emptyList());
        List<BlockedUserRes> blockedUserResList = blocks.stream()
                .map(block -> BlockedUserRes.builder()
                        .userNickName(block.getBlocked().getNickName())
                        .userProfile(block.getBlocked().getProfilePhoto()) // User 엔티티에 프로필 사진이 profilePhoto라 가정
                        .build())
                .collect(Collectors.toList());
        return blockedUserResList;
    }

    public List<TopInfluencerRes> getTopInfluencers(int limit) {
        List<Object[]> topUsers = userRepository.findTopInfluencersByInfluencer(
                Influencer.Y, PageRequest.of(0, limit)
        );

        return topUsers.stream()
                .map(obj -> TopInfluencerRes.builder()
                        .userId((Long) obj[0])
                        .profileImage((String)obj[1])
                        .nickname((String)obj[2])
                        .followersCount(((Long)obj[3]).intValue())
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

    public List<UserListRes> getAllUserProfiles() {
        return userRepository.findAll().stream()
                .map(user -> UserListRes.builder()
                        .userId(user.getId())
                        .role(user.getRole().toString())
                        .profilePhoto(user.getProfilePhoto())
                        .identify(user.getIdentify())
                        .email(user.getEmail())
                        .phoneNumber(user.getPhoneNumber())
                        .vipGrade(user.getVipGrade())
                        .influencer(user.getInfluencer())
                        .userStatus(user.getUserStatus())
                        .nickName(user.getNickName())
                        .build())
                .collect(Collectors.toList());
    }

    public void updateUserStatus(Long id, UserStatusUpdateDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 사용자를 찾을 수 없습니다."));

        user.updateUserStatus(dto.getVipGrade(), dto.getInfluencer(), dto.getUserStatus());
    }

    public boolean validId(IdValidReq idValidReq){
        Optional<User> optionalUser = userRepository.findByIdentify(idValidReq.getIdentify());
        if(optionalUser.isPresent() && optionalUser.get().getUserStatus() != UserStatus.S) {
            return false;
        }
        return true;
    }

    public boolean validNickName(NickNameValidReq nickNameValidReq){
        Optional<User> optionalUser = userRepository.findByNickName(nickNameValidReq.getNickName());
        if(optionalUser.isPresent() && optionalUser.get().getUserStatus() != UserStatus.S) {
            return false;
        }
        return true;
    }
}
