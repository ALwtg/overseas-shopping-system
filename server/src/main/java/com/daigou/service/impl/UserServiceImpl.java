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
        boolean passwordMatch = password.equals(user.getPassword());
        if (!passwordMatch) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            passwordMatch = encoder.matches(password, user.getPassword());
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
}
