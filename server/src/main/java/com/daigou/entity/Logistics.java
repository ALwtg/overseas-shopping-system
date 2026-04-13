package com.daigou.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("logistics")
public class Logistics {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderNo;
    // purchased/shipped/customs/clearance/delivering/delivered
    private String node;
    private String nodeName; // 节点中文名
    private String location;
    private String description;
    private String operator;
    private String logisticsNo;  // 物流单号
    private LocalDateTime time;
}
