package com.zyy.test;

/**
 * @Author:zhouyuyang
 * @Description:
 * @Date: Created in 10:00 2019/5/24
 */
public class Test2 {
    public static void main(String[] args) {
        int pageNumber=3;
        pageNumber = (pageNumber - 1 ) / 10 + 1;
        System.out.println(pageNumber);
    }
}
