package com.beyond.StomachForce.menu.controller;

import com.beyond.StomachForce.menu.domain.Menu;
import com.beyond.StomachForce.menu.dto.MenuResDto;
import com.beyond.StomachForce.menu.dto.MenuCreateDto;
import com.beyond.StomachForce.menu.service.MenuService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/menu")
public class MenuController {
    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

//    @PostMapping("/create")
////    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<?> menuCreate(MenuCreateDto dto){
//        Menu menu = menuService.menuCreate(dto);
//        return new ResponseEntity<>(menu.getId(), HttpStatus.CREATED);
//    }

    @GetMapping("/list")
    public ResponseEntity<?> menuList(){
        List<MenuResDto> menuResDtoList = menuService.findAll();
        return new ResponseEntity<>(menuResDtoList, HttpStatus.OK);
    }
}