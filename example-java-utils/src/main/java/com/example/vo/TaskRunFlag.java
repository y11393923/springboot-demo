package com.example.vo;

/**
 * @Author: zhouyuyang
 * @Date: 2020/7/20 17:10
 */
public class TaskRunFlag {
    private boolean flag = true;
    private String description;

    public TaskRunFlag() {
    }

    public boolean isFlag() {
        return this.flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
