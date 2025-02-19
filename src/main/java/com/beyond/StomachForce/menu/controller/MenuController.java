package com.beyond.StomachForce.menu.controller;

import com.beyond.StomachForce.Common.Auth.JwtTokenProvider;
import com.beyond.StomachForce.menu.domain.Menu;
import com.beyond.StomachForce.menu.dto.MenuListResDto;
import com.beyond.StomachForce.menu.dto.MenuResDto;
import com.beyond.StomachForce.menu.dto.MenuCreateDto;
import com.beyond.StomachForce.menu.dto.MenuUpdateDto;
import com.beyond.StomachForce.menu.service.MenuService;
import io.jsonwebtoken.Claims;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/menu")
public class MenuController {
    private final MenuService menuService;
    private final JwtTokenProvider jwtTokenProvider;

    public MenuController(MenuService menuService, JwtTokenProvider jwtTokenProvider) {
        this.menuService = menuService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/create")
    public ResponseEntity<?> menuCreate(@RequestBody MenuCreateDto dto){
        MenuResDto menu = menuService.menuCreate(dto);
        return new ResponseEntity<>(menu.getId(), HttpStatus.CREATED);
    }

    @GetMapping("/list/{restaurantId}")
    public ResponseEntity<?> getMenuList(@PathVariable Long restaurantId) {
        List<MenuListResDto> menuList = menuService.getMenuList(restaurantId);
        return new ResponseEntity<>(menuList, HttpStatus.OK);
    }

    @PatchMapping("/update/{menuId}")
    public ResponseEntity<?> updateMenu(@PathVariable Long menuId, @RequestBody MenuUpdateDto dto) {
        MenuResDto updatedMenu = menuService.updateMenu(menuId, dto);
        return new ResponseEntity<>(updatedMenu, HttpStatus.OK);
    }
}