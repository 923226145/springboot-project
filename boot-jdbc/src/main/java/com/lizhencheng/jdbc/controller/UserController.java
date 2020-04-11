package com.lizhencheng.jdbc.controller;

import com.lizhencheng.jdbc.bean.User;
import com.lizhencheng.jdbc.service.UserService;
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
    public List<User> users() {
        return userService.getUserList();
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
