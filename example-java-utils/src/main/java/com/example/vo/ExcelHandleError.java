package com.example.vo;

/**
 * @Author: zhouyuyang
 * @Date: 2020/7/20 16:50
 */
public class ExcelHandleError {
    /**行信息**/
    private String rowMsg;

    /**列信息**/
    private String columnMsg;

    /**错误信息**/
    private String errorMsg;

    public ExcelHandleError() {
    }

    public ExcelHandleError(String rowMsg, String columnMsg, String errorMsg) {
        this.rowMsg = rowMsg;
        this.columnMsg = columnMsg;
        this.errorMsg = errorMsg;
    }

    public String getRowMsg() {
        return this.rowMsg;
    }

    public void setRowMsg(String rowMsg) {
        this.rowMsg = rowMsg;
    }

    public String getColumnMsg() {
        return this.columnMsg;
    }

    public void setColumnMsg(String columnMsg) {
        this.columnMsg = columnMsg;
    }

    public String getErrorMsg() {
        return this.errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
