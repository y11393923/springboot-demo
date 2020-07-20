package com.example.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: zhouyuyang
 * @Date: 2020/7/20 16:49
 */
public class ExcelConvertResponse<T> {
    /**Excel根据类型转化的list list.size()小于等于size**/
    private List<T> succeessList;

    /**size为Excel中实际的行数**/
    private int size;

    /**Excel转换错误信息**/
    private List<ExcelHandleError> errorResultList = new ArrayList<ExcelHandleError>();

    public ExcelConvertResponse() {
    }

    public ExcelConvertResponse(List<T> succeessList, int size, List<ExcelHandleError> errorResultList) {
        this.succeessList = succeessList;
        this.size = size;
        this.errorResultList = errorResultList;
    }

    public List<T> getSucceessList() {
        return this.succeessList;
    }

    public void setSucceessList(List<T> succeessList) {
        this.succeessList = succeessList;
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public List<ExcelHandleError> getErrorResultList() {
        return this.errorResultList;
    }

    public void setErrorResultList(List<ExcelHandleError> errorResultList) {
        this.errorResultList = errorResultList;
    }
}
