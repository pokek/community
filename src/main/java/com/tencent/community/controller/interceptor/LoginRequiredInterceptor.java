package com.tencent.community.controller.interceptor;

import com.tencent.community.annotation.LoginRequired;
import com.tencent.community.domain.User;
import com.tencent.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {

    @Autowired
    HostHolder hd;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        /*
            拦截器的拦截路径是由配置中决定，注解只是为了标识这个是我需要拦截的，判断逻辑还是在实现的拦截器类中
         */
        // 拦截所需要拦截的请求
        if(handler instanceof HandlerMethod){
            HandlerMethod method = (HandlerMethod) handler;
            User user = hd.get();
            if(method.hasMethodAnnotation(LoginRequired.class) && user == null){
                response.sendRedirect(request.getContextPath() + "/login");
                return false;
            }
        }
        return true;
    }
}
