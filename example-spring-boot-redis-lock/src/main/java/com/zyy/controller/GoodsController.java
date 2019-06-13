package com.zyy.controller;

import com.zyy.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Administrator
 * @create 2018/9/26
 * @since 1.0.0
 */
@RestController
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @GetMapping("/buyGoods")
    public String index(){
        return goodsService.updateById(1,2);
    }



}
