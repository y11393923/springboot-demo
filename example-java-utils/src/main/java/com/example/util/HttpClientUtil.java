package com.example.util;

import com.example.vo.HttpResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author: zhouyuyang
 * @Date: 2020/7/20 17:47
 */
public class HttpClientUtil {

    private final static Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

    private static void addBody(MultipartEntityBuilder builder, String key, Object value) {
        if (value instanceof List) {
            List<?> values = (List<?>) value;
            for (Object obj : values) {
                addBody(builder, key, obj);
            }
        }
        if (value instanceof byte[]) {
            builder.addBinaryBody(key, (byte[]) value);
        } else if (value instanceof File) {
            builder.addBinaryBody(key, (File) value);
        } else if(value instanceof Object[]) {
            Object[] sv = (Object[]) value;
            for (int i = 0; i < sv.length; i++) {
                builder.addTextBody(key, (String)sv[i]);
            }
        } else {
            builder.addTextBody(key, value.toString());
        }
    }


    private static HttpPost buildBinaryPost(String url, Map<String, Object> params) {
        HttpPost post = new HttpPost(url);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        if (params != null && params.size() > 0) {
            Object value;
            for (String key : params.keySet()) {
                value = params.get(key);
                addBody(builder, key, value);
            }
        }
        post.setConfig(RequestConfig.custom().setConnectTimeout(5000).build());
        post.setEntity(builder.build());
        return post;
    }

    private static HttpPost buildHttpPost(String url, Map<String, String> params) throws Exception {
        if (StringUtils.isBlank(url)) {
            logger.error(">>>构建HttpPost时,url不能为null");
            throw new Exception("url is null.");
        }
        HttpPost post = new HttpPost(url);
        HttpEntity he = null;
        if (params != null) {
            List<NameValuePair> formparams = new ArrayList<NameValuePair>();
            for (String key : params.keySet()) {
                formparams.add(new BasicNameValuePair(key, params.get(key)));
            }
            post.setEntity(new UrlEncodedFormEntity(formparams));
        }
        // 在RequestContent.process中会自动写入消息体的长度，自己不用写入，写入反而检测报错
        // setContentLength(post, he);
        return post;

    }

    public static HttpResult doBinaryPost(String url, Map<String, Object> params) {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = buildBinaryPost(url, params);
        CloseableHttpResponse response = null;
        HttpResult result = null;
        try {
            response = client.execute(post);
            result = getHttpResult(response);
        } catch (Exception e) {
            logger.debug(">>> post error url:" + url, e);
            logger.error(">>> post error url:" + url);
        } finally {
            close(client, response);
        }
        return result;
    }

    public static HttpResult doPost(String url, Map<String, String> params) throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = buildHttpPost(url, params);
        CloseableHttpResponse response = null;
        HttpResult result = null;
        try {
            response = client.execute(post);
            result = getHttpResult(response);
        } catch (Exception e) {
            logger.debug(">>> post error url:" + url, e);
            logger.error(">>> post error url:" + url);
        } finally {
            close(client, response);
        }
        return result;
    }



    public static HttpPost buildMultiHttpPost(String url, Map<String, Object> params){
        HttpPost post = new HttpPost(url);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        if (params != null && params.size() > 0) {
            Object value;
            for (String key : params.keySet()) {
                value = params.get(key);
                addMultiBody(builder, key, value);
            }
        }
        post.setConfig(RequestConfig.custom().setConnectTimeout(5000).build());
        post.setEntity(builder.build());
        return post;
    }

    private static void addMultiBody(MultipartEntityBuilder builder, String key, Object value) {
        if (value instanceof List) {
            List<?> values = (List<?>) value;
            for (Object obj : values) {
                addMultiBody(builder, key, obj);
            }
        }
        if (value instanceof byte[]) {
            builder.addBinaryBody(key, (byte[]) value);
        } else if (value instanceof File) {
            builder.addBinaryBody(key, (File) value);
        } else if(value instanceof File[]){
            File[] files = (File[]) value;
            for(int i = 0; i < files.length; i++){
                FileBody fileBody = new FileBody(files[i]);
                builder.addPart(key, fileBody);
            }
        }else if(value instanceof Object[]) {
            Object[] sv = (Object[]) value;
            for (int i = 0; i < sv.length; i++) {
                builder.addTextBody(key, (String)sv[i]);
            }
        } else {
            builder.addTextBody(key, value.toString());
        }
    }

    /**
     * 多文件上传
     * @param url
     * @param params
     * @return
     */
    public static HttpResult multiUploadPost(String url, Map<String, Object> params){
        HttpResult result;
        CloseableHttpClient httpclient = null;
        CloseableHttpResponse response = null;
        try {
            int timeout = 60;
            RequestConfig config = RequestConfig.custom().setConnectTimeout(timeout * 1000)
                    .setConnectionRequestTimeout(timeout * 1000).setSocketTimeout(timeout * 1000).build();
            httpclient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
            HttpPost httpPost = buildMultiHttpPost(url, params);
            response = httpclient.execute(httpPost);
            result = new HttpResult();
            result.setStatus(response.getStatusLine().getStatusCode());
            HttpEntity entity = response.getEntity();
            String returnStr = EntityUtils.toString(entity);
            EntityUtils.consume(entity);
            result.setData(returnStr);
            return result;
        } catch (IOException e) {
            logger.error(">>>httpClientUtils do multiple files post is error." + e.getMessage());
        }finally {
            close(httpclient, response);
        }
        return null;
    }

    public static HttpResult doPost(String url, byte[] data) {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        post.setHeader("Content-Type", "text/xml;charset=UTF-8");
        HttpEntity entity = new ByteArrayEntity(data);
        post.setEntity(entity);
        CloseableHttpResponse response = null;
        HttpResult result = null;
        try {
            response = client.execute(post);
            result = getHttpResult(response);
        } catch (Exception e) {
            logger.debug(">>> post error url:" + url, e);
            logger.error(">>> post error url:" + url);
        } finally {
            close(client, response);
        }
        return result;
    }

    private static HttpResult getHttpResult(CloseableHttpResponse response) throws IOException {
        if (response != null) {
            HttpEntity entity = response.getEntity();
            HttpResult httpResult = new HttpResult();
            httpResult.setStatus(response.getStatusLine().getStatusCode());
            httpResult.setData(EntityUtils.toString(entity));
            EntityUtils.consume(entity);
            return httpResult;
        }
        return null;
    }

    private static void close(CloseableHttpClient client, CloseableHttpResponse response) {
        try {
            if (response != null)
                response.close();
            client.close();
        } catch (Exception e) {
            logger.debug(">>> close response or client error", e);
            logger.error(">>> close response or client error");
        }
    }

    public static HttpResult doGet(String url) {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet(url);
        CloseableHttpResponse response = null;
        HttpResult result = null;
        try {
            response = client.execute(get);
            result = getHttpResult(response);
        } catch (Exception e) {
            logger.debug(">>> get error url:" + url, e);
            logger.error(">>> get error url:" + url);
        } finally {
            close(client, response);
        }
        return result;
    }

    /**
     * 根据URL下载图片
     *
     * @param url
     * @return
     */
    public static byte[] downloadImage(String url) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        byte[] dataBuffer = null;
        CloseableHttpResponse response = null;
        try {
            if(url.startsWith("https")) {
                SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                    //信任所有
                    @Override
                    public boolean isTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) throws java.security.cert.CertificateException {
                        return true;
                    }
                }).build();
                SSLConnectionSocketFactory ssl = new SSLConnectionSocketFactory(sslContext);
                httpclient = HttpClients.custom().setSSLSocketFactory(ssl).build();
            }
            response = httpclient.execute(httpGet);
            int status = response.getStatusLine().getStatusCode();
            if (status == 200) {
                HttpEntity entity = response.getEntity();
                InputStream input = entity.getContent();
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                IOUtils.copy(input, output);
                output.flush();
                input.close();
                dataBuffer = output.toByteArray();
                output.close();
                EntityUtils.consume(entity);
            }
        } catch (Exception e) {
            logger.error("download image url failed",e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    logger.error("close io failed",e);
                }
            }
        }
        return dataBuffer;
    }
}
