package com.daigou.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String password;
    private String nickname;
    private String phone;
    private String avatar;
    private String email;
    private Integer role; // 0-普通用户, 1-代购商家, 2-管理员
    private Integer status; // 0-禁用, 1-正常
    private Integer deleted;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
