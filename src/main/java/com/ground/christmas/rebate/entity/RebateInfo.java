package com.ground.christmas.rebate.entity;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class RebateInfo {

    private long orderId;
    private String orderNum;
    private long userId;
    private String phoneNum;
    private int realPrice;
    private int orderStatus;
    private int returnAmount;
    private Date startBillingTime;
    private short rentType;
    private boolean isReturned;
    private Date returnTime;
    private Date lastUpdateTime;
    private Date createTime;

}
