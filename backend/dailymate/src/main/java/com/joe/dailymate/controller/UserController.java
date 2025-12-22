package com.joe.dailymate.controller;

import com.joe.dailymate.dto.ChangePasswordRequest;
import com.joe.dailymate.dto.UpdateUserProfileRequest;
import com.joe.dailymate.entity.User;
import com.joe.dailymate.service.UserService;
import com.joe.dailymate.util.JwtUtil;
import com.joe.dailymate.util.PasswordUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public List<User> getUserList() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public User findById(@PathVariable Long id) {
        return userService.findById(id);
    }

    @GetMapping("/find")
    public User findByUsername(@RequestParam String username) {
        return userService.findByUsername(username);
    }

    @PostMapping("/add")
    public User addUser(@RequestBody User user) {
        return userService.saveUser(user);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
    }

    // ========================== 新增：获取当前登录用户信息  ==========================

    /**
     * 获取当前登录用户信息（需带token）
     */
    @GetMapping("/me")
    public User getMyInfo(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        return userService.findById(userId);
    }

    // ========================== 新增：更新当前用户基本信息 ==========================
    /**
     * 更新当前登录用户资料（如头像、邮箱、昵称等）
     * 允许字段部分更新
     */
    @PutMapping("/profile")
    public Map<String, Object> updateProfile(
            HttpServletRequest request,
            @RequestBody UpdateUserProfileRequest req
    ) {
        Long userId = getUserIdFromRequest(request);
        User user = userService.findById(userId);
        if (user == null) return Map.of("success", false, "msg", "用户不存在");
        if (req.getEmail() != null) user.setEmail(req.getEmail());
        if (req.getAvatar() != null) user.setAvatar(req.getAvatar());
        if (req.getUsername() != null) user.setUsername(req.getUsername());
        userService.saveUser(user);
        return Map.of("success", true, "msg", "信息修改成功", "user", user);
    }

    // ========================== 新增：修改密码接口 ==========================
    /**
     * 修改当前登录账户密码
     * @param request 包含token请求
     * @param req oldPassword/newPassword参数
     */
    @PutMapping("/password")
    public Map<String, Object> updatePassword(
            HttpServletRequest request,
            @RequestBody ChangePasswordRequest req
    ) {
        Long userId = getUserIdFromRequest(request);
        User user = userService.findById(userId);
        if (user == null) return Map.of("success", false, "msg", "用户不存在");
        if (!PasswordUtil.match(req.getOldPassword(), user.getPassword())) {
            return Map.of("success", false, "msg", "原密码错误");
        }
        user.setPassword(PasswordUtil.encode(req.getNewPassword()));
        userService.saveUser(user);
        return Map.of("success", true, "msg", "密码修改成功");
    }

    // ========================== 新增：注销账号（软删除）接口 ==========================
    /**
     * 注销用户（软删除当前账户）
     * @param request 带token
     */
    @DeleteMapping("/me")
    public Map<String, Object> deleteMyAccount(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        userService.deleteById(userId);
        return Map.of("success", true, "msg", "账号注销成功");
    }

    // ========================== 辅助方法：从token获取userid ==========================
    /**
     * 从请求头的token获取当用户的用户ID
     */
    private Long getUserIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) throw new RuntimeException("未登录");
        Claims claims = JwtUtil.parseToken(token.substring(7));
        return Long.valueOf(claims.getSubject());
    }
}