package com.zyy.entity;

import com.baomidou.mybatisplus.annotation.Version;

import java.io.Serializable;

/**
 * @Author:zhouyuyang
 * @Description:
 * @Date: Created in 18:29 2019/5/20
 */
public class User implements Serializable {
    private Long id;
    private String userName;
    private String password;
    @Version
    private Integer version;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", version=" + version +
                '}';
    }
}
