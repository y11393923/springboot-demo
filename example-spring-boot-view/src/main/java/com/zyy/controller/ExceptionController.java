package com.zyy.controller;

import com.zyy.exception.CustomException;
import com.zyy.response.ResponseResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author:zhouyuyang
 * @Description: 添加全局异常处理逻辑
 * @Date: Created in 15:12 2019/5/27
 */
@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(CustomException.class)
    @ResponseBody
    public Object customException(CustomException e){
        if (e.getResultCode() != null){
            return new ResponseResult(e.getResultCode());
        }
        return new ResponseResult(401,e.getMessage(),false);
    }
}
