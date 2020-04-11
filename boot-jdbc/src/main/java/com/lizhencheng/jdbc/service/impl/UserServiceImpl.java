package com.lizhencheng.jdbc.service.impl;


import com.lizhencheng.jdbc.bean.User;
import com.lizhencheng.jdbc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
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
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<User> getUserList() {
        String sql = "select id,username,age from user";
        BeanPropertyRowMapper<User> rowMapper = new BeanPropertyRowMapper<User>(User.class);
        List<User> users = jdbcTemplate.query(sql, rowMapper);
        return users;
    }
    @Override
    public User getUserById(Integer id) {
        String sql = "select id,username,age from user where id = ?";
        BeanPropertyRowMapper<User> rowMapper = new BeanPropertyRowMapper<User>(User.class);
        User user = jdbcTemplate.queryForObject(sql, rowMapper,id);
        return user;
    }
    @Transactional
    @Override
    public void addUser(User user) {
        String insertSql = "insert into user (username,age) values (?,?)";
        String updateSql = "update user set username = ?,age = ? where id = ?";
        if (user.getId() != null && !user.getId().equals("")) {
            jdbcTemplate.update(updateSql, new Object[]{user.getUsername(), user.getAge(), user.getId()});
        } else {
            jdbcTemplate.update(insertSql, new Object[]{user.getUsername(), user.getAge()});
        }
    }
    @Override
    public int deleteUserById(Integer id) {
        String sql = "delete from user where id=?";
        int delete = jdbcTemplate.update(sql, new Object[]{id});
        return delete;
    }
}
