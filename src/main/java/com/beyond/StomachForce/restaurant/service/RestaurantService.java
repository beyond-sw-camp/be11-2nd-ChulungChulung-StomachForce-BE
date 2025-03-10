package com.beyond.StomachForce.restaurant.service;

import com.beyond.StomachForce.Common.Auth.JwtTokenProvider;
import com.beyond.StomachForce.User.domain.User;
import com.beyond.StomachForce.User.repository.UserRepository;
import com.beyond.StomachForce.menu.domain.Menu;
import com.beyond.StomachForce.menu.dto.MenuResDto;
import com.beyond.StomachForce.restaurant.domain.*;
import com.beyond.StomachForce.restaurant.domain.select.DepositAvailable;
import com.beyond.StomachForce.restaurant.domain.select.RestaurantInfoStatus;

import com.beyond.StomachForce.restaurant.domain.select.RestaurantType;

import com.beyond.StomachForce.restaurant.domain.select.RestaurantStatus;
import com.beyond.StomachForce.restaurant.dtos.*;

import com.beyond.StomachForce.restaurant.dtos.forLogin.LoginDto;
import com.beyond.StomachForce.restaurant.dtos.forLogin.RestaurantRefreshDto;
import com.beyond.StomachForce.restaurant.dtos.forRestaurantInfo.RestaurantInfoCreateReq;
import com.beyond.StomachForce.restaurant.dtos.forRestaurantInfo.RestaurantInfoListRes;
import com.beyond.StomachForce.restaurant.dtos.forRestaurantInfo.RestaurantInfoUpdateReq;
import com.beyond.StomachForce.restaurant.repository.BookmarkRepository;
import com.beyond.StomachForce.restaurant.repository.RestaurantInfoRepository;
import com.beyond.StomachForce.restaurant.repository.RestaurantPhotoRepository;
import com.beyond.StomachForce.restaurant.repository.RestaurantRepository;
import com.beyond.StomachForce.review.repository.ReviewRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final UserRepository userRepository;
    private final RestaurantPhotoRepository restaurantPhotoRepository;
    //사진 넣을 때 필요한 의존성 추가
    private final S3Client s3Client;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;



    public RestaurantService(RestaurantRepository restaurantRepository, ReviewRepository reviewRepository,
                             BookmarkRepository bookmarkRepository, PasswordEncoder passwordEncoder,
                             JwtTokenProvider jwtTokenProvider, RedisTemplate<String, Object> redisTemplate,

    S3Client s3Client, RestaurantInfoRepository restaurantInfoRepository, UserRepository userRepository, RestaurantPhotoRepository restaurantPhotoRepository) {
        this.restaurantRepository = restaurantRepository;
        this.reviewRepository = reviewRepository;
        this.bookmarkRepository = bookmarkRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisTemplate = redisTemplate;
        this.restaurantInfoRepository = restaurantInfoRepository;
        this.s3Client = s3Client;
        this.userRepository = userRepository;
        this.restaurantPhotoRepository = restaurantPhotoRepository;
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
                if (searchDto.getRestaurantType() != null) {
                    RestaurantType type = RestaurantType.valueOf(searchDto.getRestaurantType());
                    predicates.add(criteriaBuilder.equal(root.get("restaurantType"), type));
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

    public Page<RestaurantListRes> findAllKorean (Pageable pageable, RestaurantSearchDto searchDto){
        Specification<Restaurant> specification = new Specification<Restaurant>() {
            @Override
            public Predicate toPredicate(Root<Restaurant> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(criteriaBuilder.equal(root.get("restaurantType"), RestaurantType.KOREAN));

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
        List<String> informationTexts = restaurant.getInfos().stream()
                .filter(info -> info.getRestaurantInfoStatus() == RestaurantInfoStatus.ACTIVE) // ACTIVE 상태인 것만 필터링
                .map(RestaurantInfo::getInformationText)  // 각 RestaurantInfo 객체의 informationText를 뽑기
                .collect(Collectors.toList());  // 리스트로 수집
        return restaurant.detailFromEntity(informationTexts);
    }

    public Long save(RestaurantCreateReq restaurantCreateReq){

        if(restaurantRepository.findByEmail(restaurantCreateReq.getEmail()).isPresent()){
            throw new IllegalArgumentException("email already exists");
        }
        if(restaurantCreateReq.getPassword().length()<8){
            throw new IllegalArgumentException("비번 너무 짧아요");
        }
        if (restaurantRepository.findByName(restaurantCreateReq.getName()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 레스토랑 이름입니다.");
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
        loginInfo.put("name",restaurant.getName());
        loginInfo.put("email",restaurant.getEmail());
        loginInfo.put("userType",restaurant.getRole().toString());
        loginInfo.put("restaurantType", restaurant.getRestaurantType());
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



    public void update(RestaurantUpdateReq restaurantUpdateReq){
        String registrationNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        Restaurant restaurant = restaurantRepository.findByRegistrationNumber(registrationNumber)
                .orElseThrow(()-> new EntityNotFoundException("없는 사용자입니다."));
        restaurant.updateProfile(restaurantUpdateReq);
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
    public String addPhoto(RestaurantPhotoAdd dto) throws IOException {
        String registrationNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        Restaurant restaurant = restaurantRepository.findByRegistrationNumber(registrationNumber).orElseThrow(()-> new EntityNotFoundException("없는 레스토랑"));
        MultipartFile image = dto.getAdditionalPhoto();
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
        restaurantPhotoRepository.save(RestaurantPhoto.builder().restaurant(restaurant).photoUrl(s3Url).build());
        return "ok";
    }

    public List<MyPhotoRes> findMyPhotos(){
        String registrationNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        Restaurant restaurant = restaurantRepository.findByRegistrationNumber(registrationNumber).orElseThrow(()-> new EntityNotFoundException("없는 레스토랑"));
        List<MyPhotoRes> myPhotos = restaurantPhotoRepository.findByRestaurant(restaurant)
                .orElse(Collections.emptyList())  // Optional이 비어 있으면 빈 리스트 반환
                .stream()
                .map(photo -> MyPhotoRes.builder()  // RestaurantPhoto 객체를 MyPhotoRes로 변환
                        .photoId(photo.getId())
                        .photoUrl(photo.getPhotoUrl())
                        .build())
                .collect(Collectors.toList());

        return myPhotos;
    }
    public String deletePhoto(PhotoDeleteReq req){
        RestaurantPhoto photo = restaurantPhotoRepository.findById(req.getPhotoId()).orElseThrow(()->new EntityNotFoundException("없는 사진"));
        restaurantPhotoRepository.delete(photo);
        return "ok";
    }

    //염병하느니 이거 만드는게 훨나음
    public RestaurantMypage myPage() {
        String registrationNumber = SecurityContextHolder.getContext().getAuthentication().getName();

        Restaurant restaurant = restaurantRepository.findByRegistrationNumber(registrationNumber)
                .orElseThrow(() -> new EntityNotFoundException("해당 레스토랑을 찾을 수 없습니다."));

        return RestaurantMypage.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .email(restaurant.getEmail())
                .description(restaurant.getDescription())
                .phoneNumber(restaurant.getPhoneNumber())
                .address(restaurant.getAddress().getFullAddress())
                .restaurantType(restaurant.getRestaurantType().toString()) // Enum 처리
                .build();
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
                .map(info -> info.restaurantInfoListRes()).collect(Collectors.toList());
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
    public List<TopFavoriteRestaurantRes> getTopFavoriteRestaurants(int limit) {
        List<Restaurant> topRestaurants = restaurantRepository.findTopRestaurantsByRating(PageRequest.of(0, limit));
        return topRestaurants.stream().map(restaurant -> TopFavoriteRestaurantRes.builder()
                .rating(restaurant.getReviews().isEmpty() ? 0.0 : restaurant.getReviews().stream().mapToDouble(r -> r.getRating().getValue()).average().orElse(0.0))
                .restaurantId(restaurant.getId())
                .restaurantImage(restaurant.getPhotos().isEmpty() ? null : restaurant.getPhotos().get(0).getPhotoUrl())
                .restaurantName(restaurant.getName())
                .build()
        ).collect(Collectors.toList());
    }

    public List<CategoryRes> getCategories() {
        List<Restaurant> restaurants = restaurantRepository.findAll();

        return restaurants.stream()
                .map(Restaurant::getRestaurantType) // 레스토랑에서 카테고리만 추출
                .distinct() // 중복 제거
                .map(type -> CategoryRes.builder()
                        .categoryId((long) type.ordinal()) // Enum의 ordinal을 ID처럼 사용
                        .categoryName(type.name()) // Enum의 name()을 카테고리명으로 사용
                        .categoryIcon(null) // 아이콘 URL (추후 설정 가능)
                        .build())
                .collect(Collectors.toList());
    }


    public String addBookMark(AddBookMarkReq addBookMarkReq){
        String identify = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByIdentify(identify).orElseThrow(()->new EntityNotFoundException("없는 회원입니다."));
        Restaurant restaurant = restaurantRepository.findById(addBookMarkReq.getRestaurantId()).orElseThrow(()->new EntityNotFoundException("없는 레스토랑"));
        bookmarkRepository.save(Bookmark.builder().user(user).restaurant(restaurant).build());
        return "ok";
    }

    public String deleteBookMark(DeleteBookMarkReq deleteBookMarkReq){
        String identify = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByIdentify(identify).orElseThrow(()->new EntityNotFoundException("없는 회원입니다."));
        Restaurant restaurant = restaurantRepository.findById(deleteBookMarkReq.getRestaurantId()).orElseThrow(()->new EntityNotFoundException("없는 레스토랑"));
        bookmarkRepository.deleteByUserAndRestaurant(user,restaurant);
        return "ok";
    }

    public Page<MyBookMarkRes> myBookMark(Pageable pageable){
        String identify = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByIdentify(identify).orElseThrow(()->new EntityNotFoundException("없는 회원입니다."));
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "id")
        );

        Page<Bookmark> bookmarkPage = bookmarkRepository.findByUserAndRestaurant_RestaurantStatus(user, RestaurantStatus.ACTIVE, sortedPageable);

        return bookmarkPage.map(bookmark -> {
            Restaurant restaurant = bookmark.getRestaurant();
            String restaurantName = restaurant.getName();
            String restaurantPhoto = (restaurant.getPhotos() != null && !restaurant.getPhotos().isEmpty())
                    ? restaurant.getPhotos().get(0).getPhotoUrl()
                    : null;
            return MyBookMarkRes.builder()
                    .restaurantId(restaurant.getId())
                    .restaurantName(restaurantName)
                    .restaurantPhoto(restaurantPhoto)
                    .build();
        });
    }
    public boolean isBookMark(IsBookMarkReq isBookMarkReq){
        String identify = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByIdentify(identify).orElseThrow(()->new EntityNotFoundException("없는 회원입니다."));
        Restaurant restaurant = restaurantRepository.findById(isBookMarkReq.getRestaurantId()).orElseThrow(()->new EntityNotFoundException("없는 레스토랑입니다."));
        return bookmarkRepository.findByUserAndRestaurant(user, restaurant).isPresent();
    }
    public List<MenuResDto> getMenusByRestaurantId(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));
        List<Menu> menuList = restaurant.getMenus();

        if (menuList == null || menuList.isEmpty()) {
            return Collections.emptyList();
        }
        return menuList.stream().map(menu -> MenuResDto.builder()
                .id(menu.getId())
                .name(menu.getName())      // ✅ 메뉴 이름 추가
                .price(menu.getPrice())    // ✅ 가격 추가
                .description(menu.getDescription()) // ✅ 메뉴 설명 추가
                .menuPhoto(menu.getMenuPhoto()) // ✅ 메뉴 이미지 URL 추가
                .build()
        ).collect(Collectors.toList());
    }

    // 🔹 1. 레스토랑 목록 조회
    public List<RestaurantManageRes> getAllRestaurants() {
        List<RestaurantManageRes> restaurantList = restaurantRepository.findAll().stream()
                .map(r -> {
                    RestaurantManageRes dto = new RestaurantManageRes(
                            r.getId(),
                            r.getRestaurantStatus().toString(),
                            r.getEmail(),
                            r.getPhoneNumber(),
                            r.getName()
                    );
                    System.out.println("DTO 생성 확인: " + dto); // ✅ 로그 찍기
                    return dto;
                })
                .collect(Collectors.toList());

        System.out.println("최종 반환되는 리스트: " + restaurantList); // ✅ 로그 찍기
        return restaurantList;
    }

    // 🔹 2. 레스토랑 상태 업데이트
    public void updateRestaurantStatus(Long id, RestaurantStatusUpdateDto dto) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("레스토랑을 찾을 수 없습니다."));

        // 상태 업데이트
        restaurant.updateStatus(dto.getStatus());
        restaurantRepository.save(restaurant);
    }
}
