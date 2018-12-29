package com.ground.christmas.rebate.service;

import com.ground.christmas.rebate.entity.RebateInfo;

import java.util.List;

public interface ChristmasRebateService {

    /**
     * 返奖
     */
    List<Long> rebate();

    /**
     * 收集返奖信息到表christmas_return_record
     */
    void collectRebateInfo();

    /**
     * 查询未返奖用户信息
     *
     * @return 返奖信息集合
     */
    List<RebateInfo> findUnrebated();

    void toRebated(long orderId, String phoneNum);

}
