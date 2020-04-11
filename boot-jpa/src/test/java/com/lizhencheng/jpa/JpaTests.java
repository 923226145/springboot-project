package com.lizhencheng.jpa;

import com.lizhencheng.jpa.bean.Student;
import com.lizhencheng.jpa.repository.StudentRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.startsWith;


/**
 * Created by lzc
 * 2020/3/28 22:42
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class JpaTests {

    @Autowired
    private StudentRepository studentRepository;

    // 新增
    @Test
    public void save() {
        Student student = new Student();
        student.setCityName("深圳");
        student.setStudentName("小李");
        studentRepository.save(student);
        System.out.println(student);
    }

    // 删除
    @Test
    public void deleteById() {
        // 通过id删除，如果id不存在，将会抛异常
        // 删除之前可以通过studentRepository.existsById(10L)来判断id是否存在，如果存在，则删除
        studentRepository.deleteById(10L);
    }

    // 修改
    @Test
    public void update() {
        // 通过id查询
        Student result = studentRepository.findById(13L).orElse(null); // 当查询结果不存在时则返回null
        result.setAge(9);
        studentRepository.save(result);
    }

    // 简单查询：查询所有记录
    @Test
    public void findAll() {
        List<Student> studentList = studentRepository.findAll();
        studentList.stream().forEach(s -> System.out.println(s.toString()));
    }

    // 简单查询：分页查询
    @Test
    public void findPage1() {
        Page<Student> studentPage = studentRepository.findAll(PageRequest.of(0,2));

        List<Student> studentList = studentPage.getContent();
        studentList.stream().forEach(s -> System.out.println(s.toString()));

        System.out.println("【TotalPages】"  + studentPage.getTotalPages());
        System.out.println("【totalElements】"  + studentPage.getTotalElements());
        System.out.println("【Number】"  + studentPage.getNumber());
        System.out.println("【Size】"  + studentPage.getSize());
        System.out.println("【NumberOfElements】"  + studentPage.getNumberOfElements());
    }

    // 简单查询：分页查询+排序（要么升序，要么降序）
    @Test
    public void findPage2() {

        Page<Student> studentPage = studentRepository.findAll(PageRequest.of(0,2,Sort.Direction.ASC,"age"));

        List<Student> studentList = studentPage.getContent();
        studentList.stream().forEach(s -> System.out.println(s.toString()));

        System.out.println("【TotalPages】"  + studentPage.getTotalPages());
        System.out.println("【totalElements】"  + studentPage.getTotalElements());
        System.out.println("【Number】"  + studentPage.getNumber());
        System.out.println("【Size】"  + studentPage.getSize());
        System.out.println("【NumberOfElements】"  + studentPage.getNumberOfElements());
    }

    // 简单查询：分页查询+排序（既有升序，又有降序）
    @Test
    public void findPage3() {

        Sort sort = Sort.by(Sort.Direction.DESC,"age"); // 年龄降序
        sort = sort.and(Sort.by(Sort.Direction.ASC,"className")); // 班级升序

        Page<Student> studentPage = studentRepository.findAll(PageRequest.of(0,2,sort));

        List<Student> studentList = studentPage.getContent();
        studentList.stream().forEach(s -> System.out.println(s.toString()));

        System.out.println("【TotalPages】"  + studentPage.getTotalPages());
        System.out.println("【totalElements】"  + studentPage.getTotalElements());
        System.out.println("【Number】"  + studentPage.getNumber());
        System.out.println("【Size】"  + studentPage.getSize());
        System.out.println("【NumberOfElements】"  + studentPage.getNumberOfElements());
    }

    @Test
    public void findByStudentName() {
        Student student = studentRepository.findByStudentName("李振成");
        System.out.println(student.toString());
    }

    // JPA动态查询
    @Test
    public void test1() {
        Student student = new Student();
        student.setCityName("南宁");
        student.setClassName("计科152");
        Example<Student> example = Example.of(student);
        // SQL语句 select * from student where city_name = '南宁' and class_name = '计科152'
        List<Student> studentList = studentRepository.findAll(example);
        studentList.forEach(s -> System.out.println(s.toString()));
    }
    @Test
    public void test2() {
        Student student = new Student();
        student.setCityName("南宁");
        student.setClassName("计科152");

        Example<Student> example = Example.of(student);
        // SQL语句 select * from student where city_name = '南宁' and class_name = '计科152' order by age desc limit 0,2
        Page<Student> studentPage = studentRepository.findAll(example, PageRequest.of(0,2, Sort.Direction.DESC,"age"));

        List<Student> studentList = studentPage.getContent();
        studentList.stream().forEach(s -> System.out.println(s.toString()));

        System.out.println("【TotalPages】"  + studentPage.getTotalPages());
        System.out.println("【totalElements】"  + studentPage.getTotalElements());
        System.out.println("【Number】"  + studentPage.getNumber());
        System.out.println("【Size】"  + studentPage.getSize());
        System.out.println("【NumberOfElements】"  + studentPage.getNumberOfElements());
    }
    @Test
    public void test3() {
        Student student = new Student();
        student.setCityName("南宁");
        student.setClassName("计科");

        // 设置属性的查询规则，
        // 有ignoreCase()，caseSensitive()，contains()，endsWith()，startsWith()，exact()，storeDefaultMatching()，regex()
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("className",startsWith());

        Example<Student> example = Example.of(student, matcher);

        List<Student> studentList = studentRepository.findAll(example);
        studentList.forEach(s -> System.out.println(s.toString()));
    }
}

