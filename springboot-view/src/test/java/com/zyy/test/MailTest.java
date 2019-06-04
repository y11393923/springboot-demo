package com.zyy.test;

import com.zyy.service.MailService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author:zhouyuyang
 * @Description:
 * @Date: Created in 15:57 2019/5/30
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class MailTest {
    @Autowired
    private MailService mailService;
    @Test
    public void testSendSimpleMail() throws Exception {
        mailService.sendSimpleMail("11393923@qq.com",
                "11393923@qq.com",
                null,
                "测试邮件主题",
                "测试邮件内容");
    }

    @Test
    public void testSendAttachFileMail() throws Exception {
        mailService.sendAttachFileMail("11393923@qq.com",
                "11393923@qq.com",
                null,
                "测试邮件主题",
                "测试邮件内容",
                new File("E:\\workspaces\\springboot-view\\src\\main\\resources\\banner.txt"));
    }

    @Test
    public void testSendMailWithirng() throws Exception {
        mailService.sendMailWithirng("11393923@qq.com",
                "11393923@qq.com",
                null,
                "测试邮件主题(图片)",
                "<div> hello 这是 封带图片资源的邮件：" +
                        "这是图片1：<div><img src='cid:p01'/></div>" +
                        "这是图片2：<div><img src='cid:p02'/></div>" +
                        "</div>",
                new String[]{"E:\\workspaces\\springboot-view\\src\\main\\resources\\static\\images\\Koala.jpg","E:\\workspaces\\springboot-view\\src\\main\\resources\\static\\images\\Koala.jpg"},
                new String[]{"p01","p02"});
    }

    @Test
    public void testSendHtmlMailThymeleaf() throws Exception {
        Map<String,Object> params=new HashMap<>();
        params.put("name","赵小骗子");
        params.put("workID","438");
        params.put("contractTerm",1);
        params.put("beginContract",new Date());
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR,1);
        Date time = calendar.getTime();
        params.put("endContract", time);
        params.put("departmentName","研发部");
        params.put("posName","java工程师");
        mailService.sendHtmlMailThymeleaf("11393923@qq.com",
                "11393923@qq.com",
                null,
                "测试邮件主题",
                params,
                "email.html");
    }
}
