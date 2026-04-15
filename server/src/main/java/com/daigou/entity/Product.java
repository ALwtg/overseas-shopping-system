package com.daigou.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("product")
public class Product {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long categoryId;
    private Long merchantId;
    private String name;
    private String description;
    private BigDecimal originPrice; // 原价(外币)
    private BigDecimal price;       // 售价(RMB)
    private String currency;        // 原币种
    private Integer stock;
    private Integer sales;
    private String coverImage;
    private String images;          // JSON数组
    private String sourceCountry;
    private String brand;
    private String spec;
    private Integer status;         // 0-下架, 1-上架, 2-审核中, 3-拒绝
    private Integer isHot;
    private Integer isRecommend;
    private Integer deleted;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 非字段 - 关联查询使用
    @com.baomidou.mybatisplus.annotation.TableField(exist = false)
    private String categoryName;
    @com.baomidou.mybatisplus.annotation.TableField(exist = false)
    private String merchantName;
}
