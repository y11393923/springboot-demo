package com.example.vo;

import com.alibaba.fastjson.JSON;

/**
 * @Author: zhouyuyang
 * @Date: 2020/7/20 17:47
 */
public class HttpResult {
    private int status;
    private String data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
    public String getErrorReason() {
        return JSON.parseObject(this.getData()).getString("error");
    }
}
