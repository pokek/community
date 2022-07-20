package com.tencent.community.util;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class CookiesUtils {

    public static String getCookieValue(HttpServletRequest request, String name){
        // 判断参数是否合法
        if(request == null || StringUtils.isBlank(name)){
            throw new IllegalArgumentException("参数不合法");
        }
        Cookie[] cookies = request.getCookies();
        if(cookies == null){
            return null;
        }
        /*
               数组为null时遍历会出现空指针异常，但长度为0时不会
         */
        for(Cookie cookie : cookies){
            if(cookie.getName().equals(name)){
                return cookie.getValue();
            }
        }
        return null;
    }
}
