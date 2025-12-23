package com.aikc.service;

import com.aikc.dto.LoginRequest;
import com.aikc.dto.LoginResponse;
import com.aikc.dto.RegisterRequest;
import com.aikc.entity.User;
import com.aikc.mapper.UserMapper;
import com.aikc.security.JwtUtil;
import com.aikc.security.SecurityUserDetails;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 认证服务
 */
@Service
public class AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * 用户登录
     */
    public LoginResponse login(LoginRequest request) {
        // 使用 Spring Security 进行认证
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        // 获取用户信息
        SecurityUserDetails userDetails = (SecurityUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        // 生成 JWT Token
        String token = jwtUtil.generateToken(user.getUsername());

        return LoginResponse.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .role(user.getRole())
                .build();
    }

    /**
     * 用户注册
     */
    public void register(RegisterRequest request) {
        // 检查用户名是否已存在
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getUsername, request.getUsername())
        );

        if (count > 0) {
            throw new RuntimeException("用户名已存在");
        }

        // 创建新用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setEmail(request.getEmail());
        user.setRole("STUDENT");
        user.setStatus(1);

        userMapper.insert(user);
    }

    /**
     * 获取当前用户信息
     */
    public User getCurrentUser(String username) {
        return userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, username)
        );
    }
}
