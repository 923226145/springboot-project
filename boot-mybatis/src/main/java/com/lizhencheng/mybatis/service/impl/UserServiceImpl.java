package com.lizhencheng.mybatis.service.impl;


import com.lizhencheng.mybatis.bean.User;
import com.lizhencheng.mybatis.mapper.UserMapper;
import com.lizhencheng.mybatis.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by lzc
 * 2020/3/14 17:54
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<User> getUserList() {
        List<User> users = userMapper.getUserList();
        return users;
    }
    @Override
    public User getUserById(Integer id) {
        User user = userMapper.getUserById(id);
        return user;
    }
    @Transactional
    @Override
    public void addUser(User user) {
        if (user.getId() != null && !user.getId().equals("")) {
            userMapper.updateUser(user);
        } else {
            userMapper.addUser(user);
        }
    }
    @Override
    public int deleteUserById(Integer id) {
        return userMapper.deleteUserById(id);
    }
}
