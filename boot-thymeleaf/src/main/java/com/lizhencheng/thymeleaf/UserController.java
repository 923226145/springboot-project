package com.lizhencheng.thymeleaf;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lzc
 * 2020/3/28 17:01
 */
@Controller
public class UserController {
    @GetMapping("/user")
    public String user(Model model, HttpServletRequest request) {
        request.setAttribute("requestKey","requestValue");
        request.getSession().setAttribute("sessionKey","sessionValue");
        List<Map<String, Object>> userList = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("id",i);
            map.put("username", "name_" +  i);
            if (i % 2 == 0) {
                map.put("gender", 1);
            } else {
                map.put("gender", 0);
            }
            userList.add(map);
        }
        model.addAttribute("userList",userList);
        model.addAttribute("username","lizhencheng");
        model.addAttribute("date",LocalDateTime.now());
        return "user";
    }
}
