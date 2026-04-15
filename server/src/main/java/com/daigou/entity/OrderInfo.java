package com.daigou.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("order_info")
public class OrderInfo {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderNo;
    private Long userId;
    private Long merchantId;
    private BigDecimal totalAmount;
    private BigDecimal payAmount;
    private BigDecimal freight; // 运费
    // 0-待付款, 1-待采购, 2-运输中, 3-清关中, 4-派送中, 5-已完成, 6-已取消, 7-退款中
    private Integer status;
    private Integer payType;   // 0-微信, 1-支付宝, 2-银行卡
    private LocalDateTime payTime;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private String remark;
    private String cancelReason;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 非字段
    @com.baomidou.mybatisplus.annotation.TableField(exist = false)
    private String statusText;
}
