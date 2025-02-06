package com.beyond.StomachForce.restaurant.controller;

import com.beyond.StomachForce.Common.Auth.JwtTokenProvider;
import com.beyond.StomachForce.restaurant.dtos.*;
import com.beyond.StomachForce.restaurant.entity.RestaurantRefreshDto;
import com.beyond.StomachForce.restaurant.repository.RestaurantRepository;
import com.beyond.StomachForce.restaurant.service.RestaurantService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/restaurant")
public class RestaurantController {
    private final RestaurantRepository restaurantRepository;
    private final RestaurantService restaurantService;
    @Value("${jwt.secretKeyRT}")
    private String secretKeyRt;


    public RestaurantController(RestaurantRepository restaurantRepository, RestaurantService restaurantService, JwtTokenProvider jwtTokenProvider, RedisTemplate<String, Object> redisTemplate) {
        this.restaurantRepository = restaurantRepository;
        this.restaurantService = restaurantService;
    }

    @PostMapping("/create")// 회원가입
    public String authorCreate(@Valid @RequestBody RestaurantCreateReq restaurantCreateReq) {
        restaurantService.save(restaurantCreateReq);
        return "OK";
    }
    @PostMapping("/doLogin")
    public ResponseEntity<?> doLogin(@RequestBody LoginDto dto) {
        Map<String, Object> loginInfo = restaurantService.login(dto);
        return new ResponseEntity<>(loginInfo, HttpStatus.OK);
    }
    @PostMapping("/refresh-token")
    public ResponseEntity<?> generateNewAt(@RequestBody RestaurantRefreshDto dto) {
        try {
            String newToken = restaurantService.refreshAccessToken(dto, secretKeyRt);
            Map<String, Object> loginInfo = new HashMap<>();
            loginInfo.put("token", newToken);
            return new ResponseEntity<>(loginInfo, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/list")// 레스토랑 사람들 리스트로 뽑기
    public ResponseEntity<?> list() {
        List<RestaurantListRes> restaurantListResList = restaurantService.findAll();
        return new ResponseEntity<>(restaurantListResList, HttpStatus.OK);
    }

    @GetMapping("/detailRegistration")//
    public RestaurantDetailRes restaurantDetailRegistration (@RequestBody String registrationNumber) {
        return restaurantService.findByRegistrationNumber(registrationNumber);
    }

    @GetMapping("/detailEmail")
    public RestaurantDetailRes restaurantDetailResEmail (@RequestBody String email) {
        return restaurantService.findByEmail(email);
    }

    @PatchMapping("/update/{email}")
    public String authorUpdate(@PathVariable String email, @RequestBody RestaurantUpdateReq dto){
        restaurantService.update(email,dto);
        return "Id is successfully updated";
    }

    @PostMapping("/bookmark/{id}")
    public ResponseEntity<?> authorBookmark(@PathVariable Long id){
        restaurantService.toggleBookmark(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/detail/photos/{restaurantId}")
    public ResponseEntity<List<String>> getRestaurantPhotos(@PathVariable Long restaurantId) {
        List<String> photos = restaurantService.findPhotosByRestaurantId(restaurantId);
        return ResponseEntity.ok(photos);
    }


}
