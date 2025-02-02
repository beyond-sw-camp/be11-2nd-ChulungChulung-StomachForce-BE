package com.beyond.StomachForce.youngjae.restaurant.controller;

import com.beyond.StomachForce.youngjae.restaurant.dtos.RestaurantCreateReq;
import com.beyond.StomachForce.youngjae.restaurant.dtos.RestaurantDetailRes;
import com.beyond.StomachForce.youngjae.restaurant.dtos.RestaurantListRes;
import com.beyond.StomachForce.youngjae.restaurant.dtos.RestaurantUpdateReq;
import com.beyond.StomachForce.youngjae.restaurant.repository.RestaurantRepository;
import com.beyond.StomachForce.youngjae.restaurant.service.RestaurantService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/restaurant")
public class RestaurantController {
    private final RestaurantRepository restaurantRepository;
    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantRepository restaurantRepository, RestaurantService restaurantService) {
        this.restaurantRepository = restaurantRepository;
        this.restaurantService = restaurantService;
    }

    @PostMapping("/create")
    public String authorCreate(@Valid @RequestBody RestaurantCreateReq restaurantCreateReq) {
        restaurantService.save(restaurantCreateReq);
        return "OK";
    }
    @GetMapping("/list")
    public ResponseEntity<?> list() {
        List<RestaurantListRes> restaurantListResList = restaurantService.findAll();
        return new ResponseEntity<>(restaurantListResList, HttpStatus.OK);
    }

    @GetMapping("/detailRegistration")
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
