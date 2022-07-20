package com.tencent.community.controller;

import com.google.code.kaptcha.Producer;
import com.tencent.community.dao.TicketMapper;
import com.tencent.community.domain.LoginTicket;
import com.tencent.community.domain.User;
import com.tencent.community.service.UserService;
import com.tencent.community.util.CommunityConstant;
import com.tencent.community.util.MailClient;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService us;

    @Autowired
    private Producer kaptcha;

    @Autowired
    private TicketMapper ticketMapper;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    MailClient sendMail;


    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String registerPage(){
        return "/site/register";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String getLoginPage(){

        return "/site/login";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String register(Model model, User user){
        Map<String, String> resmeg = us.register(user);
        if(resmeg != null){
            model.addAllAttributes(resmeg);
            return "/site/register";
        }else{
            String target = "/index";
            model.addAttribute("target", target);
            model.addAttribute("mes", "您的账户已经创建，请尽快激活!");
            return "/site/operate-result";
        }
    }

    @RequestMapping(value = "/activation/{userId}/{activationCode}", method = RequestMethod.GET)
    public String action(Model model, @PathVariable("userId") int userId, @PathVariable("activationCode") String activationCode){
        int acStatus = us.activation(userId, activationCode);
        if(acStatus == ACTIVATION_SUCCESS){
            model.addAttribute("target", "/login");
            model.addAttribute("mes", "您的账户激活成功，可以登录!");
        }else if(acStatus == ACTIVATION_REPEAT){
            model.addAttribute("target", "/index");
            model.addAttribute("mes", "您的账户已经激活过，请勿重复激活!");
        }else{
            model.addAttribute("target", "/index");
            model.addAttribute("mes", "激活失败，您的激活码不正确!");
        }
        return "/site/operate-result";
    }

    /**
     * 获取验证码
     * @param response 响应体对象
     * @param session session对象
     */
    // html中的流式数据通过请求输出在对应的html文本中 对应th:src标签
    // spring mvc中的model是为了产生动态数据类型页面，通过往域中存入对象达到动态数据效果，响应html格式文件
    @RequestMapping(value = "/kaptcha", method = RequestMethod.GET)
    public void getKapt(HttpServletResponse response, HttpSession session){
        String text = kaptcha.createText();
        BufferedImage image = kaptcha.createImage(text);
        session.setAttribute("kaptcha", text);
        // 设置响应文件格式
        response.setContentType("image/png");
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            // 将图片输入响应流中
            ImageIO.write(image, "png", outputStream);
        } catch (IOException e) {
            logger.error("生成验证码异常" + e.getMessage());
        }
    }

/*
    spring mvc 对基本参数类型的值，不储存到model中，要在页面获取的话可以加入map中，然后加入model中
    或者 使用thyemleaf的 param对象
    参数中自定义的对象会自动加入model中

    */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(String username, String password, boolean isLongTimeStorage,
                        String code, HttpSession session, HttpServletResponse response,
                        Model model){
        if(StringUtils.isBlank(code)){
            model.addAttribute("codemes", "验证码不能为空");
            /**
             *  这为转发请求，直接返回动态页面就好
             */
//            return "/login";
            return "/site/login";
        }
        if(!code.equalsIgnoreCase((String) session.getAttribute("kaptcha"))){
            model.addAttribute("codemes", "验证码错误");
            return "/site/login";
        }
        int storageTimeOut = isLongTimeStorage == true ? LONG_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;
        Map<String, String> maps = us.login(username, password, storageTimeOut);
        if(maps.containsKey("ticket")){
            Cookie cookie = new Cookie("ticket", maps.get("ticket"));
            // 设置cookie
            cookie.setPath(contextPath);
            cookie.setMaxAge(storageTimeOut);
            response.addCookie(cookie);
            return "redirect:/index";
        }else{
            model.addAllAttributes(maps);
            return "/site/login";
        }
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logOut(@CookieValue("ticket") String ticket){
        us.updateTicketStaus(ticket);
        return "redirect:/login";
    }

    /*

     // TODO: 2022/7/20 忘记密码功能
     */
//    @RequestMapping(value = "/forget", method = RequestMethod.GET)
//    public String forget(){
//        return "/site/forget";
//    }
//
//    @RequestMapping(value = "/forget", method = RequestMethod.POST)
//    public String forget(Model model, String newPassword, String email, String code){
//
//        return null;
//    }
//
//    @RequestMapping(value = "/sendcode", method = RequestMethod.GET)
//    public String sendCode(Model model){
//
//    }


}
