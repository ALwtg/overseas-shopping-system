package com.daigou.controller;

import com.daigou.common.Result;
import com.daigou.entity.User;
import com.daigou.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired private UserService userService;

    @PostMapping("/login")
    public Result<?> login(@RequestBody User user) {
        String token = userService.login(user.getUsername(), user.getPassword());
        User info = userService.getUserInfo(userService.getUserIdFromToken(token));
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", info);
        return Result.ok("登录成功", data);
    }

    @PostMapping("/register")
    public Result<?> register(@RequestBody User user) {
        userService.register(user);
        return Result.ok("注册成功");
    }
}
