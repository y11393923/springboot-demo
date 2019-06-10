package com.zyy.util;

import org.apache.shiro.crypto.hash.Md5Hash;

/**
 * 功能描述：加密工具
 */
public class Encrypt {

    /**
     * 功能描述：高强度加密算法,不可逆
     * @param password:密码
     * @param salt：盐
     * @return java.lang.String
     **/
    public static String md5(String password, String salt){
        return new Md5Hash(password,salt,2).toString();
    }

    /**
     * 功能描述：用于测试的main方法
     * @param args
     **/
    public static void main(String[] args) {

        //ae7f487d56152e165afdfd87c2b819a5
        System.out.println(new Md5Hash("123456","jack",2).toString());
        //cc804223edc8063d7b3d9dc94b81fba3
        System.out.println(new Md5Hash("123456","tom",2).toString());
        //c89f94fdfb8ae723413296a03c0f8d3b
        System.out.println(new Md5Hash("123456","rose",2).toString());
        System.out.println();
        System.out.println(md5("123456","jack"));
        System.out.println(md5("123456","tom"));
        System.out.println(md5("123456","rose"));

    }
}
