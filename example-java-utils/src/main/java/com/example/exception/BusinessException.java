package com.example.exception;

public class BusinessException extends RuntimeException {
    /**
     * 这里给出一个基础异常码,系统异常
     */
    private String code="500";

    private String message;

    public BusinessException(String message){
        super(message);
        this.message=message;
    }

    public BusinessException(String errCode, String message) {
        super(message);
        this.code = errCode;
        this.message = message;
    }

    /**
     * 构造一个基本异常.
     *
     * @param errorCode
     *            错误编码
     * @param message
     *            信息描述
     */
    public BusinessException(String errorCode, String message, Throwable cause)
    {
        super(message, cause);
        this.code=errorCode;
        this.message=message;
    }

    public String getCode() {
        return code;
    }

}
