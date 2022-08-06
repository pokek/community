package com.tencent.community.controller;

import com.tencent.community.annotation.LoginRequired;
import com.tencent.community.domain.User;
import com.tencent.community.service.FollowService;
import com.tencent.community.service.LikeService;
import com.tencent.community.service.UserService;
import com.tencent.community.util.CommunityConstant;
import com.tencent.community.util.CommunityUtils;
import com.tencent.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {

    @Value("${communtiy-upload-path}")
    private String uploadPath;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserService us;

    @Value("${community-domain}")
    String domain;

    @Value("${server.servlet.context-path}")
    String contextPath;

    @Autowired
    HostHolder hd;

    @Autowired
    LikeService likeService;

    @Autowired
    FollowService followService;

    @LoginRequired
    @RequestMapping(value = "/setting", method = RequestMethod.GET)
    public String getSettingPage(){
        return "/site/setting";
    }

    @LoginRequired
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String upload(MultipartFile headImage, Model model){
        // 图片为空
        if(headImage == null){
            model.addAttribute("error", "您上传的图片不能空");
            return "/site/setting";
        }
        // 图片格式
        String fileName = headImage.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if(!(".png".equals(suffix))){
            model.addAttribute("error", "只支持上传后缀为png格式文件");
            return "/site/setting";
        }

        // 保存图片到本地
        fileName = CommunityUtils.getUUID() + suffix;
        String path = uploadPath + "/" + fileName;
        File des = new File(path);
        try {
            headImage.transferTo(des);
        } catch (IOException e) {
            logger.error("文件上传遇到系统错误", e);
            throw new RuntimeException("文件上传遇到系统错误");
        }

        // 更新用户headerUrl
        String headerUrl = domain + contextPath + "/" + "user/header/" + fileName;
        User user = hd.get();
        us.updateUserHeaderUrl(user.getId(), headerUrl);
        return "redirect:/index";
    }

    @RequestMapping(value = "/header/{filename}", method = RequestMethod.GET)
    public void getHeader(HttpServletResponse response, @PathVariable("filename") String filename){
        // 打开本地文件输入流
        String path = uploadPath + "/" + filename;
        // 设置响应格式
        response.setContentType("image/png");
        try(
                FileInputStream src = new FileInputStream(path);
                // 本地文件响应客户端
                ServletOutputStream des = response.getOutputStream();
                )
        {
            byte[] buffer = new byte[1024];
            int b = 0;
            while((b = src.read(buffer)) != -1){
                des.write(buffer);
            }
        } catch (Exception e) {
            logger.error("头像下载遇到系统错误", e);
        }
    }

    @RequestMapping(value = "/updatepassword", method = RequestMethod.POST)
    public String updatePassword(String password, String newPassword, Model model, @CookieValue("ticket") String ticket){
        if(StringUtils.isBlank(password)){
            model.addAttribute("passworderror", "请输入原始密码");
            return "/site/setting";
        }
        User user = hd.get();
        if(!((CommunityUtils.md5(password + user.getSalt())).equals(user.getPassword()))){
            model.addAttribute("passworderror", "原始密码错误，请重新输入");
            return "/site/setting";
        }

        if(StringUtils.isBlank(newPassword)){
            model.addAttribute("newerror", "密码不能为空");
            return "/site/setting";
        }
        // 使得凭证失效
        us.updateTicketStaus(ticket);
        // 更新密码
        us.updateUserPassword(user, newPassword);
        return "redirect:/index";
    }

    @GetMapping("/profile/{userId}")
    public String getUserProfile(Model model, @PathVariable("userId") int userId){
        int userLikeCount = likeService.getUserLikeCount(userId);
        model.addAttribute("user", us.findUserById(userId));
        model.addAttribute("loginUser", hd.get());
        // 点赞数
        model.addAttribute("likeCount", userLikeCount);
        // 关注数
        long followeeCount = followService.followeeCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount", followeeCount);
        // 粉丝数
        long followerCount = followService.followerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount", followerCount);
        // 是否关注
        Boolean hasFollowed = followService.isFollowed(ENTITY_TYPE_USER, userId);
        model.addAttribute("hasFollowed", hasFollowed);
        return "/site/profile";
    }
}
