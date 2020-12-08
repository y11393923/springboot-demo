package com.example.rest;

import com.alibaba.fastjson.TypeReference;
import com.example.common.BaseResult;
import com.example.common.enums.CommonCodeEnum;
import com.example.util.BaseResultUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Type;
import java.util.List;

/**
 * BaseResultRestClient
 *
 */
public class BaseResultRestClient extends RestClient {
    private static Logger logger = LoggerFactory.getLogger(BaseResultRestClient.class);

    private final RestTemplate restTemplate;

    public BaseResultRestClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public <T> BaseResultRestRequest.BaseResultRequestBuilder<T> baseResultRequest(String url, Class<T> responseType) {
        return new BaseResultRestRequest.BaseResultRequestBuilder<T>(restTemplate).url(url).responseType(responseType);
    }


    public <T> BaseResultRestRequest.BaseResultRequestBuilder<T> baseResultRequest(String url, TypeReference<T> typeReference) {
        return new BaseResultRestRequest.BaseResultRequestBuilder<T>(restTemplate).url(url).responseType(typeReference);
    }


    public static class BaseResultRestRequest<T> extends RestRequest<T> {

        protected BaseResultRestRequest(Builder builder) {
            super(builder);
        }

        @Override
        public T exchange(HttpMethod method) throws BaseResultRestClientException {
            HttpEntity entity = buildHttpEntitiy(method);
            String url = concreteUrl(writeParamToUrl);

            long start = System.currentTimeMillis();
            logger.debug(" ~~ whale request start, url=[{}]{}, body={}", method, url, entity.getBody());
            ResponseEntity<BaseResult<T>> response = null;
            try {
                // 将目标类型转为BaseResult嵌套类型
                TypeReference<BaseResult<T>> baseResultTypeReference =
                        new TypeReference<BaseResult<T>>(this.typeReference.getType()){};
                response = restTemplate.exchange(url, method, entity, typeReferenceToParameterizedTypeReference(baseResultTypeReference));
            } catch (RestClientException e) {
                logger.error("~~ whale request error, url=({}){}, entity={}", method, url, entity, e);
                throw new BaseResultRestClientException(BaseResultUtils.buildBaseResult(CommonCodeEnum.REMOTE_SERVICE_UNAVAILABLE));
            }
            // 状态码不正确
            if (response.getStatusCode() != HttpStatus.OK) {
                logger.error(" ~~ whale request status code error, response url=[{}]{}, statusCode={}", method, url, response.getStatusCode());
                throw new BaseResultRestClientException(BaseResultUtils.buildBaseResult(CommonCodeEnum.REMOTE_SERVICE_FAILED));
            }


            BaseResult<T> baseResult = response.getBody();
            // 返回内容不正确
            if (baseResult == null || baseResult.getErrorCode() == null) {
                logger.error(">> whale request get invalid response, url=[{}]{}, baseResult={}, elapse={} ms",
                        method, url, baseResult, (System.currentTimeMillis() - start));
                throw new BaseResultRestClientException(baseResult);
            }

            if (!baseResult.getErrorCode().equals(CommonCodeEnum.SUCCESS.getCode())) {
                logger.warn(" ~~ whale request failed, url=({}){}, baseResult={}, elapse={} ms",
                        method, url, baseResult, (System.currentTimeMillis() - start));
                throw new BaseResultRestClientException(baseResult);
            }

            logger.debug("~~~ whale request success, url=({}){}, elapse={} ms",
                    method, url, (System.currentTimeMillis() - start));
            T result = baseResult.getData();
            return result;
        }


        protected static <K> ParameterizedTypeReference<K> typeReferenceToParameterizedTypeReference(final TypeReference<K> typeReference) {
            if (typeReference != null) {
                return (ParameterizedTypeReference<K>)new ParameterizedTypeReference<List<String>>() {
                    @Override
                    public Type getType() {
                        return typeReference.getType();
                    }
                };
            } else {
                throw new IllegalStateException("not specified response type!");
            }
        }

        @Override
        public T post() throws BaseResultRestClientException {
            return exchange(HttpMethod.POST);
        }

        @Override
        public T get() throws BaseResultRestClientException {
            return exchange(HttpMethod.GET);
        }

        @Override
        public T put() throws BaseResultRestClientException {
            return exchange(HttpMethod.PUT);
        }

        @Override
        public T delete() throws BaseResultRestClientException {
            return exchange(HttpMethod.DELETE);
        }



        public static class BaseResultRequestBuilder<T> extends Builder<T, BaseResultRequestBuilder<T>> {
            BaseResultRequestBuilder(RestTemplate restTemplate) {
                super(restTemplate);
            }

            @Override
            public BaseResultRestRequest<T> build() {
                return new BaseResultRestRequest<>(this);
            }
        }
    }


    @Override
    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

}
