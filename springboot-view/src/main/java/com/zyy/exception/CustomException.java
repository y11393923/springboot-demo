package com.zyy.exception;

import com.zyy.enums.ResultCode;

/**
 * @Author:zhouyuyang
 * @Description:    自定义异常类
 * @Date: Created in 15:07 2019/5/27
 */
public class CustomException extends RuntimeException {

    private ResultCode resultCode;

    public CustomException(ResultCode resultCode){
        super("错误代码：" + resultCode.getCode()+" 错误信息：" + resultCode.getMessage());
        this.resultCode=resultCode;
    }
    public CustomException(String message){
        super("错误信息：" + message);
    }

    public ResultCode getResultCode() {
        return resultCode;
    }


}
