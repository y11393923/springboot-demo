package com.zyy.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.ToStringSerializer;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author:lianjinlin
 * @Description:
 * @Date: Created in 17:15 2019/4/22
 */
public class User implements Serializable {
    @JSONField(serializeUsing = ToStringSerializer.class)
    private Long id;
    private String name;
    private Date birthday;

    public User() {
    }

    public User(Long id, String name, Date birthday) {
        this.id = id;
        this.name = name;
        this.birthday = birthday;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", birthday=" + birthday +
                '}';
    }
}
