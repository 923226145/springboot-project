package com.lizhencheng.jdbc;

import com.lizhencheng.jdbc.bean.User;
import com.lizhencheng.jdbc.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import java.sql.SQLException;

/**
 * Created by lzc
 * 2020/3/28 22:42
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class JdbcApplicationTests {

    @Autowired
    private UserService userService;

    @Test
    public void test1() throws SQLException {
        User user = new User();
        user.setUsername("demo");
        user.setAge(1);
        userService.addUser(user);
    }
}
