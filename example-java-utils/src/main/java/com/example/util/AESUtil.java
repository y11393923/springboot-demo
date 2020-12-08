package com.example.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * AESUtil
 * <p>AES 加密解密工具类</p>
 *
 */
@Slf4j
public class AESUtil {


    private static final String KEY_ALGORITHM = "AES";
    /**
     * 默认的加密算法
     */
    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";


    private static final int ONE = 1;
    private static final int TWO = 2;
    private static final int FOUR = 4;
    private static final int EIGHT = 8;
    private static final int AES_KEY_DIGIT = 128;


    /**
     * AES 加密操作
     *
     * @param content 明文
     * @param key     加密密钥
     * @return 返回Base64转码后的加密数据
     */
    public static String encrypt(String content, String key) {
        try {
            // 创建密码器
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);

            byte[] byteContent = content.getBytes(StandardCharsets.UTF_8);
            // 初始化为加密模式的密码器
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(key));
            // 加密
            byte[] result = cipher.doFinal(byteContent);
            //通过Base64转码返回
            return Base64.encodeBase64String(result);
        } catch (Exception ex) {
            log.error("AES 加密异常:{}", ex.getMessage());
        }
        return null;
    }

    /**
     * AES 解密操作
     *
     * @param content 密文
     * @param key     key
     * @return
     */
    public static String decrypt(String content, String key) {

        try {
            //实例化
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);

            //使用密钥初始化，设置为解密模式
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(key));

            //执行操作
            byte[] result = cipher.doFinal(Base64.decodeBase64(content));

            return new String(result, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            log.error("AES 解密异常:{}", ex.getMessage());
        }

        return null;
    }

    /**
     * 生成加密秘钥
     *
     * @param key
     * @return
     */
    private static SecretKeySpec getSecretKey(final String key) {
        //返回生成指定算法密钥生成器的 KeyGenerator 对象
        KeyGenerator kg = null;
        try {
            kg = KeyGenerator.getInstance(KEY_ALGORITHM);
            //AES 要求密钥长度为 128
            kg.init(AES_KEY_DIGIT, new SecureRandom(key.getBytes()));
            //生成一个密钥
            SecretKey secretKey = kg.generateKey();
            // 转换为AES专用密钥
            return new SecretKeySpec(secretKey.getEncoded(), KEY_ALGORITHM);
        } catch (NoSuchAlgorithmException ex) {
            log.error("AES 生成加密秘钥异常:{}", ex.getMessage());
        }
        return null;
    }

    /**
     * 解密密文并转成 122****7的格式
     *
     * @param content
     * @param key
     * @return
     */
    public static String decryptProcess(String content, String key) {
        return processPlaintext(decrypt(content, key));
    }

    /**
     * 处理明文 把部分文字转换成*
     *
     * @param content
     * @return
     */
    public static String processPlaintext(String content) {
        if (StringUtils.isBlank(content)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        int length = content.length();
        if (length <= FOUR) {
            for (int i = 0; i < length; i++) {
                sb.append("*");
            }
        }
        if (length > FOUR && length < EIGHT) {
            int j = length - FOUR;
            int q = ONE;
            for (int i = ONE; i <= length; i++) {
                if (i >= j) {
                    if (q <= FOUR && q != i) {
                        sb.append("*");
                        q++;
                    } else {
                        sb.append(content.charAt(i - 1));
                    }
                } else {
                    sb.append(content.charAt(i - 1));
                }
            }
        }

        if (length >= EIGHT) {
//            加密位数
            int p = length / TWO;
//            加密位数是奇数还是偶数  奇数为1 偶数为0
            int g = p % TWO;
//          总数是奇数还是偶数 奇数为1 偶数为0
            int big = length % p;
//            前多少位不加密
            int t = p / TWO + big;
//            当加密位数是奇数 且 总数是偶数时 前面不加密字符数+1
            if (g == ONE && big == 0) {
                t = t + g;
            }
            for (int i = ONE; i <= length; i++) {
                if (i > t && i <= t + p) {
                    sb.append("*");
                } else {
                    sb.append(content.charAt(i - ONE));
                }
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        String content = "431103199510281876";
        String key = "senseguard_aes_128_secret_key";
        log.info("原文:" + content);

        long estrTime = System.currentTimeMillis();
        String encryptContent = encrypt(content, key);
        long eendTime = System.currentTimeMillis();
        log.info("加密:" + encryptContent + "  用时:" + (eendTime - estrTime));

        long dstrTime = System.currentTimeMillis();
        String decryptStr = decrypt(encryptContent, key);
        long dendTime = System.currentTimeMillis();
        log.info("解密:" + decryptStr + "用时:" + (dendTime - dstrTime));

    }
}
