package com.lizhencheng.mp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lizhencheng.mp.bean.Student;
import com.lizhencheng.mp.mapper.StudentMapper;
import com.lizhencheng.mp.service.StudentService;
import org.springframework.stereotype.Service;

/**
 * Created by lzc
 * 2020/3/29 21:09
 */
@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {

}
