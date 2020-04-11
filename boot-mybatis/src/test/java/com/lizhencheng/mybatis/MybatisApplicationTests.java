package com.lizhencheng.mybatis;

import com.lizhencheng.mybatis.bean.User;
import com.lizhencheng.mybatis.mapper.UserMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by lzc
 * 2020/3/28 22:42
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MybatisApplicationTests {
    @Autowired
    private UserMapper userMapper;

    @Test
    public void test1() throws SQLException {
//        List<User> userList = userMapper.getUserList();
//        System.out.println();
        User user = userMapper.getUserById(1);
        System.out.println(user);
    }
}
