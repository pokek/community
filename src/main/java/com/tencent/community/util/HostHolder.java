package com.tencent.community.util;

import com.tencent.community.domain.User;
import org.springframework.stereotype.Component;


/*
    !  为什么封装一个对象放在容器中?????   托管给容器管理，并且是单例，所有成员变量，但是threadlocal就是为当前线程绑定变量即可认为是当前线程变量，是线程安全的
    ! tomcat中的请求线程池为 nio线程池
 */
@Component
public class HostHolder {

    /*
        ! threadlocal为线程中绑定的局部变量名，有几个就创建几个绑定到该线程中
        ! 本质就是替代session  session的底层获取 HttpServerletRequest.getSession()
        ! 一个浏览器和服务器之间关系  和  多个浏览器和服务器之间关系？？？？？？？
        存放用户信息的容器

     */
    private ThreadLocal<User> threadLocal = new ThreadLocal<>();

    public void set(User user){
        threadLocal.set(user);
    }

    public User get(){
        User user = threadLocal.get();
        return user;
    }

    public void clear(){
        threadLocal.remove();
    }

}
