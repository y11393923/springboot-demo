package com.zyy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @Author:zhouyuyang
 * @Description:   对全局错误进行处理，但是其获取不到异常的具体信息，同时也无法根据异常类型进行不同的响应
 * @Date: Created in 14:25 2019/5/27
 */
@Controller
@ApiIgnore
public class ErrorController extends BasicErrorController{

    @Autowired
    public ErrorController(ErrorAttributes errorAttributes,
                           ServerProperties serverProperties,
                           List<ErrorViewResolver> errorViewResolvers) {
        super(errorAttributes,serverProperties.getError(), errorViewResolvers);
    }

    /**
     * web页面错误处理 404,405,500等
     * @param request
     * @param response
     * @return
     */
    @Override
    public ModelAndView errorHtml(HttpServletRequest request, HttpServletResponse response) {
        HttpStatus httpStatus = getStatus(request);
        Map<String, Object> errorAttributes = getErrorAttributes(request, isIncludeStackTrace(request, MediaType.TEXT_HTML));
        errorAttributes.put("custommsg","出错啦");
        ModelAndView modelAndView=new ModelAndView("error",errorAttributes,httpStatus);
        return modelAndView;
    }

    /**
     * web页面外的错误处理 json xml等
     * @param request
     * @return
     */
    @Override
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
        Map<String, Object> errorAttributes = getErrorAttributes(request, isIncludeStackTrace(request, MediaType.ALL));
        errorAttributes.put("custommsg","出错啦");
        HttpStatus httpStatus = getStatus(request);
        return new ResponseEntity<> (errorAttributes , httpStatus);
    }
}
