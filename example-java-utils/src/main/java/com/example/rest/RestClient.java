package com.example.rest;

import com.alibaba.fastjson.TypeReference;
import org.springframework.web.client.RestTemplate;

public abstract class RestClient {

    /**
     * 获取RestTemplate实例
     *
     * @return
     */
    protected abstract RestTemplate getRestTemplate();


    public <T> DefaultRequestBuilder<T> request(String url, Class<T> responseType) {
        return new DefaultRequestBuilder<T>(getRestTemplate()).url(url).responseType(responseType);
    }

    public <T> DefaultRequestBuilder<T> request(String url, TypeReference<T> typeReference) {
        return new DefaultRequestBuilder<T>(getRestTemplate()).url(url).responseType(typeReference);
    }


    public static class DefaultRestRequest<T> extends RestRequest<T> {
        protected DefaultRestRequest(Builder builder) {
            super(builder);
        }
    }

    public static class DefaultRequestBuilder<T> extends RestRequest.Builder<T, DefaultRequestBuilder<T>> {

        protected DefaultRequestBuilder(RestTemplate restTemplate) {
            super(restTemplate);
        }

        @Override
        public DefaultRestRequest<T> build() {
            return new DefaultRestRequest(this);
        }
    }


}
