package com.joe.dailymate.controller;

import com.joe.dailymate.dto.LoginRequest;
import com.joe.dailymate.dto.RegisterRequest;
import com.joe.dailymate.entity.User;
import com.joe.dailymate.service.UserService;
import com.joe.dailymate.util.JwtUtil;
import com.joe.dailymate.util.PasswordUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    // 注册接口
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody RegisterRequest req) {
        User exist = userService.findByUsername(req.getUsername());
        Map<String, Object> resp = new HashMap<>();
        if(exist != null){
            resp.put("success", false);
            resp.put("msg", "用户名已存在");
            return resp;
        }
        User u = new User();
        u.setUsername(req.getUsername());
        u.setPassword(PasswordUtil.encode(req.getPassword()));
        u.setAvatar(req.getAvatar());
        u.setEmail(req.getEmail());
        User saved = userService.saveUser(u);
        resp.put("success", true);
        resp.put("msg", "注册成功");
        resp.put("user", saved);
        return resp;
    }

    // 登录接口
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginRequest req) {
        User user = userService.findByUsername(req.getUsername());
        Map<String, Object> result = new HashMap<>();
        if (user == null || user.getIsDelete() != null && user.getIsDelete() == 1) {
            result.put("success", false);
            result.put("msg", "用户不存在或已删除");
            return result;
        }
        if (!PasswordUtil.match(req.getPassword(), user.getPassword())) {
            result.put("success", false);
            result.put("msg", "密码错误");
            return result;
        }
        String token = JwtUtil.generateToken(user.getId(), user.getUsername());
        result.put("success", true);
        result.put("token", token);
        result.put("user", user);
        return result;
    }
}