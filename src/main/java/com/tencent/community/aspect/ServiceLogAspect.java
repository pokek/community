package com.tencent.community.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@Aspect
public class ServiceLogAspect {

    private static final Logger logger = LoggerFactory.getLogger(ServiceLogAspect.class);

    @Pointcut("execution(* com.tencent.community.service..*.*(..))")
    public void poinCut(){}

    @Before("poinCut()")
    public void log(JoinPoint jp){
        // 获取httprequest对象
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        // 消费线程消费时，没有请求传到service，就出现了空指针异常
        if(requestAttributes == null){
            return;
        }
        HttpServletRequest request = requestAttributes.getRequest();
        // 在[ip]的用户，[time]访问了[方法]
        String host = request.getRemoteHost();
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        Signature signature = jp.getSignature();
        String className = signature.getDeclaringTypeName();
        String methodName = signature.getName();
        String target = className + "." + methodName;
        logger.info(String.format("用户[%s]，在[%s]访问了该服务[%s]", host, time, target));

    }
}
