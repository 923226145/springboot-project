package com.lizhencheng.jdbc.controller;

import com.lizhencheng.jdbc.bean.User;
import com.lizhencheng.jdbc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
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
    public String home(Model model) {
        List<User> userList = userService.getUserList();
        model.addAttribute("userList",userList);
        return "home";
    }
}
