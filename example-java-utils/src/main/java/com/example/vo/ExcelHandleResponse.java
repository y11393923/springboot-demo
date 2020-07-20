package com.example.vo;

/**
 * @Author: zhouyuyang
 * @Date: 2020/7/20 16:50
 */
public class ExcelHandleResponse<T> {
    private boolean success;
    private T data;

    public ExcelHandleResponse() {
    }

    public boolean isSuccess() {
        return this.success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
