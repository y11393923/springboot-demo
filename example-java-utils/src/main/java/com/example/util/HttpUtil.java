package com.example.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {
    private static final Logger LOG = LoggerFactory.getLogger(HttpUtil.class);

    /**
     * 得到文件流
     * @param url
     * @return
     */
    public static byte[] getImageStream(String url){
        HttpURLConnection conn = null;
        InputStream inStream = null;
        try {
            URL httpUrl = new URL(url);
            conn = (HttpURLConnection)httpUrl.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5 * 1000);
            //通过输入流获取图片数据
            inStream = conn.getInputStream();
            //得到图片的二进制数据
            return readInputStream(inStream);
        } catch (Exception e) {
            LOG.error("getImageStream error: \n", e);
        }finally {
            try {
                if (inStream != null) {
                    inStream.close();
                }
                if (conn != null) {
                    conn.disconnect();
                }
            } catch (Exception e1) {
                LOG.error("getImageStream release connection error: \n", e1);
            }
        }
        return null;
    }

    /**
     * 从输入流中获取数据
     * @param inStream 输入流
     * @return
     * @throws Exception
     */
    public static byte[] readInputStream(InputStream inStream) throws Exception{
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while( (len=inStream.read(buffer)) != -1 ){
            outStream.write(buffer, 0, len);
        }
        inStream.close();
        return outStream.toByteArray();
    }
}
