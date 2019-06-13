package com.zyy;

import com.zyy.service.GoodsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author:zhouyuyang
 * @Description:
 * @Date: Created in 15:26 2019/6/13
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestGoods {
    @Autowired
    private GoodsService goodsService;
    @Test
    public void test(){
        goodsService.updateById(1,2);
    }
}
