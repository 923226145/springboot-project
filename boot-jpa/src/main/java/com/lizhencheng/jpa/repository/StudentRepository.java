package com.lizhencheng.jpa.repository;

import com.lizhencheng.jpa.bean.Student;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by lzc
 * 2020/3/29 22:13
 */
public interface StudentRepository extends JpaRepository<Student, Long> {
    Student findByStudentName(String studentName);
}
