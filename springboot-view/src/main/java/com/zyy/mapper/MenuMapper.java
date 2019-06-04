package com.zyy.mapper;

import com.zyy.entity.Menu;

import java.util.List;

public interface MenuMapper {
    List<Menu> getMenusByUserId(Integer userId);

    List<Menu> getAllMenu();
}
