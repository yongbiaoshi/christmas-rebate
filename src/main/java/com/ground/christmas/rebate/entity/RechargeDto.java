package com.ground.christmas.rebate.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RechargeDto {
    /**
     * phoneNum : 13848131779
     * money : 500
     * remark : 圣诞节活动返现
     * rechargeReasonId : 6
     * rechargeTypeId : 2
     * sysUserId : 1
     * orderId :
     * income : 1
     */

    private String phoneNum;
    private int money;
    private String remark;
    private int rechargeReasonId;
    private int rechargeTypeId;
    private String sysUserId;
    private String orderId;
    private int income;
}
