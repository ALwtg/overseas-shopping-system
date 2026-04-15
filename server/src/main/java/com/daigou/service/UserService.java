package com.daigou.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.daigou.entity.User;

public interface UserService extends IService<User> {
    String login(String username, String password);
    void register(User user);
    User getUserInfo(Long id);
    void updateUserInfo(User user);
    Long getUserIdFromToken(String token);
    void changePassword(Long userId, String oldPassword, String newPassword);
}
