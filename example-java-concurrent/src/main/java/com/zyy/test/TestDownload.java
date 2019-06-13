package com.zyy.test;

import com.zyy.entity.User;

import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author:zhouyuyang
 * @Description:
 * @Date: Created in 15:22 2019/6/13
 */
public class TestDownload {
    public static void main(String[] args) throws Exception {
        LocalDate now = LocalDate.now();
        System.out.println(now);
        LocalDateTime nowTime=LocalDateTime.now();
        DateTimeFormatter dtf=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        System.out.println(dtf.format(nowTime));
        long instant = Instant.now().toEpochMilli();
        System.out.println(instant);

        User user1=new User(1L,"张三",new Date());
        User user2=new User(2L,"李四",new Date());
        List<User> list=new ArrayList<>();
        list.add(user1);
        list.add(user2);
        list.forEach(item -> System.out.println(item.getName()));

        list.stream().filter(item -> item.getName().equals("张三")).limit(10).map(User::getName).forEach(System.out::println);

        //图片下载
        URL url=new URL("https://f11.baidu.com/it/u=1174863640,1423973292&fm=76");
        DataInputStream dataInputStream=new DataInputStream(url.openStream());
        FileOutputStream fos=new FileOutputStream("E://123.jpg");
        byte[] buffer=new byte[1024];
        int len;
        while ((len=dataInputStream.read(buffer))!=-1){
            fos.write(buffer,0,len);
        }
        fos.flush();
        fos.close();
        dataInputStream.close();
    }
}
