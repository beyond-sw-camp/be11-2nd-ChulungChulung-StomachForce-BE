package com.beyond.StomachForce.User.controller;

import com.beyond.StomachForce.Common.dtos.StatusCode;
import com.beyond.StomachForce.Common.Auth.JwtTokenProvider;
import com.beyond.StomachForce.User.domain.Enum.BlockUser;
import com.beyond.StomachForce.User.domain.Mileage;
import com.beyond.StomachForce.User.domain.User;
import com.beyond.StomachForce.User.domain.VipBenefit;
import com.beyond.StomachForce.User.dtos.*;
import com.beyond.StomachForce.User.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @Qualifier("rtdb")
    private final RedisTemplate<String,Object> redisTemplate;

    @Value("${jwt.secretKeyRT}")
    private String secretKeyRT;

    public UserController(UserService userService, JwtTokenProvider jwtTokenProvider, @Qualifier("rtdb") RedisTemplate<String, Object> redisTemplate) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisTemplate = redisTemplate;
    }


    @PostMapping("/create")
    public ResponseEntity<?> postCreatePost(@Valid @RequestBody UserSaveReq userSaveReq) {
        User user = userService.save(userSaveReq);
        return new ResponseEntity<>(new StatusCode(HttpStatus.CREATED.value(),
                "회원가입이 완료되었습니다.",user.getId()),HttpStatus.CREATED);
    }

    @PostMapping("/profile")
    public ResponseEntity<?> profile(@Valid ProfileReq profileReq) throws IOException {
        String response = userService.profile(profileReq);
        return new ResponseEntity<>(response,HttpStatus.CREATED);
    }

    @PatchMapping("/update")
    public ResponseEntity<?> userUpdate(UserUpdateReq userUpdateReq) throws IOException {
        userService.updateByIdentify(userUpdateReq);
        return new ResponseEntity<>(new StatusCode(HttpStatus.OK.value(),
                "회원정보가 수정되었습니다.","ok"),HttpStatus.OK);
    }

    @PatchMapping("/stop")
    public ResponseEntity<?> delete(){
        userService.quit();
        return new ResponseEntity<>(new StatusCode(HttpStatus.OK.value(),
                "회원탈퇴가 완료되었습니다.","ok"),HttpStatus.OK);
    }

    @PostMapping("/doLogin")
    public ResponseEntity<?> doLogin(@RequestBody LoginDto dto){
        User user = userService.login(dto);
        String token = jwtTokenProvider.createToken(user.getIdentify() ,user.getRole().toString());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getIdentify() ,user.getRole().toString());
        redisTemplate.opsForValue().set(user.getIdentify(),refreshToken, 200, TimeUnit.DAYS);

        Map<String, Object> loginInfo = new HashMap<>();
        loginInfo.put("id",user.getId());
        loginInfo.put("token",token);
        loginInfo.put("refreshToken",refreshToken);
        return new ResponseEntity<>(loginInfo,HttpStatus.OK);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> generateNewAt(@RequestBody UserRefreshDto dto){

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKeyRT)
                .build()
                .parseClaimsJws(dto.getRefreshToken())
                .getBody();

        Object rt = redisTemplate.opsForValue().get(claims.getSubject());
        if(rt == null || rt.toString().equals(dto.getRefreshToken())){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        String token = jwtTokenProvider.createToken(claims.getSubject(),claims.get("role").toString());
        Map<String, Object> loginInfo = new HashMap<>();
        loginInfo.put("token",token);
        return new ResponseEntity<>(loginInfo,HttpStatus.OK);
    }
    @PostMapping("/manageMileage")
    public ResponseEntity<?> Mileage(@Valid @RequestBody ManageMileageDto manageMileage){
        Mileage mileage = userService.mangeMileage(manageMileage);
        return new ResponseEntity<>(new StatusCode(HttpStatus.CREATED.value(),
                "마일리지 처리가 완료되었습니다.",mileage.getId()),HttpStatus.CREATED);
    }

    @PostMapping("/follow")
    public ResponseEntity<?> Follow(@RequestBody FollowReq followReq){
        String response = userService.follow(followReq);
        return new ResponseEntity<>(new StatusCode(HttpStatus.CREATED.value(),
                "",response),HttpStatus.CREATED);
    }

    @GetMapping("/followingList")
    public ResponseEntity<?> FollowingList(){
        List<FollowingListRes> response = userService.follwings();
        return new ResponseEntity<>(response,HttpStatus.CREATED);
    }

    @GetMapping("/followerList")
    public ResponseEntity<?> FollowerList(){
        List<FollowerListRes> response = userService.follwers();
        return new ResponseEntity<>(response,HttpStatus.CREATED);
    }

    @GetMapping("/userInfo")
    public ResponseEntity<?> userInfo(){
        UserInfoRes response = userService.userInfo();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @GetMapping("/myPage")
    public ResponseEntity<?> myPage(Pageable pageable){
        MypageRes response = userService.myPage(pageable);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @GetMapping("/findUser")
    public ResponseEntity<?> findUser(Pageable pageable,UserSearchDto userSearchDto){
        Page<UserInfoRes> response= userService.findUser(pageable,userSearchDto);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @GetMapping("/yourPage")
    public ResponseEntity<?> yourPage(Pageable pageable, UserSearchDto userSearchDto){
        YourPageRes response = userService.yourPage(pageable,userSearchDto);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @GetMapping("/myVip")
    public ResponseEntity<?> myVip(Pageable pageable){
        Page<VipBenefitRes> response = userService.myVip(pageable);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PostMapping("/vipBenefitRegist")
    public ResponseEntity<?> vipBenefitRegist(VipBenefitRegistDto vipBenefitRegistDto) throws IOException {
        VipBenefit response = userService.vipBenefitRegist(vipBenefitRegistDto);
        return new ResponseEntity<>(new StatusCode(HttpStatus.CREATED.value(),
                "혜택이 등록되었습니다.",response),HttpStatus.CREATED);
    }

    @PostMapping("/block")
    public ResponseEntity<?> block(@Valid @RequestBody UserBlockingDto userBlockingDto) {
        BlockUser response = userService.blocking(userBlockingDto);
        return new ResponseEntity<>(new StatusCode(HttpStatus.CREATED.value(),
                "차단되었습니다.",response),HttpStatus.OK);
    }

    @PostMapping("/isblocked")
    public ResponseEntity<?> isblocked(@Valid @RequestBody UserBlockingDto userBlockingDto) {
        boolean[] response = userService.isBlockedBy(userBlockingDto);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PostMapping("/unblock")
    public ResponseEntity<?> unblock(@Valid @RequestBody UserBlockingDto userBlockingDto) {
        BlockUser response = userService.unblockUser(userBlockingDto);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @GetMapping("/blockedList")
    public ResponseEntity<?> blockedList() {
        List<BlockedUserRes> response = userService.blockedUsers();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}
