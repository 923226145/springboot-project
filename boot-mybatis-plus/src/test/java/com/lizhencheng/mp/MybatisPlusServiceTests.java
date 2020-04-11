package com.lizhencheng.mp;
import com.lizhencheng.mp.bean.Student;
import com.lizhencheng.mp.mapper.StudentMapper;
import com.lizhencheng.mp.service.StudentService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * Created by lzc
 * 2020/3/28 22:42
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MybatisPlusServiceTests {

    @Autowired
    private StudentService studentService;

    /* ################################# 新增 ################################# */
    // 插入一条记录
    @Test
    public void insert() {
        Student student = new Student();
        student.setCityName("南宁");
        student.setStudentName("王麻子");
        //SQL语句：INSERT INTO student ( student_name, city_name ) VALUES ( ?, ? )
        studentService.save(student);
    }
}

