package com.example.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;

public class Base64Util {
    private static final Logger LOG = LoggerFactory.getLogger(Base64Util.class);

    /**
     * 二进制数据编码为BASE64字符串.
     *
     * @param b
     * @return
     */
    public static String encode(byte[] b) {
        return Base64.encodeBase64String(b);
    }

    /**
     * 解码
     *
     * @param str base64字符串
     * @return string
     */
    public static byte[] decode(String str) {
        return Base64.decodeBase64(str);
    }

    /**
     * 私有构造器
     */
    private Base64Util() {
        throw new IllegalStateException("Base64Util class");
    }

    /**
     * 根据URL下载图片
     *
     * @param url 图片url
     * @return
     */
    public static byte[] downloadImage(String url) {
        HttpGet httpGet = new HttpGet(url);
        byte[] dataBuffer = null;
        CloseableHttpResponse response = null;
        try (ByteArrayOutputStream output = new ByteArrayOutputStream(); CloseableHttpClient httpclient = HttpClients.createDefault()){
            response = httpclient.execute(httpGet);
            int status = response.getStatusLine().getStatusCode();
            if (status == 200) {
                HttpEntity entity = response.getEntity();
                InputStream input = entity.getContent();
                IOUtils.copy(input, output);
                output.flush();
                input.close();
                dataBuffer = output.toByteArray();
                EntityUtils.consume(entity);
            }
        } catch (Exception e) {
            LOG.error(">>> downloadImage failed: \n", e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    LOG.error(">>> httpclient close failed: \n", e);
                }
            }
        }
        return dataBuffer;
    }

    /**
     * 根据图片url获取byte[]
     *
     * @param urlPath 图片url
     * @return
     */
    public static byte[] getBase64(String urlPath) {
        try (BufferedInputStream in = new BufferedInputStream(new URL(urlPath).openStream());) {
            return streamToByte(in);
        } catch (Exception e) {
            LOG.error(">>> getBase64 from url error:", e);
        }
        return new byte[1024];
    }

    public static byte[] streamToByte(InputStream input) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int len = 0;
        byte[] b = new byte[1024];
        while ((len = input.read(b, 0, b.length)) != -1) {
            baos.write(b, 0, len);
        }
        return baos.toByteArray();
    }

    /**
     * 根据图片url获取byte[]
     *
     * @param filePath 图片url
     * @return
     */
    public static  byte[] getBytes(String filePath) {
        byte[] buffer = new byte[1024];
        ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
        File file = new File(filePath);
        try(FileInputStream fis = new FileInputStream(file)) {
            byte[] b = new byte[1000];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            LOG.error(">>> file not found exception: ", e);
        } catch (IOException e) {
            LOG.error(">>> io exception: ", e);
        }finally {
            try {
                bos.close();
            } catch (Exception e) {
                LOG.error(">>> io close error: ", e);
            }
        }
        return buffer;
    }

    /**
     * base64字符串转化成图片
     *
     * @param imgStr      base64字符串
     * @param imgFilePath 路径
     * @return
     */
    public static String base64ToImage(String imgStr, String imgFilePath) {
        //对字节数组字符串进行Base64解码并生成图片
        if (imgStr == null) {
            return null;
        }
        Base64 decoder = new Base64();
        try (OutputStream out = new FileOutputStream(imgFilePath)){
            //Base64解码
            byte[] b = decoder.decode(imgStr);
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {
                    b[i] += 256;
                }
            }
            //生成jpeg图片
            LOG.info("imgFilePath:", imgFilePath);
            File file = new File(imgFilePath);
            file.mkdirs();
            out.write(b);
            out.flush();
            return imgFilePath;
        } catch (Exception e) {
            LOG.info("Base64ToImage e" + e.getMessage());
        }
        return null;
    }

    /**
     * 图片转base64保存到本地
     *
     * @param urlPath 图片url
     * @param path    存储路径
     * @return
     */
    public static void convertImageToBase64(String urlPath, String path) {
        BufferedInputStream in = null;
        File file = new File(path);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        try(FileWriter fw = new FileWriter(file, true)) {
            URL url = new URL(urlPath);
            in = new BufferedInputStream(url.openStream());
            byte[] bys = streamToByte(in);
            String base64 = Base64.encodeBase64String(bys);
            boolean success = file.createNewFile();
            if (!success) {
                LOG.error(">>> createNewFile failed....");
            }
            fw.write(base64);
            fw.flush();
        } catch (Exception e) {
            LOG.error(">>> convertImageToBase64 error: \n", e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                LOG.error(">>> io close error: \n", e);
            }
        }
    }

    /**
     * 图片文件转base64存本地
     *
     * @param bytes 图片文件字节
     * @param path 存储路径
     * @return
     */
    public static void convertImageFileToBase64(byte[] bytes, String path) {
        try {
            String base64 = Base64.encodeBase64String(bytes);
            File fileAddress = new File(path);
            if (!fileAddress.getParentFile().exists()) {
                fileAddress.getParentFile().mkdirs();
            }
            boolean success = fileAddress.createNewFile();
            if (!success) {
                LOG.error(">>> createNewFile failed....");
            }
            LOG.info("file_address" + fileAddress.toString());
            write(fileAddress, base64);
        } catch (Exception e) {
            LOG.error(">>> convertImageFileToBase64 error: \n", e);
        }
    }

    /**
     * write
     * @param fileAddress
     * @param base64
     */
    public static  void write(File fileAddress, String base64) {
        try (FileWriter fw = new FileWriter(fileAddress, true)) {
            fw.write(base64);
            fw.flush();
        } catch (Exception e) {
            LOG.error(">>> FileWriter write error: \n", e);
        }
    }
}
