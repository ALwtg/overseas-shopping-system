package com.daigou.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.daigou.common.BusinessException;
import com.daigou.config.JwtUtil;
import com.daigou.entity.User;
import com.daigou.mapper.UserMapper;
import com.daigou.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public String login(String username, String password) {
        User user = getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user == null) throw new BusinessException("用户不存在");
        if (user.getStatus() == 0) throw new BusinessException("账号已被禁用");
        // 开发环境：支持明文密码和BCrypt密码
        System.out.println("[调试] 输入密码: " + password);
        System.out.println("[调试] 数据库密码: " + user.getPassword());
        boolean passwordMatch = password.equals(user.getPassword());
        System.out.println("[调试] 明文匹配结果: " + passwordMatch);
        if (!passwordMatch) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            passwordMatch = encoder.matches(password, user.getPassword());
            System.out.println("[调试] BCrypt匹配结果: " + passwordMatch);
        }
        if (!passwordMatch) throw new BusinessException("密码错误");
        return jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
    }

    @Override
    public void register(User user) {
        long count = baseMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getUsername, user.getUsername()));
        if (count > 0) throw new BusinessException("用户名已存在");
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode(user.getPassword()));
        user.setRole(0);
        user.setStatus(1);
        user.setDeleted(0);
        save(user);
    }

    @Override
    public User getUserInfo(Long id) {
        User user = getById(id);
        if (user != null) user.setPassword(null);
        return user;
    }

    @Override
    public void updateUserInfo(User user) {
        updateById(user);
    }

    @Override
    public Long getUserIdFromToken(String token) {
        return jwtUtil.getUserId(token);
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = getById(userId);
        if (user == null) throw new BusinessException("用户不存在");
        // 验证旧密码
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        boolean passwordMatch = oldPassword.equals(user.getPassword());
        if (!passwordMatch) {
            passwordMatch = encoder.matches(oldPassword, user.getPassword());
        }
        if (!passwordMatch) throw new BusinessException("原密码错误");
        // 更新密码
        user.setPassword(encoder.encode(newPassword));
        updateById(user);
    }
}
