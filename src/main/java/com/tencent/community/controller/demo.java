package com.tencent.community.controller;

import ch.qos.logback.core.encoder.EchoEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
}
