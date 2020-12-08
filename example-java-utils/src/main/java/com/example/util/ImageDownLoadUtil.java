package com.example.util;

import com.example.config.OKHttpClientBuilder;
import okhttp3.OkHttpClient;

import java.io.IOException;
import java.util.Base64;

/**
 * 下载图片适配https
 */
public class ImageDownLoadUtil {

    private static final Base64.Encoder BASE64_ENCODER = Base64.getEncoder();

    private OkHttpClient okHttpClient= OKHttpClientBuilder.buildOKHttpClient().build();

    private final String BASE64_IMG_PRE = "data:image/jpeg;base64,";

    public String convertToBase64(String url) throws IOException {
        byte[] bytes = this.downLoad(url);
        if(bytes != null && bytes.length > 0) {
            String base64Src = BASE64_ENCODER.encodeToString(bytes);
            if(base64Src.startsWith(BASE64_IMG_PRE)) {
                return base64Src;
            } else {
                return BASE64_IMG_PRE + base64Src;
            }
        } else {
            return null;
        }
    }

    public byte[] downLoad(String url) throws IOException {
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .get().build();
        okhttp3.Response response = okHttpClient.newCall(request).execute();
        if(response.isSuccessful()){
            return response.body().bytes();
        } else {
            return null;
        }
    }
}
