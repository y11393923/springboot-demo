package com.zyy.test;

import javafx.beans.binding.StringBinding;

import java.util.Collections;

/**
 * @Author:zhouyuyang
 * @Description:
 * @Date: Created in 12:26 2019/6/25
 */
public class TestStringReverse {
    public static void main(String[] args) {
        String str = "abcdefg";
        System.out.println(new StringBuilder(str).reverse().toString());

        System.out.println(reverse(str));

    }

    public static String reverse(String str){
        if (null != str && str.length() > 0){
            int len = str.length();
            char[] chars = new char[len];
            for (int i = len - 1 ; i >= 0 ; i--) {
                chars[len - 1 - i] = str.charAt(i);
            }
            return new String(chars);
        }
        return null;
    }
}
