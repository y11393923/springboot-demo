package com.zyy.enums;

public enum ResultCode {
    SUCCESS(true,200,"操作成功"),
    FAIL(false,500,"操作失败"),
    TIME_OUT(false,1001,"服务器连接超时"),
    SERVER_ERROR(false,500,"抱歉，系统繁忙，请稍后重试"),
    THE_ACCOUNT_IS_LOCKED(false,1000,"账户被锁定，请联系管理员"),
    PASSWORD_EXPIRES(false,1001,"密码过期，请联系管理员"),
    ACCOUNT_EXPIRES(false,1002,"账户过期，请联系管理员"),
    ACCOUNT_DISABLED(false,1003,"账户被禁用，请联系管理员"),
    ERROR_ACCOUNT_NAME_OR_PASSWORD(false,1004,"用户名或者密码错误"),
    LOGIN_SUCCESSFULLY(true,1005,"登录成功"),
    INSUFFICIENT_PRIVILEGES(false,1006,"权限不足，请联系管理员"),
    NOT_LOGIN(false,1007,"未登录");

    /**
     * 操作是否成功
     */
    boolean success;
    /**
     * 操作代码
     */
    int code;
    /**
     * 提示信息
     */
    String message;

    private ResultCode(boolean success, int code, String message){
        this.success = success;
        this.code = code;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
