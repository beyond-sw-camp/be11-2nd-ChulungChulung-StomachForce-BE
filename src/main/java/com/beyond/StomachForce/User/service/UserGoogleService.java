package com.beyond.StomachForce.User.service;

import com.beyond.StomachForce.User.domain.User;
import com.beyond.StomachForce.User.dtos.AccessTokendto;
import com.beyond.StomachForce.User.dtos.GoogleProfileDto;
import com.beyond.StomachForce.User.dtos.UserSaveReq;
import com.beyond.StomachForce.User.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Service
public class UserGoogleService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Value("${oauth.google.client-id}")
    private String googleClientId;
    @Value("${oauth.google.client-secret}")
    private String googleClientSecret;
    @Value("${oauth.google.redirect-uri}")
    private String googleRedirectUri;

    public UserGoogleService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AccessTokendto getAccessToken(String code){
        RestClient restClient = RestClient.create();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code",code);
        params.add("client_id",googleClientId);
        params.add("client_secret",googleClientSecret);
        params.add("redirect_uri",googleRedirectUri);
        params.add("grant_type", "authorization_code");
        ResponseEntity<AccessTokendto> response = restClient.post()
                .uri("https://oauth2.googleapis.com/token")
                .header("Content-Type","application/x-www-form-urlencoded")
                .body(params)
                .retrieve()
                .toEntity(AccessTokendto.class);
        return response.getBody();
    }

    public GoogleProfileDto getGoogleProfile(String token){
        RestClient restClient = RestClient.create();
        ResponseEntity<GoogleProfileDto> response = restClient.post()
                .uri("https://openidconnect.googleapis.com/v1/userinfo")
                .header("Authorization","Bearer " +token)
                .retrieve()
                .toEntity(GoogleProfileDto.class);
        return response.getBody();
    }

    public User getUserByEmail(String email){
        User user = userRepository.findByEmail(email).orElse(null);
        return user;
    }
    public User createOauth(String socialId, String email, String name){
        String password = "12341234";
        UserSaveReq userSaveReq = UserSaveReq.builder()
                .identify(email)
                .password(password)
                .name(name)
                .nickName(socialId)
                .email(email)
                .phoneNumber("01012341234")
                .birth("990621")
                .build();
        String password1 = passwordEncoder.encode(password);
        User saveUser = userSaveReq.toEntity(password1);
        User finalUser = userRepository.save(saveUser);
        return finalUser;
    }
}
