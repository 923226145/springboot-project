package com.lizhencheng.mybatis.service;


import com.lizhencheng.mybatis.bean.User;

import java.util.List;

/**
 * Created by lzc
 * 2020/3/14 17:52
 */
public interface UserService {
    public List<User> getUserList();
    public User getUserById(Integer id);
    public void addUser(User user);
    public int deleteUserById(Integer id);
}
