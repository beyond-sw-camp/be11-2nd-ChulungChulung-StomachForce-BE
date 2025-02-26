package com.beyond.StomachForce.restaurant.controller;

import com.beyond.StomachForce.Common.dtos.CommonDto;
import com.beyond.StomachForce.User.dtos.UserInfoRes;
import com.beyond.StomachForce.User.dtos.UserSearchDto;

import com.beyond.StomachForce.User.dtos.MypageRes;
import com.beyond.StomachForce.menu.dto.MenuResDto;

import com.beyond.StomachForce.restaurant.domain.Restaurant;
import com.beyond.StomachForce.restaurant.domain.RestaurantInfo;
import com.beyond.StomachForce.restaurant.dtos.*;

import com.beyond.StomachForce.restaurant.dtos.forLogin.LoginDto;
import com.beyond.StomachForce.restaurant.dtos.forLogin.RestaurantRefreshDto;
import com.beyond.StomachForce.restaurant.dtos.forRestaurantInfo.RestaurantInfoCreateReq;
import com.beyond.StomachForce.restaurant.dtos.forRestaurantInfo.RestaurantInfoListRes;
import com.beyond.StomachForce.restaurant.dtos.forRestaurantInfo.RestaurantInfoUpdateReq;
import com.beyond.StomachForce.restaurant.service.RestaurantService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/restaurant")
public class RestaurantController {
    private final RestaurantService restaurantService;
    @Value("${jwt.secretKeyRT}")
    private String secretKeyRt;


    public RestaurantController( RestaurantService restaurantService) {

        this.restaurantService = restaurantService;
    }

    @PostMapping("/create")// 회원가입
    public String authorCreate(@Valid RestaurantCreateReq restaurantCreateReq) {
        restaurantService.save(restaurantCreateReq);
        return "OK";
    }
    @PostMapping("/login")
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
    public ResponseEntity<?> list(Pageable pageable, @ModelAttribute RestaurantSearchDto dto) {
        System.out.println("Received Name: " + dto.getName());
        System.out.println("Received location: " + dto.getAddress());

        Page<RestaurantListRes> restaurantListResList = restaurantService.findAll(pageable, dto);
        return new ResponseEntity<>(restaurantListResList, HttpStatus.OK);
    }


    @GetMapping("/detail/{id}")//
    public RestaurantDetailRes restaurantDetail (@PathVariable Long id) {
        return restaurantService.findById(id);
    }

//    @GetMapping("/mypage")
//    public ResponseEntity<?> getMyRestaurantInfo() {
//        String registrationNumber = SecurityContextHolder.getContext().getAuthentication().getName();
//        Restaurant restaurant = restaurantRepository.findByRegistrationNumber(registrationNumber)
//                .orElseThrow(() -> new EntityNotFoundException("해당 레스토랑을 찾을 수 없습니다."));
//
//        Map<String, Object> restaurantInfo = new HashMap<>();
//        restaurantInfo.put("id", restaurant.getId());
//        restaurantInfo.put("name", restaurant.getName());
//        restaurantInfo.put("email", restaurant.getEmail());
//        restaurantInfo.put("description", restaurant.getDescription());
//
//        return ResponseEntity.ok(restaurantInfo);
//    }



    @GetMapping("/mypage")
    public ResponseEntity<?> myPage(){
        RestaurantMypage myPageRes = restaurantService.myPage();
        return new ResponseEntity<>(myPageRes,HttpStatus.OK);
    }

    @PatchMapping("/update")
    public ResponseEntity<?> authorUpdate(@Valid @RequestBody RestaurantUpdateReq dto){
        restaurantService.update(dto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/delete")
    public ResponseEntity<?> restaurantDelete() {
        restaurantService.delete();
        return new ResponseEntity<>(new CommonDto(HttpStatus.OK.value(),"deletion success","success"),HttpStatus.OK);
    }

    @GetMapping("/photos")
    public ResponseEntity<?> findMyPhotos() {
        List<MyPhotoRes> response = restaurantService.findMyPhotos();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PostMapping("/photoDelete")
    public ResponseEntity<?> deletePhoto(@Valid @RequestBody PhotoDeleteReq photoDeleteReq) {
        String response = restaurantService.deletePhoto(photoDeleteReq);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PostMapping("/photoAdd")
    public ResponseEntity<?> photoUpdate(RestaurantPhotoAdd dto) throws IOException {
        String response = restaurantService.addPhoto(dto);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    // info 관련 CRUD ------------------------------------------------------------------------------------
    @PostMapping("/info/create/{id}")
    private ResponseEntity<?> restaurantInfo(@PathVariable Long id, @RequestBody RestaurantInfoCreateReq req) {
        restaurantService.infoCreate(id, req);
        return new ResponseEntity<>(restaurantService.findById(id), HttpStatus.OK);
    }


    // 상단 5개만 노출되는 list 나머지는 inactive
    @GetMapping("/info/list/{id}")
    public ResponseEntity<?> infoList(@PathVariable Long id) {
        List<RestaurantInfoListRes> restaurantInfoListResList = restaurantService.findInfoAll(id);
        return new ResponseEntity<>(restaurantInfoListResList, HttpStatus.OK);
    }

    @PatchMapping("/info/update/{id}")
    public ResponseEntity<?> infoUpdate(@PathVariable Long id, @RequestBody RestaurantInfoUpdateReq dto){
        restaurantService.infoUpdate(id,dto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/info/{id}/delete")
    public ResponseEntity<?> restaurantInfoDelete(@PathVariable Long id) {
        Long deleteInfoId = restaurantService.infoDelete(id);
        return new ResponseEntity<>(deleteInfoId,HttpStatus.OK);
    }

//    @PostMapping("/bookmark/{id}")
//    public ResponseEntity<?> authorBookmark(@PathVariable Long id){
//        restaurantService.toggleBookmark(id);
//        return new ResponseEntity<>(HttpStatus.OK);
//    }

    @GetMapping("/detail/photos/{restaurantId}")
    public ResponseEntity<List<String>> getRestaurantPhotos(@PathVariable Long restaurantId) {
        List<String> photos = restaurantService.findPhotosByRestaurantId(restaurantId);
        return ResponseEntity.ok(photos);
    }
    @GetMapping("/top-favorites")
    public ResponseEntity<List<TopFavoriteRestaurantRes>> getTopFavoriteRestaurants(@RequestParam(defaultValue = "10") int limit) {
        List<TopFavoriteRestaurantRes> response = restaurantService.getTopFavoriteRestaurants(limit);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryRes>> getCategories() {
        return ResponseEntity.ok(restaurantService.getCategories());
    }

    @PostMapping("/addBookMark")
    public ResponseEntity<?> addBookMark(@Valid @RequestBody AddBookMarkReq addBookMarkReq) {
        String response = restaurantService.addBookMark(addBookMarkReq);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PostMapping("/deleteBookMark")
    public ResponseEntity<?> deleteBookMark(@Valid @RequestBody DeleteBookMarkReq deleteBookMarkReq) {
        String response = restaurantService.deleteBookMark(deleteBookMarkReq);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @GetMapping("/myBookMark")
    public ResponseEntity<?> myBookMark(Pageable pageable) {
        Page<MyBookMarkRes> response = restaurantService.myBookMark(pageable);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PostMapping("/isBookMark")
    public ResponseEntity<?> isBookMark(@Valid @RequestBody IsBookMarkReq isBookMarkReq) {
        boolean response = restaurantService.isBookMark(isBookMarkReq);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{restaurantId}/menus")
    public ResponseEntity<List<MenuResDto>> getRestaurantMenus(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(restaurantService.getMenusByRestaurantId(restaurantId));
    }
    // 🔹 1. 전체 레스토랑 목록 조회 API
    @GetMapping("/listmanage")
    public ResponseEntity<List<RestaurantManageRes>> getAllRestaurants() {
        List<RestaurantManageRes> restaurantList = restaurantService.getAllRestaurants();
        return ResponseEntity.ok(restaurantList);
    }

    // 🔹 2. 레스토랑 상태 변경 API
    @PatchMapping("/update/status/{id}")
    public ResponseEntity<String> updateRestaurantStatus(
            @PathVariable Long id,
            @RequestBody RestaurantStatusUpdateDto dto) {
        restaurantService.updateRestaurantStatus(id, dto);
        return ResponseEntity.ok("레스토랑 상태가 변경되었습니다.");
    }
}



