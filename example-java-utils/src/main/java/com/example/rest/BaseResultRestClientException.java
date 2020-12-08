package com.example.rest;


import com.example.common.BaseResult;

/**
 * BaseResultRestClientException
 *
 */
public class BaseResultRestClientException extends RestClientException {

    private BaseResult<?> result;

    public BaseResultRestClientException(BaseResult<?> result) {
        super("code=" + result.getErrorCode() + "; msg=" + result.getErrorMsg());
        this.result = result;
    }

    public BaseResultRestClientException(String message, BaseResult<?> result) {
        super(message);
        this.result = result;
    }

    public BaseResultRestClientException(String message, Throwable cause, BaseResult<?> result) {
        super(message, cause);
        this.result = result;
    }

    public BaseResultRestClientException(Throwable cause, BaseResult<?> result) {
        super(cause);
        this.result = result;
    }


    public <T> BaseResult<T> getBaseResult() {
        return (BaseResult<T>)result;
    }
}
