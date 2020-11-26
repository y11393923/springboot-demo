package com.example.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.net.ssl.*;
import java.io.*;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

public class HttpsUtil {
    private static final Logger LOG = LoggerFactory.getLogger(HttpsUtil.class);

    private static final class DefaultTrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }

    private static HttpsURLConnection getHttpsURLConnection(String uri, String method) throws IOException {
        SSLContext ctx;
        try {
            TrustManager[] trustManagers = new TrustManager[]{
                    new X509TrustManager() {
                        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {
                        }

                        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {
                        }

                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
                    }
            };
            ctx = SSLContext.getInstance("TLS");
            ctx.init(null, trustManagers, null);
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage());
        }
        SSLSocketFactory ssf = ctx.getSocketFactory();

        URL url = new URL(uri);
        HttpsURLConnection httpsConn = (HttpsURLConnection) url.openConnection();
        httpsConn.setSSLSocketFactory(ssf);
        httpsConn.setHostnameVerifier((arg0, arg1) -> true);
        httpsConn.setRequestMethod(method);
        httpsConn.setDoInput(true);
        httpsConn.setDoOutput(true);
        return httpsConn;
    }

    private static byte[] getBytesFromStream(InputStream is) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()){
            byte[] kb = new byte[1024];
            int len;
            while ((len = is.read(kb)) != -1) {
                outputStream.write(kb, 0, len);
            }
            is.close();
            return outputStream.toByteArray();
        }
    }

    private static void setBytesToStream(OutputStream os, byte[] bytes) throws IOException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes)){
            byte[] kb = new byte[1024];
            int len;
            while ((len = inputStream.read(kb)) != -1) {
                os.write(kb, 0, len);
            }
            os.flush();
            os.close();
        }
    }

    public static byte[] doGet(String uri) throws IOException {
        HttpsURLConnection httpsConn = getHttpsURLConnection(uri, "GET");
        return getBytesFromStream(httpsConn.getInputStream());
    }

    public static byte[] doPost(String uri, String data) throws IOException {
        HttpsURLConnection httpsConn = getHttpsURLConnection(uri, "POST");
        setBytesToStream(httpsConn.getOutputStream(), data.getBytes());
        return getBytesFromStream(httpsConn.getInputStream());
    }

    public static byte[] getByte(InputStream in) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            //缓存数组
            byte[] buf = new byte[1024];
            //读取输入流中的数据放入缓存，如果读取完则循环条件为false;
            while (in.read(buf) != -1) {
                //将缓存数组中的数据写入out输出流，如果需要写到文件，使用输出流的其他方法
                out.write(buf);
            }
            out.flush();
            //将输出流的结果转换为字节数组的形式返回	（先执行finally再执行return	）
            return out.toByteArray();
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    /**
     * 编码
     * Base64被定义为：Base64内容传送编码被设计用来把任意序列的8位字节描述为一种不易被人直接识别的形式
     */
    public static String base64ToStr(byte[] bytes) throws IOException {
        String content = new BASE64Encoder().encode(bytes);
        //消除回车和换行
        return content.trim().replaceAll("\n", "").replaceAll("\r", "");
    }
    /**
     * 解码
     */
    public static byte[] strToBase64(String content) throws IOException {
        if (null == content) {
            return null;
        }
        return new BASE64Decoder().decodeBuffer(content.trim());
    }
}
