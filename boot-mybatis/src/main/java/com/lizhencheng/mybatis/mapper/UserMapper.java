package com.lizhencheng.mybatis.mapper;

import com.lizhencheng.mybatis.bean.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by lzc
 * 2020/3/29 15:26
 */
@Mapper
public interface UserMapper {
//    @Select("select * from user")
    public List<User> getUserList();
//    @Select("select id,username,age from user where id = #{id}")
    public User getUserById(Integer id);
//    @Insert("insert into user (username,age) values (#{username},#{age})")
    public void addUser(User user);
//    @Update("update user set username = #{username},age = #{age} where id = #{id}")
    public void updateUser(User user);
//    @Delete("delete from user where id=#{id}")
    public int deleteUserById(Integer id);
}
