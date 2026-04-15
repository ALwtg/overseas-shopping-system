package com.daigou.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.daigou.common.Result;
import com.daigou.entity.Address;
import com.daigou.entity.User;
import com.daigou.mapper.AddressMapper;
import com.daigou.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired private UserService userService;
    @Autowired private AddressMapper addressMapper;

    @GetMapping("/info")
    public Result<?> info(@RequestAttribute Long userId) {
        return Result.ok(userService.getUserInfo(userId));
    }

    @PutMapping("/info")
    public Result<?> updateInfo(@RequestAttribute Long userId, @RequestBody User user) {
        user.setId(userId);
        userService.updateUserInfo(user);
        return Result.ok("修改成功");
    }

    @GetMapping("/address")
    public Result<?> addressList(@RequestAttribute Long userId) {
        List<Address> list = addressMapper.selectList(
                new LambdaQueryWrapper<Address>().eq(Address::getUserId, userId).orderByDesc(Address::getIsDefault));
        return Result.ok(list);
    }

    @PostMapping("/address")
    public Result<?> addAddress(@RequestAttribute Long userId, @RequestBody Address addr) {
        if (addr.getIsDefault() == 1) {
            addressMapper.selectList(new LambdaQueryWrapper<Address>().eq(Address::getUserId, userId).eq(Address::getIsDefault, 1))
                    .forEach(a -> { a.setIsDefault(0); addressMapper.updateById(a); });
        }
        addr.setUserId(userId);
        addressMapper.insert(addr);
        return Result.ok(addr);
    }

    @PutMapping("/address/{id}")
    public Result<?> updateAddress(@RequestAttribute Long userId, @PathVariable Long id, @RequestBody Address addr) {
        addr.setId(id);
        addr.setUserId(userId);
        addressMapper.updateById(addr);
        return Result.ok("更新成功");
    }

    @DeleteMapping("/address/{id}")
    public Result<?> deleteAddress(@RequestAttribute Long userId, @PathVariable Long id) {
        long count = addressMapper.selectCount(new LambdaQueryWrapper<Address>().eq(Address::getUserId, userId));
        if (count <= 1) throw new RuntimeException("至少保留一个地址");
        addressMapper.deleteById(id);
        return Result.ok("删除成功");
    }

    @PutMapping("/password")
    public Result<?> changePassword(@RequestAttribute Long userId, @RequestBody Map<String, String> params) {
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        userService.changePassword(userId, oldPassword, newPassword);
        return Result.ok("密码修改成功");
    }
}
