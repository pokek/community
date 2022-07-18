package com.tencent.community.controller;

import ch.qos.logback.core.encoder.EchoEncoder;
import com.tencent.community.util.CommunityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

@RestController
@RequestMapping("/test")
public class demo {

    @RequestMapping("/demo")
    public String test(){
        return "initialize success spring boot.";
    }

    @RequestMapping("/base")
    public void model(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){

        Enumeration<String> headerNames = httpServletRequest.getHeaderNames();
        while (headerNames.hasMoreElements()){
            String key = headerNames.nextElement();
            System.out.println(key + ":" + httpServletRequest.getHeader(key));
        }

        httpServletResponse.setContentType("text/html; charset=UTF-8");
        try(ServletOutputStream outputStream = httpServletResponse.getOutputStream()){
            outputStream.print("success");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/cookies/set", method = RequestMethod.GET)
    public String setCookies(HttpServletResponse response){
        Cookie cookie = new Cookie("code", CommunityUtils.getUUID());
        // 设置cookie
        cookie.setPath("/community/test");
        cookie.setMaxAge(60 * 10);
        // 加入cookie到响应
        response.addCookie(cookie);
        return "set cookie.";
    }

    @RequestMapping(value = "/cookies/get", method = RequestMethod.GET)
    public String getCookies(@CookieValue("code") String code){
        System.out.println(code);
        return "get cookie.";
    }

    @RequestMapping(value = "/session/set", method = RequestMethod.GET)
    public String setSession(HttpSession session){
        session.setAttribute("name", "xiaokan");
        session.setAttribute("id", 1);
        return "set session.";
    }

    @RequestMapping(value = "/session/get", method = RequestMethod.GET)
    public  String getSession(HttpSession session){
        Object name = session.getAttribute("name");
        System.out.println(name);
        return "get session.";
    }
}
