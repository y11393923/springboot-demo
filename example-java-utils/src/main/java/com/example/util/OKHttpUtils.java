package com.example.util;

import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import javax.net.ssl.*;
import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * OKHttpUtils
 *
 * @author lihaibing
 **/
public final class OKHttpUtils {

    private static final Logger logger = LoggerFactory.getLogger(OKHttpUtils.class);

    private static final MediaType IMAGE_MEDIA_TYPE = MediaType.parse("image/jpg");
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static final OkHttpClient CLIENT = new OkHttpClient().newBuilder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .sslSocketFactory(createSSLSocketFactory())
            .hostnameVerifier(new TrustAllHostnameVerifier())
            .build();

    private OKHttpUtils() {
        throw new IllegalStateException("OKHttpUtils class");
    }

    /**
     * 同步get请求方法
     *
     * @param uri
     * @param params
     * @return
     */
    public static Response get(String uri, Map<String, String> params) {
        String requestUri = buildGETParams(uri, params);
        OkHttpClient okHttpClient = buildClient();
        Request request = new Request.Builder()
                .url(requestUri)
                .build();
        try {
            Call call = okHttpClient.newCall(request);
            return call.execute();
        } catch (IOException e) {
            logger.error("[HTTP GET Request]url:{}, exception message: ", uri, e.getCause());
        }
        return null;
    }

    /**
     * 同步post请求方法
     *
     * @param uri
     * @param params
     * @return
     */
    public static Response post(String uri, Map<String, Object> params) {
        OkHttpClient okHttpClient = buildClient();
        Request request = new Request.Builder()
                .url(uri)
                .post(buildPOSTBody(params))
                .build();
        try {
            Call call = okHttpClient.newCall(request);
            return call.execute();
        } catch (IOException e) {
            logger.error("[HTTP POST Request]url:{}, exception message:{}", uri, e.getMessage());
        }
        return null;
    }

    /**
     * 同步post请求方法
     *
     * @param uri
     * @param json
     * @return
     */
    public static String postJSONStr(String uri, String json) throws IOException {
        OkHttpClient okHttpClient = buildClient();
        RequestBody requestBody = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(uri)
                .post(requestBody)
                .build();

        return handleResponse(okHttpClient, request);
    }

    /**
     * 处理返回结果
     *
     * @param okHttpClient
     * @param request
     * @return
     * @throws IOException
     */
    private static String handleResponse(OkHttpClient okHttpClient, Request request) throws IOException {
        ResponseBody responseBody = null;
        String responseStr = null;
        try {
            Call call = okHttpClient.newCall(request);
            Response response = call.execute();
            responseBody = response.body();
            if (response.isSuccessful()) {
                responseStr = responseBody != null ? responseBody.string() : null;
            } else {
                logger.error("[OKHttpUtils] request {} error.Code: {},Cause: {}.", request.url(), response.code(), responseBody);
            }
        } finally {
            if (null != responseBody) {
                responseBody.close();
            }
        }

        return responseStr;
    }

    /**
     * 设置GET请求参数
     *
     * @param uri
     * @param params
     * @return
     */
    private static String buildGETParams(String uri, Map<String, String> params) {
        StringBuilder paramsBuilder = new StringBuilder(uri);
        paramsBuilder.append("?");
        if (!CollectionUtils.isEmpty(params)) {
            params.forEach((key, value) -> {
                paramsBuilder.append(key);
                paramsBuilder.append("=");
                paramsBuilder.append(value);
                paramsBuilder.append("&");
            });
        }
        return paramsBuilder.substring(0, paramsBuilder.length() - 1);
    }

    /**
     * 设置POST请求参数
     *
     * @param params
     * @return
     */
    private static RequestBody buildPOSTBody(Map<String, Object> params) {
        RequestBody requestBody;
        if (!CollectionUtils.isEmpty(params)) {
            if (isMultipartRequest(params)) {
                requestBody = buildMultipartBody(params);
            } else {
                requestBody = buildRequestBody(params);
            }
        } else {
            requestBody = new FormBody.Builder().build();
        }
        return requestBody;
    }

    /**
     * 设置POST文件参数
     *
     * @param params
     * @return
     */
    private static MultipartBody buildMultipartBody(Map<String, Object> params) {
        MultipartBody.Builder multipartBodyBuild = new MultipartBody.Builder();
        multipartBodyBuild.setType(MultipartBody.FORM);

        params.forEach((key, value) -> {
            if (value instanceof Collection) {//集合参数
                if (!CollectionUtils.isEmpty((Collection) value)) {
                    ((Collection) value).stream().forEach(item ->
                        setMultiPart(multipartBodyBuild, key, item)
                    );
                }
            } else {
                setMultiPart(multipartBodyBuild, key, value);
            }

        });

        return multipartBodyBuild.build();
    }

    /**
     * 设置文件参数
     *
     * @param multipartBodyBuild
     * @param key
     * @param value
     */
    private static void setMultiPart(MultipartBody.Builder multipartBodyBuild, String key, Object value) {
        if (value instanceof File) {
            File file = (File) value;
            RequestBody reqBody = RequestBody.create(IMAGE_MEDIA_TYPE, file);
            multipartBodyBuild.addFormDataPart(key, file.getName(), reqBody);
        } else if (value instanceof FileParamDTO) {
            FileParamDTO file = (FileParamDTO) value;
            RequestBody reqBody = RequestBody.create(IMAGE_MEDIA_TYPE, file.getBuff());
            multipartBodyBuild.addFormDataPart(key, file.getFileName(), reqBody);
        } else {
            multipartBodyBuild.addFormDataPart(key, String.valueOf(value));
        }
    }

    /**
     * 设置普通POST BODY参数
     *
     * @param params
     * @return
     */
    private static RequestBody buildRequestBody(Map<String, Object> params) {
        FormBody.Builder formBodyBuilder = new FormBody.Builder();

        params.forEach((key, value) -> {
            if (value instanceof Collection) {//集合参数
                if (!CollectionUtils.isEmpty((Collection) value)) {
                    ((Collection) value).stream().forEach(item ->
                        formBodyBuilder.add(key, String.valueOf(item))
                    );
                }
            } else {
                formBodyBuilder.add(key, String.valueOf(value));
            }

        });

        return formBodyBuilder.build();
    }

    /**
     * 是否包含文件参数
     *
     * @param params
     * @return
     */
    private static boolean isMultipartRequest(Map<String, Object> params) {
        boolean isMultipart = false;

        for (String key : params.keySet()) {
            if (params.get(key) instanceof File
                    || params.get(key) instanceof FileParamDTO) {
                isMultipart = true;
                break;
            }

            if (params.get(key) instanceof Collection) {
                if (!((Collection) params.get(key)).stream().noneMatch(item -> item instanceof File || item instanceof FileParamDTO)) {
                    isMultipart = true;
                    break;
                }
            }
        }

        return isMultipart;
    }

    private static OkHttpClient buildClient() {
        return CLIENT;
    }

    class FileParamDTO {
        private byte[] buff;
        private String fileName;

        public byte[] getBuff() {
            return buff;
        }

        public void setBuff(byte[] buff) {
            this.buff = buff;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }
    }


    private static class TrustAllCerts implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}

        @Override
        public X509Certificate[] getAcceptedIssuers() {return new X509Certificate[0];}
    }
    private static class TrustAllHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }
    private static SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null,  new TrustManager[] { new TrustAllCerts() }, new SecureRandom());

            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
        }

        return ssfFactory;
    }
}
