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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MenuService {
    private final MenuRepository menuRepository;
    private final RestaurantRepository restaurantRepository;

    public MenuService(MenuRepository menuRepository, RestaurantRepository restaurantRepository) {
        this.menuRepository = menuRepository;
        this.restaurantRepository = restaurantRepository;
    }

    public MenuResDto menuCreate(MenuCreateDto dto){
        Restaurant restaurant = restaurantRepository.findById(dto.getRestaurantId())
                .orElseThrow(()-> new EntityNotFoundException("없는 레스토랑회원입니다."));

        AllergyInfo allergyInfo = dto.getAllergyInfo().toEntity();

        Menu menu = Menu.builder()
                .restaurant(restaurant)
                .name(dto.getName())
                .price(dto.getPrice())
                .description(dto.getDescription())
                .menuPhoto(dto.getMenuPhoto())
                .allergyInfo(allergyInfo)
                .build();

        menuRepository.save(menu);
        return new MenuResDto(menu);
    }

    public List<MenuListResDto> getMenuList(Long restaurantId){
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(()->new EntityNotFoundException("없는 레스토랑회원입니다."));

        return menuRepository.findByRestaurant(restaurant).stream()
                .map(MenuListResDto::new)
                .collect(Collectors.toList());
    }

    public MenuResDto updateMenu(Long menuId, MenuUpdateDto dto) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new EntityNotFoundException("해당 메뉴가 존재하지 않습니다."));

        if (dto.getName() != null) menu.setName(dto.getName());
        if (dto.getPrice() != null) menu.setPrice(dto.getPrice());
        if (dto.getDescription() != null) menu.setDescription(dto.getDescription());
        if (dto.getMenuPhoto() != null) menu.setMenuPhoto(dto.getMenuPhoto());

        return new MenuResDto(menu);
    }


}
