package com.zyy.aspect;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.zyy.mqtt.RabbitMqSender;
import com.zyy.utils.IPUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author:zhouyuyang
 * @Description:
 * @Date: Created in 12:24 2019/5/31
 */
@Component
@Aspect
public class AccessLogAspect {
    private final Logger log = LoggerFactory.getLogger(AccessLogAspect.class);
    @Autowired
    private RabbitMqSender rabbitMqSender;
    /**
     * 定义切入点为 controller 包下所有类中的所有方法
     */
    @Pointcut("execution(public * com.zyy.controller.*.*(..)) && " +
            "@annotation(org.springframework.web.bind.annotation.RequestMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.GetMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping)")
    public void pointcut(){
    }


    @Around("pointcut()")
    public Object handler(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        String ip = IPUtil.getIpAddress(request);
        String url = request.getRequestURL().toString();
        String uri = request.getRequestURI();
        //获取方法所在的类 带包名
        String className = methodSignature.getDeclaringType().getName();
        //获取方法
        String methodName = className + "." + methodSignature.getMethod().getName();
        String params = getParams(proceedingJoinPoint,methodSignature);
        String format = String.format("IP:%s, URL:%s, Method:%s, Params:%s", ip, url, methodName, params);
        rabbitMqSender.convertAndSend("log.info",format);
        long startTime = System.currentTimeMillis();
        Object proceed = proceedingJoinPoint.proceed();
        long endTime = System.currentTimeMillis();
        log.info("URL:{}, URI:{}, Method:{}, Take up time:{}", url, uri, methodName, (endTime - startTime) + "ms");
        return proceed;
    }

    /**
     * 获取方法参数值返回json
     * @param joinPoint
     * @return
     */
    private String getParams(ProceedingJoinPoint joinPoint,MethodSignature methodSignature) {
        //获取参数值
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length==0) {
            return null;
        }
        JSONObject params = new JSONObject();
        //对象接收参数
        try {
            Object object = args[0];
            if (object instanceof HttpServletRequest || object instanceof HttpServletResponse){
                return null;
            }
            if (object instanceof MultipartFile){
                return "File文件";
            }
            String data = JSON.toJSONString(object);
            params = JSON.parseObject(data);
        }
        //普通参数传入
        catch (JSONException e){
            //获取参数名
            for(int i = 0;i < methodSignature.getParameterNames().length; i++){
                params.put(methodSignature.getParameterNames()[i],args[i]);
            }
        }
        return params.toJSONString();
    }
}
