package com.beyond.StomachForce.User.service;

import com.beyond.StomachForce.User.domain.User;
import com.beyond.StomachForce.User.domain.UserAddress;
import com.beyond.StomachForce.User.dtos.LoginDto;
import com.beyond.StomachForce.User.dtos.UserUpdateReq;
import com.beyond.StomachForce.User.dtos.UserSaveReq;
import com.beyond.StomachForce.User.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User save(UserSaveReq userSaveReq) throws IllegalArgumentException{
        if(userRepository.findByName(userSaveReq.getName()).isPresent()){
            if(userRepository.findByBirth(userSaveReq.getBirth()).isPresent()){
                throw new IllegalArgumentException("이미 가입된 회원입니다.");
            }
        }
        String password = passwordEncoder.encode(userSaveReq.getPassword());
        User user = userSaveReq.toEntity(password);
//        User tempUser = User.builder().name(userSaveReq.getName()).build();
        String state = userSaveReq.getUserAddress().getState();
        String city = userSaveReq.getUserAddress().getCity();
        String village = userSaveReq.getUserAddress().getVillage();
        UserAddress userAddress  = UserAddress.builder().state(state).city(city).village(village).user(user).build();
        user.getUserAddresses().add(userAddress);
        User finalUser = userRepository.save(user);
        return finalUser;
    }
    public void updateByIdentify(UserUpdateReq userUpdateReq){
        String identify = userUpdateReq.getIdentify();
        User user = userRepository.findByIdentify(identify).orElseThrow(()->new EntityNotFoundException("없는 id입니다"));
        user.updateUser(userUpdateReq);
    }

    public void quit(String identify){
        User user = userRepository.findByIdentify(identify).orElseThrow(()->new EntityNotFoundException("없는 사람입니다."));
        user.userStop();
    }

    public User login(LoginDto dto){
        boolean check = true;
        Optional<User> optionalUser = userRepository.findByIdentify(dto.getIdentify());
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
    }
}
