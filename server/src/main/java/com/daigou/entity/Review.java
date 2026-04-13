package com.daigou.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("review")
public class Review {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orderId;
    private Long orderItemId;
    private Long userId;
    private Long merchantId;
    private Long productId;
    private Integer rating; // 1-5
    private String content;
    private String images;  // JSON数组
    private String reply;   // 商家回复
    private LocalDateTime replyTime;
    private Integer status; // 0-隐藏, 1-显示
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
