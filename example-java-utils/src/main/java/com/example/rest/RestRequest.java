package com.example.rest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.lang.reflect.Type;
import java.util.*;

/**
 * RestRequest
 *
 */
@Slf4j
public abstract class RestRequest<T> {

    public static final List<HttpMethod> HAS_BODY_METHOD = Arrays.asList(
            HttpMethod.POST, HttpMethod.PUT, HttpMethod.PATCH);

    protected RestTemplate restTemplate;

    protected String url;
    protected TypeReference<T> typeReference;
    protected Object requestBody;
    protected MultiValueMap<String, Object> fileMap = new LinkedMultiValueMap<>();
    protected MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<String, String>();
    protected List<Object> urlVariables = new ArrayList<>();
    protected HttpHeaders headers = new HttpHeaders();
    protected boolean writeParamToUrl = false;

    protected RestRequest(Builder builder) {
        this.restTemplate = builder.restTemplate;

        this.url = builder.url;
        this.typeReference = builder.typeReference;
        this.requestBody = builder.requestBody;
        this.fileMap = builder.fileMap;
        this.paramMap = builder.paramMap;
        this.urlVariables = builder.urlVariables;
        this.headers = builder.headers;
    }


    /**
     * Post请求
     * @return
     * @throws RestClientException
     */
    public T post() throws RestClientException {
        return exchange(HttpMethod.POST);
    }


    /**
     * Get请求
     * @return
     * @throws RestClientException
     */
    public T get() throws RestClientException {
        return exchange(HttpMethod.GET);
    }


    /**
     * Put请求
     * @return
     * @throws RestClientException
     */
    public T put() throws RestClientException {
        return exchange(HttpMethod.PUT);
    }


    /**
     * Delete请求
     * @return
     * @throws RestClientException
     */
    public T delete() throws RestClientException {
        return exchange(HttpMethod.DELETE);
    }


    /**
     * 请求操作
     * @param method
     * @return
     * @throws RestClientException
     */
    public T exchange(HttpMethod method) throws RestClientException {
        HttpEntity entity = buildHttpEntitiy(method);
        String url = concreteUrl(writeParamToUrl);

        ParameterizedTypeReference<T> parameterizedTypeReference = getParameterizedTypeReference();
        T t = doExchange(url, method, entity, parameterizedTypeReference);
        return t;
    }



    protected <R> R doExchange(
            String url,
            HttpMethod method,
            HttpEntity entity,
            ParameterizedTypeReference<R> parameterizedTypeReference) throws RestClientException {
        log.info("Rest request(url=[{}]{}), body={})", method, url, entity.getBody());
        ResponseEntity<R> response = null;
        try {
            response = restTemplate.exchange(url, method, entity, parameterizedTypeReference);
        } catch (org.springframework.web.client.RestClientException e) {
            log.error("Rest request(url=[{}]{})) error", method, url, e);
            throw new RestClientException("service is not available");
        }

        if (response.getStatusCode() != HttpStatus.OK) {
            log.error("Rest request(url=[{}]{})) error, statusCode={}", method, url, response.getStatusCode());
            throw new RestClientException("service is not available");
        }
        return response.getBody();
    }


    protected HttpEntity buildHttpEntitiy(HttpMethod method) {
        HttpEntity entity = null;
        boolean hasBody = hasBody(method);
        // 文件上传
        if(fileMap != null && !fileMap.isEmpty()) {
            if (!hasBody) {
                throw new IllegalStateException(method + " method not supported file upload!");
            }
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            LinkedMultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
            param.putAll(fileMap);
            paramMap.forEach((k, list) -> {
                list.forEach(v -> param.add(k, v));
            });
            entity = new HttpEntity(param, headers);
        }
        // json请求
        else if (requestBody != null) {
            if (!hasBody) {
                throw new IllegalStateException(method + " method not support request json body!");
            }
            headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
            entity = new HttpEntity(requestBody, headers);
            writeParamToUrl = true;
        }
        // 表单请求
        else if(hasBody) {
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//            // TODO DELETE不支持表单提交
//            entity = new HttpEntity(headers);
//            writeParamToUrl = true;

            entity = new HttpEntity(paramMap, headers);
            writeParamToUrl = false;
        }
        else {
            entity = new HttpEntity(headers);
            writeParamToUrl = true;
        }
        return entity;
    }


    protected ParameterizedTypeReference<T> getParameterizedTypeReference() {
        return typeReferenceToParameterizedTypeReference(typeReference);
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

    /**
     * 是否含有body
     *
     * @param method
     * @return
     */
    protected boolean hasBody(HttpMethod method) {
        return HAS_BODY_METHOD.contains(method);
    }


    protected String concreteUrl(boolean writeParamToUrl) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url);
        // 将参数写入链接
        if (writeParamToUrl) {
            uriBuilder.queryParams(paramMap);
        }
        UriComponents uriComponents = null;
        Object[] urlVariables = this.urlVariables.toArray();
        if (urlVariables != null && urlVariables.length > 0) {
            uriComponents = uriBuilder.buildAndExpand(urlVariables);
        } else {
            uriComponents = uriBuilder.build();
        }
        return uriComponents.toUriString();
    }


    public static abstract class Builder<T, B extends Builder<T, B>> {
        protected RestTemplate restTemplate;

        protected String url;
        protected TypeReference<T> typeReference;
        protected Object requestBody;
        protected MultiValueMap<String, Object> fileMap = new LinkedMultiValueMap<>();
        protected MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<String, String>();
        protected List<Object> urlVariables = new ArrayList<>();
        protected HttpHeaders headers = new HttpHeaders();

        protected Builder(RestTemplate restTemplate) {
            this.restTemplate = restTemplate;
        }

        /**
         * 构建请求
         * @return
         */
        public abstract RestRequest<T> build();

        /**
         * 请求连接
         * @param url
         * @return
         */
        public B url(String url) {
            this.url = url;
            return self();
        }

        /**
         * 设置头部信息
         * @param name
         * @param value
         * @return
         */
        public B header(String name, String... value) {
            Objects.requireNonNull(name);
            Objects.requireNonNull(value);
            Arrays.stream(value).forEach(v -> headers.add(name, v));
            return self();
        }

        /**
         * 返回类型
         * @param responseType
         * @return
         */
        public B responseType(Class<T> responseType) {
            this.typeReference = typeToTypeReference(responseType);
            return self();
        }

        /**
         * 返回类型
         * @param typeReference
         * @return
         */
        public B responseType(TypeReference<T> typeReference) {
            this.typeReference = typeReference;
            return self();
        }

        /**
         * 请求对象
         * <p>
         *     使用body参数，param参数将以url参数形式请求
         * </p>
         * @param object
         * @return
         */
        public B body(Object object) {
            this.requestBody = object;
            return self();
        }

        /**
         * 上传文件
         * <p>
         *     如果调用file，body参数将失效
         * </p>
         * @param name
         * @param file
         * @return
         */
        public B file(String name, File file) {
            if (file != null) {
                fileMap.add(name, new FileSystemResource(file));
            }
            return self();
        }

        /**
         * 请求参数
         * @param name
         * @param value
         * @return
         */
        public B param(String name, Object value) {
            if (value != null) {
                this.paramMap.putAll(object2MultiValueMap(name, value));
            }
            return self();
        }

        /**
         * 请求参数
         * @param paramObject
         * @return
         */
        public B param(Object paramObject) {
            return param(null, paramObject);
        }

        /**
         * 请求参数
         * @param paramMap
         * @return
         */
        public B paramMap(Map<String, Object> paramMap) {
            if (paramMap != null) {
                paramMap.forEach((k, v) -> {
                    if(v != null) {
                        param(k, v);
                    }
                });
            }
            return self();
        }

        /**
         * 链接请求变量
         * @param urlVariable
         * @return
         */
        public B urlVariable(Object... urlVariable) {
            this.urlVariables.addAll(Arrays.asList(urlVariable));
            return self();
        }

        protected B self() {
            return (B)this;
        }


        public MultiValueMap<String, String> object2MultiValueMap(String name, Object param) {
            if (param == null) {
                return new LinkedMultiValueMap<>();
            }
            MultiValueMap<String, String> mvm = new LinkedMultiValueMap<>();
            doObject2MultiValueMap(name, param, mvm);
            return mvm;
        }


        private void doObject2MultiValueMap(String name, Object object, MultiValueMap<String, String> mvm) {
            Object json = JSON.toJSON(object);
            if(json instanceof JSON) {
                if (json instanceof JSONObject) {
                    JSONObject jsonObject = (JSONObject)json;
                    // 名称前缀
                    String namePrefix = StringUtils.isEmpty(name) ? "" : (name + ".");
                    jsonObject.forEach((key, value) -> {
                        doObject2MultiValueMap(namePrefix + key, value, mvm);
                    });
                } else if (json instanceof JSONArray) {
                    JSONArray jsonArray = (JSONArray)json;
                    if (!StringUtils.isEmpty(name)) {
                        jsonArray.forEach(obj -> {
                            doObject2MultiValueMap(name, obj, mvm);
                        });
                    }
                }
            }
            // 原始类型对象，包装类对象，枚举类型对象
            else {
                if (!StringUtils.isEmpty(name) && json != null) {
                    mvm.add(name, String.valueOf(json));
                }
            }
        }

        private TypeReference<T> typeToTypeReference(Class<T> responseType) {
            return new TypeReference<T>(){
                @Override
                public Type getType() {
                    return responseType;
                }
            };
        }
    }

}
