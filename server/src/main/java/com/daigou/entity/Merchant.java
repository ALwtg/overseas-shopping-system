package com.daigou.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("merchant")
public class Merchant {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String storeName;
    private String licenseNo;
    private String contactPhone;
    private String storeDesc;
    private String logo;
    private String location;
    private Integer status; // 0-待审核, 1-已通过, 2-已拒绝, 3-已封禁
    private BigDecimal rating;
    private Integer totalSales;
    private BigDecimal balance;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
