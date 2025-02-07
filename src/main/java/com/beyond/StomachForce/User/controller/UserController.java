package com.beyond.StomachForce.User.controller;

import com.beyond.StomachForce.Common.dtos.StatusCode;
import com.beyond.StomachForce.Common.Auth.JwtTokenProvider;
import com.beyond.StomachForce.User.domain.User;
import com.beyond.StomachForce.User.dtos.LoginDto;
import com.beyond.StomachForce.User.dtos.UserRefreshDto;
import com.beyond.StomachForce.User.dtos.UserUpdateReq;
import com.beyond.StomachForce.User.dtos.UserSaveReq;
import com.beyond.StomachForce.User.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
    public ResponseEntity<?> postCreatePost(@Valid @RequestBody UserSaveReq userSaveReq){
        User user = userService.save(userSaveReq);
        return new ResponseEntity<>(new StatusCode(HttpStatus.CREATED.value(),
                "회원가입이 완료되었습니다.",user.getId()),HttpStatus.CREATED);
    }

    @PatchMapping("/update")
    public ResponseEntity<?> userUpdate(@Valid @RequestBody UserUpdateReq userUpdateReq){
        userService.updateByIdentify(userUpdateReq);
        return new ResponseEntity<>(new StatusCode(HttpStatus.OK.value(),
                "회원정보가 수정되었습니다.","ok"),HttpStatus.OK);
    }

    @PatchMapping("/stop")
    public ResponseEntity<?> delete(@Valid String identify){
        userService.quit(identify);
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
}
