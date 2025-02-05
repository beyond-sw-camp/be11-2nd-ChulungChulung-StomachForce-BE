package com.beyond.StomachForce.youngjae.restaurant.service;

import com.beyond.StomachForce.youngjae.common.auth.JwtTokenProvider;
import com.beyond.StomachForce.youngjae.restaurant.dtos.*;
import com.beyond.StomachForce.youngjae.restaurant.entity.Bookmark;
import com.beyond.StomachForce.youngjae.restaurant.entity.Restaurant;
import com.beyond.StomachForce.youngjae.restaurant.entity.RestaurantPhoto;
import com.beyond.StomachForce.youngjae.restaurant.entity.RestaurantRefreshDto;
import com.beyond.StomachForce.youngjae.restaurant.entity.select.BookmarkType;
import com.beyond.StomachForce.youngjae.restaurant.repository.BookmarkRepository;
import com.beyond.StomachForce.youngjae.restaurant.repository.RestaurantRepository;
import com.beyond.StomachForce.youngjae.review.repository.ReviewRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Transactional
public class RestaurantService {

    @Autowired
    private final RestaurantRepository restaurantRepository;
    private final ReviewRepository reviewRepository;
    private final BookmarkRepository bookmarkRepository;
    private final PasswordEncoder passwordEncoder;
    // 로그인에 아래 두개 필요함,
    private final JwtTokenProvider jwtTokenProvider;
    @Qualifier("rtdb")
    private final RedisTemplate<String, Object> redisTemplate;


    public RestaurantService(RestaurantRepository restaurantRepository, ReviewRepository reviewRepository, BookmarkRepository bookmarkRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider, RedisTemplate<String, Object> redisTemplate) {
        this.restaurantRepository = restaurantRepository;
        this.reviewRepository = reviewRepository;
        this.bookmarkRepository = bookmarkRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisTemplate = redisTemplate;
    }

    public List<RestaurantListRes> findAll(){
        return restaurantRepository.findAll().stream().map
                (r->r.listDtoFromEntity()).collect(Collectors.toList());
    }

    public Long save(RestaurantCreateReq restaurantCreateReq){

        if(restaurantRepository.findByEmail(restaurantCreateReq.getEmail()).isPresent()){
            throw new IllegalArgumentException("email already exists");
        }
        if(restaurantCreateReq.getPassword().length()<8){
            throw new IllegalArgumentException("비번 너무 짧아요");
        }
        String password = passwordEncoder.encode(restaurantCreateReq.getPassword());
        Restaurant restaurant = restaurantRepository.save(restaurantCreateReq.toEntity(password));
        return restaurant.getId();

    }

    public Map<String, Object> login(LoginDto dto){
        // 사업자등록증 여부 확인
       Restaurant restaurant = restaurantRepository.findByEmail(dto.getRegistrationNumber())
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



    public void update(String email, RestaurantUpdateReq restaurantUpdateReq){
        Restaurant restaurant = restaurantRepository.findByEmail(email)
                .orElseThrow(()-> new EntityNotFoundException("없는 이메일입니다."));

        restaurant.updateProfile(restaurantUpdateReq);
        restaurantRepository.save(restaurant);
    }

    //사업자번호로 디테일 화면 확인
    public RestaurantDetailRes findByRegistrationNumber(String registrationNumber){
        Restaurant restaurant = restaurantRepository.findByRegistrationNumber(registrationNumber)
                .orElseThrow(()-> new EntityNotFoundException("Restaurant with registration number " + registrationNumber + " not found"));
        return restaurant.detailFromEntity();
    }

    //이메일로 디테일 화면 확인
    public RestaurantDetailRes findByEmail (String email){
        Restaurant restaurant = restaurantRepository.findByEmail(email)
                .orElseThrow(()-> new EntityNotFoundException("Restaurant with email " + email + " not found"));
        return restaurant.detailFromEntity();
    }

    //id로 사진 찾는 메서드(레스토랑 아이디 활용)
    public List<String> findPhotosByRestaurantId(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));

        return restaurant.getPhotos().stream().map(RestaurantPhoto::getPhotoUrl).collect(Collectors.toList());
    }

    //북마크 (토글)
    public void toggleBookmark(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));

        Optional<Bookmark> bookmark = bookmarkRepository.findById(restaurantId);

        if (bookmark.isPresent()) {
            bookmarkRepository.delete(bookmark.get()); // 즐겨찾기 삭제
        } else {
            Bookmark newBookmark = Bookmark.builder().restaurant(restaurant).bookmarkType(BookmarkType.YES).build();
            bookmarkRepository.save(newBookmark); // 즐겨찾기 추가
        }
    }
}
