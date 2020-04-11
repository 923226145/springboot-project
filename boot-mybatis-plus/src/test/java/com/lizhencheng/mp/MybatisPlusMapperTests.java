package com.lizhencheng.mp;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lizhencheng.mp.bean.Student;
import com.lizhencheng.mp.mapper.StudentMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lzc
 * 2020/3/28 22:42
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MybatisPlusMapperTests {

    @Autowired
    private StudentMapper studentMapper;

    /* ################################# 新增 ################################# */
    // 插入一条记录
    @Test
    public void insert() {
        Student student = new Student();
        student.setCityName("南宁");
        student.setStudentName("王麻子");
        //SQL语句：INSERT INTO student ( student_name, city_name ) VALUES ( ?, ? )
        studentMapper.insert(student);
    }
    /* ################################# 删除 ################################# */
    // 根据 ID 删除
    @Test
    public void deleteById() {
        // SQL语句：DELETE FROM student WHERE id = ?
        studentMapper.deleteById(2L);
    }

    // 根据 columnMap 条件，删除记录
    @Test
    public void deleteByMap() {
        Map<String, Object> columnMap = new HashMap<>();
        columnMap.put("city_name", "桂林");
        columnMap.put("class_name", "计网153");
        // SQL语句：DELETE FROM student WHERE city_name = ? AND class_name = ?
        studentMapper.deleteByMap(columnMap);
    }

    // 根据 entity 条件，删除记录
    @Test
    public void delete() {
        Student student = new Student();
        student.setCityName("南宁");
        student.setStudentName("zhangsan");
        // SQL语句：DELETE FROM student WHERE student_name=? AND city_name=?
        studentMapper.delete(new QueryWrapper<>(student));
        // 通过SQL语句可以发现，条件语句只包含了实体类不为空的属性
    }

    // 删除（根据ID 批量删除）
    @Test
    public void deleteBatchIds() {
        List<Long> ids = new ArrayList<>();
        ids.add(4L);
        ids.add(5L);
        // SQL语句：DELETE FROM student WHERE id IN ( ? , ? )
        studentMapper.deleteBatchIds(ids);
    }
    /* ################################# 修改 ################################# */
    // 根据 ID 修改
    @Test
    public void updateById() {
        Student student = new Student();
        student.setId(1L);
        student.setCityName("深圳");
        student.setAge(16);
        // SQL语句：UPDATE student SET city_name=?, age=? WHERE id=?
        studentMapper.updateById(student);
    }

    // 根据 whereEntity 条件，更新记录
    @Test
    public void update() {
        Student entity = new Student(); // 修改内容
        entity.setAge(20);
        Student whereEntity = new Student(); // 修改条件
        whereEntity.setCityName("桂林");
        whereEntity.setClassName("计科152");
        // SQL语句：UPDATE student SET age=? WHERE class_name=? AND city_name=?
        studentMapper.update(entity, new UpdateWrapper<>(whereEntity));
    }
    /* ################################# 查询 ################################# */
    // 根据 ID 查询
    @Test
    public void selectById() {
        // SQL语句：SELECT id,student_name,gender,class_name,age,city_name,create_time,update_time FROM student WHERE id=?
        Student student = studentMapper.selectById(1L);
        System.out.println(student.toString());
    }

    // 查询（根据ID 批量查询）
    @Test
    public void selectBatchIds() {
        // idList 主键ID列表(不能为 null 以及 empty)
        List<Long> idList = new ArrayList<>();
        idList.add(1L);
        idList.add(2L);
        // SQL语句：SELECT id,student_name,gender,class_name,age,city_name,create_time,update_time FROM student WHERE id IN ( ? , ? )
        List<Student> studentList = studentMapper.selectBatchIds(idList);
        studentList.forEach(System.out::println);
    }

    // 查询（根据 columnMap 条件）
    @Test
    public void selectByMap() {
        Map<String, Object> columnMap = new HashMap<>();
        columnMap.put("city_name", "桂林");
        columnMap.put("class_name", "计科152");
        // SQL语句：SELECT id,student_name,gender,class_name,age,city_name,create_time,update_time FROM student WHERE city_name = ? AND class_name = ?
        List<Student> studentList = studentMapper.selectByMap(columnMap);
        studentList.forEach(System.out::println);
    }

    // 根据 Wrapper 条件，查询总记录数
    @Test
    public void selectCount() {
        Student student = new Student(); // 查询条件实体类
        student.setCityName("桂林");
        // SQL语句：SELECT COUNT( 1 ) FROM student WHERE city_name=?
        Integer count = studentMapper.selectCount(new QueryWrapper<>(student));
        System.out.println(count);
    }

    // 根据 entity 条件，查询全部记录
    @Test
    public void selectList() {
        Student entity = new Student(); // 查询条件实体类
        entity.setCityName("桂林");
        entity.setClassName("计科152");
        // SQL语句：SELECT id,student_name,gender,class_name,age,city_name,create_time,update_time FROM student WHERE class_name=? AND city_name=?
        List<Student> studentList = studentMapper.selectList(new QueryWrapper<>(entity));
        studentList.forEach(System.out::println);
    }

    // 根据 entity 条件，查询全部记录并分页（使用分页功能一定要设置PaginationInterceptor插件）
    @Test
    public void selectPage() {
        Student entity = new Student(); // 查询条件实体类
        entity.setCityName("桂林");
        entity.setClassName("计科152");
        // SQL语句：
        // SELECT id,student_name,gender,class_name,age,city_name,create_time,update_time
        // FROM student WHERE class_name=? AND city_name=? LIMIT ?,?
        Page<Student> studentPage = new Page<>(1,1);
        studentMapper.selectPage(studentPage,new QueryWrapper<>(entity));
        List<Student> records = studentPage.getRecords();// 分页对象记录列表
        System.out.println("总数：" + studentPage.getTotal());
        System.out.println("当前页：" + studentPage.getCurrent());
        System.out.println("当前分页总页数：" + studentPage.getPages());
        System.out.println("每页显示条数，默认 10：" + studentPage.getSize());
    }
}

