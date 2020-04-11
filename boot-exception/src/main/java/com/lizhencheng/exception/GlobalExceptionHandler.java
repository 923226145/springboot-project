package com.lizhencheng.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lzc
 * 2020/4/11 22:52
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 捕获HtmlException，统一返回错误的HTML页面
     */
    @ExceptionHandler(value = HtmlException.class)
    public ModelAndView htmlErrorHandler(HttpServletRequest req, Exception e) throws Exception {
        ModelAndView mav = new ModelAndView();
        mav.addObject("message", e.getMessage());
        mav.addObject("url", req.getRequestURL());
        mav.setViewName("error");
        return mav;
    }

    /**
     * 捕获JsonException，统一返回JSON数据
     */
    @ExceptionHandler(value = JsonException.class)
    @ResponseBody
    public Object jsonErrorHandler(HttpServletRequest req, Exception e) throws Exception {
        Map<String, Object> result = new HashMap<>();
        result.put("code","error");
        result.put("message",e.getMessage());
        return result;
    }
}
