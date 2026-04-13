package com.daigou.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("announcement")
public class Announcement {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String content;
    private String publisher; // 发布人
    private Integer status;   // 0-草稿, 1-已发布, 2-已撤回
    private Integer sortOrder;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
