package com.beyond.StomachForce.restaurant.service;

import com.beyond.StomachForce.Common.Auth.JwtTokenProvider;
import com.beyond.StomachForce.restaurant.domain.*;
import com.beyond.StomachForce.restaurant.domain.select.RestaurantInfoStatus;
import com.beyond.StomachForce.restaurant.dtos.*;
import com.beyond.StomachForce.restaurant.domain.select.BookmarkType;

import com.beyond.StomachForce.restaurant.dtos.forLogin.LoginDto;
import com.beyond.StomachForce.restaurant.dtos.forLogin.RestaurantRefreshDto;
import com.beyond.StomachForce.restaurant.dtos.forRestaurantInfo.RestaurantInfoCreateReq;
import com.beyond.StomachForce.restaurant.dtos.forRestaurantInfo.RestaurantInfoListRes;
import com.beyond.StomachForce.restaurant.dtos.forRestaurantInfo.RestaurantInfoUpdateReq;
import com.beyond.StomachForce.restaurant.repository.BookmarkRepository;
import com.beyond.StomachForce.restaurant.repository.RestaurantInfoRepository;
import com.beyond.StomachForce.restaurant.repository.RestaurantRepository;
import com.beyond.StomachForce.review.repository.ReviewRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
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
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Transactional
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final ReviewRepository reviewRepository;
    private final BookmarkRepository bookmarkRepository;
    private final PasswordEncoder passwordEncoder;
    // 로그인에 아래 두개 필요함,
    private final JwtTokenProvider jwtTokenProvider;
    @Qualifier("rtdb")
    private final RedisTemplate<String, Object> redisTemplate;
    private final RestaurantInfoRepository restaurantInfoRepository;

    //사진 넣을 때 필요한 의존성 추가
    private final S3Client s3Client;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;



    public RestaurantService(RestaurantRepository restaurantRepository, ReviewRepository reviewRepository,
                             BookmarkRepository bookmarkRepository, PasswordEncoder passwordEncoder,
                             JwtTokenProvider jwtTokenProvider, RedisTemplate<String, Object> redisTemplate,
                             S3Client s3Client, RestaurantInfoRepository restaurantInfoRepository) {
        this.restaurantRepository = restaurantRepository;
        this.reviewRepository = reviewRepository;
        this.bookmarkRepository = bookmarkRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisTemplate = redisTemplate;
        this.restaurantInfoRepository = restaurantInfoRepository;
        this.s3Client = s3Client;
    }

    public Page<RestaurantListRes> findAll(Pageable pageable, RestaurantSearchDto searchDto){
        Specification<Restaurant> specification = new Specification<Restaurant>() {
            @Override
            public Predicate toPredicate(Root<Restaurant> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if(searchDto.getName() != null){
                    predicates.add(criteriaBuilder.like(root.get("name"), "%" + searchDto.getName() + "%"));
                }
                if (searchDto.getAddress() != null) {
                    Join<Restaurant, RestaurantAddress> addressJoin = root.join("address"); // RestaurantAddress와 조인
                    Predicate cityPredicate = criteriaBuilder.like(addressJoin.get("city"), "%" + searchDto.getAddress() + "%");
                    Predicate streetPredicate = criteriaBuilder.like(addressJoin.get("street"), "%" + searchDto.getAddress() + "%");
                    predicates.add(criteriaBuilder.or(cityPredicate, streetPredicate)); // OR 조건 적용
                }
                Predicate[] predicateArr = new Predicate[predicates.size()];
                for(int i=0; i<predicates.size(); i++){
                    predicateArr[i] = predicates.get(i);
                }
                Predicate predicate = criteriaBuilder.and(predicateArr);
                return predicate;
            }
        };
        Page<Restaurant> restaurantList = restaurantRepository.findAll(specification, pageable);
        return restaurantList.map(p->p.listDtoFromEntity());
    }

    public RestaurantDetailRes findById(Long id){
        Restaurant restaurant = restaurantRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("Restaurant with id " + id + " not found"));
        return restaurant.detailFromEntity();
    }

    public Long save(RestaurantCreateReq restaurantCreateReq){

        if(restaurantRepository.findByEmail(restaurantCreateReq.getEmail()).isPresent()){
            throw new IllegalArgumentException("email already exists");
        }
        if(restaurantCreateReq.getPassword().length()<8){
            throw new IllegalArgumentException("비번 너무 짧아요");
        }

        //      비번 암호화
        String password = passwordEncoder.encode(restaurantCreateReq.getPassword());
        //      save 메서드도 사용할 겸 사진을 넣을 때 필요한 restaurant 객체 생성
        Restaurant restaurant = restaurantRepository.save(restaurantCreateReq.toEntity(password));
        //      사진 넣을 list 생성
        List<RestaurantPhoto> restaurantPhotos = new ArrayList<>();

        //      aws에 image 저장 후에 url 추출
        //      aws에 s3 접근 가능한  iam(새끼계정)계정 생성 iam계정을 통해 aws에 접근 가능한 접근 객체 생성(config에 AwsS3Config)

        //지금의 경우 List형태이므로 for문으로 통해 하나하나 넣어야함
        for(MultipartFile image: restaurantCreateReq.getRestaurantPhotos()){
            try {
                byte[] bytes = image.getBytes();
                String fileName = restaurant.getId() + "_" + image.getOriginalFilename();
                //      먼저 local에 저장
                Path path = Paths.get("C:/Users/Playdata/Desktop/testFolder" , fileName);
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
                RestaurantPhoto restaurantPhoto = RestaurantPhoto.builder()
                        .photoUrl(s3Url)
                        .restaurant(restaurant)
                        .build();
                // 그리고 list에 넣는다
                restaurantPhotos.add(restaurantPhoto);


            } catch (IOException e) {
                throw new RuntimeException("이미지 저장 실패");
            }
        }

        restaurant.getPhotos().addAll(restaurantPhotos);;
        restaurantRepository.save(restaurant);

        return restaurant.getId();

    }

    public Map<String, Object> login(LoginDto dto){
        // 사업자등록증 여부 확인
       Restaurant restaurant = restaurantRepository.findByRegistrationNumber(dto.getRegistrationNumber())
               .orElseThrow(()-> new EntityNotFoundException("사업자등록증 또는 비밀번호를 확인해주세요."));
       if(!passwordEncoder.matches(dto.getPassword(), restaurant.getPassword())){
           throw new IllegalArgumentException("사업자등록증 또는 비밀번호를 확인해주세요.");
       }

       String at = jwtTokenProvider.createToken
               (restaurant.getRegistrationNumber(),restaurant.getRole().toString());
       String rt = jwtTokenProvider.createRefreshToken
               (restaurant.getRegistrationNumber(),restaurant.getRole().toString());
        //      redis 에 rt 저장(상단에서 redisTemplate 주입함)
        redisTemplate.opsForValue().set(restaurant.getRegistrationNumber(), rt,200, TimeUnit.DAYS);
        //      사용자에게 at, rt 지급
        Map<String, Object> loginInfo = new HashMap<>();
        loginInfo.put("id",restaurant.getId());
        loginInfo.put("token",at);
        loginInfo.put("refreshToken",rt);
        return loginInfo;
    }
    //  rt 기반으로 at 재발급해주는 로직
    public String refreshAccessToken(RestaurantRefreshDto dto, String secretKeyRt){
        // rt 디코딩
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKeyRt)
                .build()
                .parseClaimsJws(dto.getRefreshToken())
                .getBody();
        //  redis 에서 리프레시 토큰 가져오기
        Object rt = redisTemplate.opsForValue().get(claims.getSubject());
        if(rt == null || !rt.toString().equals(dto.getRefreshToken())){
            throw new IllegalArgumentException("토큰이 만료되었습니다.");

        }
        // 새로운 액세스 토큰 생성 후 반환
        return jwtTokenProvider.createToken(claims.getSubject(), claims.get("role").toString());

    }



    public void update(Long id, RestaurantUpdateReq restaurantUpdateReq){
        String registrationNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("없는 사용자입니다."));

        if(!registrationNumber.equals(restaurant.getRegistrationNumber())){
            throw new IllegalArgumentException("회원정보가 일치하지 않습니다.");     // 사업자등록증 다름.
        }
        if(!passwordEncoder.matches(restaurantUpdateReq.getCurrentPassword(),restaurant.getPassword())){
            throw new IllegalArgumentException("회원정보가 일치하지 않습니다.");     // 비번틀림
        }
        if (restaurantUpdateReq.getCurrentPassword() == null || restaurantUpdateReq.getCurrentPassword().isBlank()) {
            throw new IllegalArgumentException("회원정보가 일치하지 않습니다.");     // 현재 비밀번호 입력
        }

        String password = passwordEncoder.encode(restaurantUpdateReq.getPassword());
        restaurant.updateProfile(restaurantUpdateReq, password);

        //  사진 삭제 처리
        if (restaurantUpdateReq.getPhotoUrlsToRemove() != null) {
            restaurant.removePhotos(restaurantUpdateReq.getPhotoUrlsToRemove());
        }

        //  새로운 사진 추가 (중복 방지)
        if (restaurantUpdateReq.getRestaurantPhotos() != null && !restaurantUpdateReq.getRestaurantPhotos().isEmpty()) {
            List<RestaurantPhoto> newPhotos = new ArrayList<>();
            for (MultipartFile image : restaurantUpdateReq.getRestaurantPhotos()) {
                try {
                    byte[] bytes = image.getBytes();
                    String fileName = restaurant.getId() + "_" + image.getOriginalFilename();
                    Path path = Paths.get("C:/Users/Playdata/Desktop/testFolder", fileName);
                    Files.write(path, bytes, StandardOpenOption.CREATE, StandardOpenOption.WRITE);

                    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(fileName)
                            .build();
                    s3Client.putObject(putObjectRequest, RequestBody.fromFile(path));

                    String s3Url = s3Client.utilities().getUrl(a -> a.bucket(bucket).key(fileName)).toExternalForm();
                    RestaurantPhoto restaurantPhoto = new RestaurantPhoto(s3Url, restaurant);

                    if (!restaurant.getPhotos().contains(restaurantPhoto)) { // 중복 방지
                        newPhotos.add(restaurantPhoto);
                    }
                } catch (IOException e) {
                    throw new RuntimeException("이미지 저장 실패");
                }
            }
            restaurant.addPhotos(newPhotos);
        }
        // info 관련 로직
        // RestaurantInfo 생성 또는 수정
        if (restaurantUpdateReq.getInfoText() != null && !restaurantUpdateReq.getInfoText().isBlank()) {
            Optional<RestaurantInfo> infotext = restaurantInfoRepository.findTop5ByRestaurantIdAndRestaurantInfoStatusOrderByCreatedTimeDesc(
                            restaurant.getId(), RestaurantInfoStatus.ACTIVE)
                    .stream()
                    .findFirst();

            if(restaurantUpdateReq.getInfoText().length()>20){
                throw new IllegalArgumentException("20글자를 넘을 수 없습니다.");
            }

            if (infotext.isPresent()) {
                // 기존 정보가 있으면 업데이트
                infotext.get().updateInfo(restaurantUpdateReq.getInfoText());
                restaurantInfoRepository.save(infotext.get());
            } else {
                // 기존 정보가 없으면 새로 생성
                RestaurantInfo newInfo = RestaurantInfo.builder()
                        .restaurant(restaurant)
                        .informationText(restaurantUpdateReq.getInfoText())
                        .build();
                restaurantInfoRepository.save(newInfo);
            }
        }

    }

    public void delete (){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Restaurant restaurant = restaurantRepository
                .findByRegistrationNumberAndRestaurantStatus(authentication.getName(), RestaurantInfoStatus.ACTIVE)
                .orElseThrow(()-> new EntityNotFoundException("없는 아이디 입니다."));
        restaurant.deleteRestaurant();
    }

    //id로 사진 찾는 메서드(레스토랑 아이디 활용)        //사진 강의 참고하여 수정 필요
    public List<String> findPhotosByRestaurantId(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));

        return restaurant.getPhotos().stream().map(RestaurantPhoto::getPhotoUrl).collect(Collectors.toList());
    }

    // info 관련 메서드--------------------------------------------------------------------------------------------
    public void infoCreate(Long restaurantId, RestaurantInfoCreateReq req){
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));

        RestaurantInfo restaurantInfo = RestaurantInfo.builder()
                .restaurant(restaurant)
                .informationText(req.getInfoText())
                .build();
        restaurantInfoRepository.save(restaurantInfo);
    }

    //  최신 5개 ACTIVE 상태 정보 조회(페이징처리해서 상단 5개만 보여줌)
    public List<RestaurantInfoListRes> findInfoAll(Long restaurantId) {
        return restaurantInfoRepository.findTop5ByRestaurantIdAndRestaurantInfoStatusOrderByCreatedTimeDesc(
                        restaurantId, RestaurantInfoStatus.ACTIVE)
                .stream()
                .map(info -> new RestaurantInfoListRes(
                        info.getId(), info.getInformationText(), info.getRestaurantInfoStatus(), info.getCreatedTime()))
                .collect(Collectors.toList());
    }

    // 정보 수정
    public void infoUpdate(Long id, RestaurantInfoUpdateReq dto) {
        RestaurantInfo restaurantInfo = restaurantInfoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID의 정보가 존재하지 않습니다."));

        restaurantInfo.updateInfo(dto.getInformationText());
        restaurantInfoRepository.save(restaurantInfo);
    }

    // 정보 삭제 후 최신 INACTIVE 중 하나를 활성화
    public Long infoDelete(Long id) {
        RestaurantInfo restaurantInfo = restaurantInfoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID의 정보가 존재하지 않습니다."));

        restaurantInfo.deactivate();
        restaurantInfoRepository.save(restaurantInfo);
        return restaurantInfo.getId();
    }







    // info 관련 메서드--------------------------------------------------------------------------------------------

    //북마크 (토글)
//    public void toggleBookmark(Long restaurantId, Long userId) {
//        Restaurant restaurant = restaurantRepository.findById(restaurantId)
//                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));
//
//        Optional<Bookmark> bookmark = bookmarkRepository.findByRestaurantIdAndUserId(restaurantId,userId);
//
//        if (bookmark.isPresent()) {
//            bookmarkRepository.delete(bookmark.get()); // 즐겨찾기 삭제
//        } else {
//            Bookmark newBookmark = Bookmark.builder()
//                    .restaurant(restaurant)
//                    .bookmarkType(BookmarkType.YES)
//                    .build();
//            bookmarkRepository.save(newBookmark); // 즐겨찾기 추가
//        }
//    }
}
