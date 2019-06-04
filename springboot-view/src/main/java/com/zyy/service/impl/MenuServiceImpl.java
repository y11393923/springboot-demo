package com.zyy.service.impl;

import com.alibaba.fastjson.JSON;
import com.zyy.entity.Menu;
import com.zyy.mapper.MenuMapper;
import com.zyy.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @Author:zhouyuyang
 * @Description:
 * @Date: Created in 15:59 2019/6/3
 */
@Service
public class MenuServiceImpl implements MenuService{
    @Autowired
    private MenuMapper menuMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<Menu> getAllMenu() {
        Object object = redisTemplate.opsForValue().get("menu:list");
        List<Menu> allMenu;
        if (null == object){
            allMenu = menuMapper.getAllMenu();
            if (!CollectionUtils.isEmpty(allMenu)){
                redisTemplate.opsForValue().set("menu:list", JSON.toJSONString(allMenu));
            }
        }else{
            allMenu = JSON.parseArray(object.toString(),Menu.class);
        }
        return allMenu;
    }
}
