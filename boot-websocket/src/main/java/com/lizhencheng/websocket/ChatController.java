package com.lizhencheng.websocket;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lzc
 * 2020/4/11 16:22
 */
@Controller
public class ChatController {
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    @PostMapping("/doLogin")
    @ResponseBody
    public Object doLogin(HttpServletRequest request) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        Map<String, Object> result = new HashMap<>();
        if ("123456".equals(password)) {
            result.put("code","000000");
            result.put("msg","登录成功");
            request.getSession(true).setAttribute("username",username);
        } else {
            result.put("code","000001");
            result.put("msg","账号或密码错误");
        }
        return result;
    }
    @GetMapping("/chat")
    public String chat(HttpServletRequest request, Model model) {
        String username = (String) request.getSession(true).getAttribute("username");
        if (username == null) {
            return "login";
        }
        model.addAttribute("username", username);
        return "chat";
    }
}
