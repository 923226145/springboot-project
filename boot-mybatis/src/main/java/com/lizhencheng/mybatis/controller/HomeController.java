package com.lizhencheng.mybatis.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lizhencheng.mybatis.bean.User;
import com.lizhencheng.mybatis.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Created by lzc
 * 2020/3/14 20:07
 */
@Controller
public class HomeController {
    @Autowired
    private UserService userService;
    @GetMapping("/home")
    public String home(Model model,
                       @RequestParam(name = "page", defaultValue = "1")Integer page,
                       @RequestParam(name = "size", defaultValue = "5")Integer size) {
        PageHelper.startPage(page, size);
        PageInfo<User> userPageInfo = new PageInfo<>(userService.getUserList());
        model.addAttribute("userList",userPageInfo.getList());
        return "home";
    }
}
