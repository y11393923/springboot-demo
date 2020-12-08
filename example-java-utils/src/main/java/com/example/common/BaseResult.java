package com.example.common;


import com.example.common.enums.CommonCodeEnum;

import java.io.Serializable;

public class BaseResult<T> implements Serializable {
    /**
     * 返回异常码
     */
    private String errorCode="0";
    /**
     * 返回异常消息
     */
    private String errorMsg;
    /**
     * 返回具体的业务数据
     */
    private T data;

    /**
     * 返回具体的业务数据
     */
    private String detail;



    public boolean isSuccess(){
        return errorCode.equals(CommonCodeEnum.SUCCESS.getCode());
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    @Override
    public String toString() {
        return "BaseResult{" +
                "errorCode='" + errorCode + '\'' +
                ", errorMsg='" + errorMsg + '\'' +
                ", data=" + data +
                '}';
    }
}
