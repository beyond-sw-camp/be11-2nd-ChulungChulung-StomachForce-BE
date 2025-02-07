package com.beyond.StomachForce.menu.service;

import com.beyond.StomachForce.menu.dto.MenuResDto;
import com.beyond.StomachForce.menu.repository.MenuRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MenuService {
    private final MenuRepository menuRepository;

    public MenuService(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    public List<MenuResDto>findAll(){
        return menuRepository.findAll().stream().map(m -> m.listFromEntity()).toList();
    }
}
