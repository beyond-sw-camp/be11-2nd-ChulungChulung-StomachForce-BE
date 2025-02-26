package com.beyond.StomachForce.menu.service;

import com.beyond.StomachForce.menu.domain.AllergyInfo;
import com.beyond.StomachForce.menu.domain.Menu;
import com.beyond.StomachForce.menu.dto.MenuCreateDto;
import com.beyond.StomachForce.menu.dto.MenuListResDto;
import com.beyond.StomachForce.menu.dto.MenuResDto;
import com.beyond.StomachForce.menu.dto.MenuUpdateDto;
import com.beyond.StomachForce.menu.repository.MenuRepository;
import com.beyond.StomachForce.restaurant.domain.Restaurant;
import com.beyond.StomachForce.restaurant.repository.RestaurantRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MenuService {
    private final MenuRepository menuRepository;
    private final RestaurantRepository restaurantRepository;
    private final S3Client s3Client;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public MenuService(MenuRepository menuRepository, RestaurantRepository restaurantRepository, S3Client s3Client) {
        this.menuRepository = menuRepository;
        this.restaurantRepository = restaurantRepository;
        this.s3Client = s3Client;
    }

    public MenuResDto menuCreate(MenuCreateDto dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String registrationNumber = authentication.getName();

        Restaurant restaurant = restaurantRepository.findByRegistrationNumber(registrationNumber)
                .orElseThrow(() -> new EntityNotFoundException("레스토랑 정보를 찾을 수 없습니다."));

        if (!restaurant.getId().equals(dto.getRestaurantId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "해당 레스토랑의 메뉴를 추가할 권한이 없습니다.");
        }

        AllergyInfo allergyInfo = dto.getAllergyInfo().toEntity();

        // 메뉴 사진이 있는 경우에만 업로드
        String menuPhotoUrl = null;
        if (dto.getMenuPhoto() != null) {
            menuPhotoUrl = uploadImageToS3(dto.getMenuPhoto(), "menu_photos");
        }

        Menu menu = Menu.builder()
                .restaurant(restaurant)
                .name(dto.getName())
                .price(dto.getPrice())
                .description(dto.getDescription())
                .menuPhoto(menuPhotoUrl)
                .allergyInfo(allergyInfo)
                .build();

        menuRepository.save(menu);
        return new MenuResDto(menu);
    }

    private String uploadImageToS3(MultipartFile image, String folder) {
        try {
            byte[] bytes = image.getBytes();
            String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename(); // 파일명만 생성

            // 로컬 저장 경로 설정 (폴더 중복 방지)
            Path dirPath = Paths.get("C:/Users/Playdata/Desktop/testFolder", folder);
            Path filePath = dirPath.resolve(fileName); // 폴더 + 파일명 조합

            // 폴더가 없으면 생성
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            // 파일을 로컬에 저장
            Files.write(filePath, bytes, StandardOpenOption.CREATE, StandardOpenOption.WRITE);

            // S3 업로드 요청 객체 생성
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(folder + "/" + fileName) // S3에서는 폴더명과 함께 저장
                    .build();

            // S3에 업로드 실행
            s3Client.putObject(putObjectRequest, RequestBody.fromFile(filePath.toFile()));

            // 업로드된 S3 URL 반환
            return s3Client.utilities().getUrl(a -> a.bucket(bucket).key(folder + "/" + fileName)).toExternalForm();
        } catch (IOException e) {
            throw new RuntimeException("이미지 업로드 실패", e);
        }
    }

    public List<MenuListResDto> getMenuList(Long restaurantId){
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(()->new EntityNotFoundException("없는 레스토랑회원입니다."));

        return menuRepository.findByRestaurant(restaurant).stream()
                .map(MenuListResDto::new)
                .collect(Collectors.toList());
    }

    public MenuResDto updateMenu(Long menuId, MenuUpdateDto dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String registrationNumber = authentication.getName();

        Restaurant restaurant = restaurantRepository.findByRegistrationNumber(registrationNumber)
                .orElseThrow(() -> new EntityNotFoundException("레스토랑 정보를 찾을 수 없습니다."));

        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new EntityNotFoundException("해당 메뉴가 존재하지 않습니다."));

        if (!menu.getRestaurant().getId().equals(restaurant.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "해당 레스토랑의 메뉴를 수정할 권한이 없습니다.");
        }

        if (dto.getName() != null) menu.setName(dto.getName());
        if (dto.getPrice() != null) menu.setPrice(dto.getPrice());
        if (dto.getDescription() != null) menu.setDescription(dto.getDescription());

        // 새로운 메뉴 사진이 있는 경우에만 업데이트
        if (dto.getMenuPhoto() != null) {
            String menuPhotoUrl = uploadImageToS3(dto.getMenuPhoto(), "menu_photos");
            menu.setMenuPhoto(menuPhotoUrl);
        }

        // 알레르기 정보 업데이트
        AllergyInfo allergyInfo = menu.getAllergyInfo();
        AllergyInfo dtoAllergyInfo = dto.getAllergyInfo();

        if (allergyInfo != null && dtoAllergyInfo != null) {
            if (dtoAllergyInfo.getMilk() != null) allergyInfo.setMilk(dtoAllergyInfo.getMilk());
            if (dtoAllergyInfo.getEgg() != null) allergyInfo.setEgg(dtoAllergyInfo.getEgg());
            if (dtoAllergyInfo.getWheat() != null) allergyInfo.setWheat(dtoAllergyInfo.getWheat());
            if (dtoAllergyInfo.getSoy() != null) allergyInfo.setSoy(dtoAllergyInfo.getSoy());
            if (dtoAllergyInfo.getPeanut() != null) allergyInfo.setPeanut(dtoAllergyInfo.getPeanut());
            if (dtoAllergyInfo.getNuts() != null) allergyInfo.setNuts(dtoAllergyInfo.getNuts());
            if (dtoAllergyInfo.getFish() != null) allergyInfo.setFish(dtoAllergyInfo.getFish());
            if (dtoAllergyInfo.getShellfish() != null) allergyInfo.setShellfish(dtoAllergyInfo.getShellfish());
        }

        return new MenuResDto(menu);
    }

    public void deleteMenu(Long menuId) {
        // 현재 로그인한 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String registrationNumber = authentication.getName(); // JWT에서 registrationNumber 추출

        // 사업자 등록번호로 레스토랑 조회
        Restaurant restaurant = restaurantRepository.findByRegistrationNumber(registrationNumber)
                .orElseThrow(() -> new EntityNotFoundException("레스토랑 정보를 찾을 수 없습니다."));

        // 삭제할 메뉴 가져오기
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new EntityNotFoundException("해당 메뉴가 존재하지 않습니다."));

        // 메뉴의 restaurantId와 로그인한 사용자의 restaurantId가 일치하는지 검증
        if (!menu.getRestaurant().getId().equals(restaurant.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "해당 레스토랑의 메뉴를 삭제할 권한이 없습니다.");
        }

        // S3에 저장된 메뉴 사진 삭제
        if (menu.getMenuPhoto() != null) {
            deleteImageFromS3(menu.getMenuPhoto());
        }

        // 메뉴 삭제
        menuRepository.delete(menu);
    }

    // S3에서 이미지 삭제 메서드
    private void deleteImageFromS3(String photoUrl) {
        try {
            // S3에서 파일 키 추출
            String key = photoUrl.substring(photoUrl.lastIndexOf("/") + 1);

            // S3 객체 삭제 요청
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key("menu_photos/" + key) // 저장된 폴더 경로와 파일명 결합
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
        } catch (Exception e) {
            throw new RuntimeException("S3 이미지 삭제 실패", e);
        }
    }

}
