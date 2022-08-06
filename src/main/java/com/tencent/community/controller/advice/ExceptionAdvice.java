package com.tencent.community.controller.advice;

import com.tencent.community.util.CommunityUtils;
import com.tencent.community.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


/*

    为加上controller注解的类进行统一异常处理，不指定注解class的话，默认service等注解修饰的类也进行处理，处理方法需用@ExceptionHandler;
    @ModelAttribute 修饰方法，在controller方法执行前执行，将绑定的方法返回值加入model中；
    @Databinder 修饰方法，在controller方法执行前执行，将请求参数进行转换;

 */
@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    @Value("${server.servlet.context-path}")
    String contextPath;


    /*
        指定处理哪些异常类型
     */
    @ExceptionHandler({Exception.class})
    public void handleException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.error("服务器发生异常", e.getMessage());

        // 记录异常堆栈信息
        for(StackTraceElement ele : e.getStackTrace()){
            logger.error(ele.toString());
        }

        // 获取请求是普通请求还是  异步请求
        String header = request.getHeader("x-requested-with");
        // XMLHttpRequest为异步请求
        if("XMLHttpRequest".equals(header)){
            // 设定响应  application/json响应json对象，不用js处理；application/plain则服务器响应json格式字符串，需js处理成json对象
            response.setContentType("application/plain");
            PrintWriter writer = response.getWriter();
            writer.println(JsonUtils.getJsonString(1, "服务器出现故障"));
        }else{
            response.sendRedirect(contextPath + "/error/500");
        }


    }
}
