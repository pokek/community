package com.tencent.community.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ErrorController {

    @RequestMapping(value = "/error/500", method = RequestMethod.GET)
    public String handleError(Model model){
        return "/error/500";
    }
}
