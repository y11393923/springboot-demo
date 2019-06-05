package com.zyy.test;


import com.zyy.dao.impl.UserDaoImpl;
import com.zyy.entity.User;
import org.apache.commons.codec.binary.Hex;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.net.InetAddress;
import java.security.MessageDigest;

public class TestClass {
    public static void main(String[] args) throws Exception {
        System.out.println(sha256Digest("123").length());
        System.out.println(sha256Digest("1233"));

        String ip = InetAddress.getLocalHost().getHostAddress().toString();
        System.out.println(ip);


    }

    private static String DEFAULT_ENCODING = "UTF-8";
    private static String SHA_256 = "SHA-256";
    public static String sha256Digest(String str)throws Exception {
        return digest(str, SHA_256, DEFAULT_ENCODING);
    }
    private static String digest(String str, String alg, String charencoding)throws Exception {
        try {
            byte[] data = str.getBytes(charencoding);
            MessageDigest md = MessageDigest.getInstance(alg);
            return Hex.encodeHexString(md.digest(data));
        } catch (Exception var5) {
            throw new RuntimeException("digest fail!", var5);
        }
    }


}
