package com.aikc.controller;

import com.aikc.common.Result;
import com.aikc.dto.LoginRequest;
import com.aikc.dto.LoginResponse;
import com.aikc.dto.RegisterRequest;
import com.aikc.entity.User;
import com.aikc.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return Result.success(response);
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return Result.success("注册成功", null);
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    public Result<User> getCurrentUser(@AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
        User user = authService.getCurrentUser(userDetails.getUsername());
        // 清除密码字段
        user.setPassword(null);
        return Result.success(user);
    }

    /**
     * 退出登录 (前端清除 token 即可)
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.success("退出成功", null);
    }
}
