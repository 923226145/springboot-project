package com.lizhencheng.mp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by lzc
 * 2020/3/28 16:56
 */
@SpringBootApplication
@MapperScan(value = {"com.lizhencheng.mp.mapper"})
public class MybatisPlusApplication {
    public static void main(String[] args) {
        SpringApplication.run(MybatisPlusApplication.class, args);
    }
}
