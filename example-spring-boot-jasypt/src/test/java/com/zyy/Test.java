package com.zyy;

import com.zyy.entity.UserInfo;
import com.zyy.mapper.UserMapper;
import org.jasypt.encryption.StringEncryptor;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class Test {

    @Autowired
    private StringEncryptor stringEncryptor;
    @Autowired
    private UserMapper userMapper;

    @org.junit.Test
    public void encryptPwd() {
        //加密root
        String result = stringEncryptor.encrypt("root");
        System.out.println(result);
    }

    @org.junit.Test
    public void decrypt() {
        String result = stringEncryptor.decrypt("egcxqaMeNuzD0I3jVPytQA==");
        System.out.println(result);
    }


    @org.junit.Test
    public void query() {
        List<UserInfo> userInfos = userMapper.queryAll();
        userInfos.forEach(e -> System.out.println(e.toString()));
    }

}
