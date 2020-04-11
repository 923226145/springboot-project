package com.lizhencheng.mybatis.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lizhencheng.mybatis.bean.User;
import com.lizhencheng.mybatis.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by lzc
 * 2020/3/14 15:42
 */
@RestController
@RequestMapping("/api/UserController")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/user")
    public List<User> users(
            @RequestParam(name = "page", defaultValue = "1")Integer page,
            @RequestParam(name = "size", defaultValue = "5")Integer size) {
        PageHelper.startPage(page, size);
        PageInfo<User> userPageInfo = new PageInfo<>(userService.getUserList());
        return userPageInfo.getList();
    }

    @GetMapping("/user/{id}")
    public User getUserById(@PathVariable Integer id) {
        return userService.getUserById(id);
    }

    @PostMapping("/user")
    public User addUser(@RequestBody User user) {
        userService.addUser(user);
        return user;
    }

    @DeleteMapping("/user/{id}")
    public boolean deleteUserById(@PathVariable Integer id) {
        return userService.deleteUserById(id) > 0;
    }
}
