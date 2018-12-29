package com.ground.christmas.rebate.service;

import com.ground.christmas.rebate.entity.UserInfo;

/**
 * 用户基础信息服务
 */
public interface UserBasicService {

    UserInfo findByUserId(long userId);

}
