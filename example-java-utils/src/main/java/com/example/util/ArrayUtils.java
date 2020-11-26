package com.example.util;


import java.util.List;

/**
 * @Author: zhouyuyang
 * @Date: 2020/10/29 11:33
 */
public class ArrayUtils {

    public static byte[] arrayCopy(byte[] target, byte[]... src){
        for (int i = 0, index = 0, len; i < src.length; index += len, i++) {
            byte[] temp = src[i];
            System.arraycopy(temp, 0, target, index, len = temp.length);
        }
        return target;
    }

    public static byte[] arrayCopy(byte[] target, List<byte[]> src){
        for (int i = 0, index = 0, len; i < src.size(); index += len, i++) {
            byte[] temp = src.get(i);
            System.arraycopy(temp, 0, target, index, len = temp.length);
        }
        return target;
    }


    public static byte[] getXOR(byte[] source){
        for (int i = 0; i < source.length; i++) {
            source[i] ^= 0x94;
        }
        return source;
    }


}
