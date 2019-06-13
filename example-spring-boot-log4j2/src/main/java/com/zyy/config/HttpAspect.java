package com.zyy.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Administrator
 * @create 2018/12/11
 * @since 1.0.0
 */
@Aspect
@Component
public class HttpAspect {
    private final Logger log = LogManager.getLogger(HttpAspect.class);

    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void log(){
    }

    @Around("log()")
    public Object around(ProceedingJoinPoint pjp) {
        String url="";
        String params="";
        Class<?> aClass=null;
        String method="";
        try {
            aClass = pjp.getTarget().getClass();//获取类名
            method = pjp.getSignature().getName();//获取方法名
            RequestAttributes ra = RequestContextHolder.getRequestAttributes();
            ServletRequestAttributes sra = (ServletRequestAttributes) ra;
            HttpServletRequest request = sra.getRequest();
            params = request.getQueryString();
            url = request.getRequestURL().toString();
            log.info("请求开始, 各个参数, url: {}, class: {},method: {}, params: {}",url,aClass,method,params);
        }catch (Exception e){
            log.info("接口日志审计：根据request获取请求ip和请求参数时异常");
        }
        try {
            Object object = pjp.proceed();
            return object;
        } catch (Throwable e) {
            log.info("接口异常, url: {}, class: {},method: {},异常信息: {}",url,aClass,method,e);
        }

        return null;
    }
}
