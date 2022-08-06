package com.tencent.community.controller.interceptor;

import com.tencent.community.domain.LoginTicket;
import com.tencent.community.domain.User;
import com.tencent.community.service.UserService;
import com.tencent.community.util.CookiesUtils;
import com.tencent.community.util.HostHolder;
import org.apache.catalina.Host;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/*
        利用拦截器，拦截获取拥有登录凭证的用户，并展现用户信息
 */
@Component
public class UserInterceptor implements HandlerInterceptor {

    @Autowired
    UserService us;

    @Autowired
    HostHolder hd;

    /*
       ! springboot每个请求为一个线程的话，单例对象如何形成多线程
     */

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ticket = CookiesUtils.getCookieValue(request, "ticket");
        /*
            凭证不为null时进行拦截处理
            ! 登录就生成一张凭证，登出该凭证位于失效状态，重新登陆就会生成一张新的凭证
         */
        if(ticket != null){
            LoginTicket loginTicket = us.findTicket(ticket);
            if(loginTicket != null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())){
                User user = us.findUserById(loginTicket.getUserId());
                /*
                    绑定用户信息到当前请求线程
                    ! threadlocal如何实现线程隔离了？它在springboot中不是只有一个实例吗？当同一个浏览器发出多个请求的时候，它是如何
                    ! 绑定用户信息的？并发处理请求如何进行的？？
                 */
                hd.set(user);

                // 构建用户认证的结果,并存入SecurityContext,以便于Security进行授权.
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        user, user.getPassword(), us.getAuthorities(user.getId()));
                SecurityContextHolder.setContext(new SecurityContextImpl(authentication));
            }
        }
        /*
            凭证为null时，不进行拦截去展现用户信息，展示未登录状态页面
         */
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hd.get();
        /*
        ! 控制台输出是个好东西！！！！  GET "/community/js/global.js"  因为拦截器并不拦截静态资源且当前http协议为1.1版本
         */
//        System.out.println(hd);
        if(user != null && modelAndView != null){
            modelAndView.addObject("loginUser", user);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hd.clear();
        SecurityContextHolder.clearContext();
    }
}
