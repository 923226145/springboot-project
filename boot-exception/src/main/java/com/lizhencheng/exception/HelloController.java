package com.lizhencheng.exception;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by lzc
 * 2020/4/11 22:48
 */
@Controller
public class HelloController {
    @GetMapping("/hello/{type}")
    public String hello(@PathVariable String type) throws Exception {
        if ("1".equals(type)) {
            throw new HtmlException("抛出异常，返回HTML页面");
        } else if ("2".equals(type)) {
            throw new JsonException("抛出异常，返回JSON页面");
        }
        return "hello";
    }
}
