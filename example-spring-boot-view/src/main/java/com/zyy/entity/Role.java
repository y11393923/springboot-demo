package com.zyy.entity;

import java.io.Serializable;

public class Role implements Serializable {
    private Integer id;
    private String name;
    private String nameRemark;
    private Integer status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameRemark() {
        return nameRemark;
    }

    public void setNameRemark(String nameRemark) {
        this.nameRemark = nameRemark;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

}
