package com.ground.christmas.rebate.controller;

import com.ground.christmas.rebate.entity.RebateInfo;
import com.ground.christmas.rebate.service.ChristmasRebateService;
import com.ground.christmas.rebate.service.DingTalkRobotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RestController
public class HomeController {

    @Resource
    ChristmasRebateService service;
    @Resource
    DingTalkRobotService dingTalkRobotService;

    @GetMapping("")
    public String index() {
        return "圣诞返奖服务";
    }

    @GetMapping("rebate")
    public List<Long> rebate() {

        log.info("圣诞活动发奖手动发奖开始");
        // 发奖
        List<Long> uids = service.rebate();
        // 通知发短信
        dingTalkRobotService.sendText("圣诞活动短信用户ID：" + uids.toString(), true);
        log.info("圣诞活动发奖手动发奖结束");

        return uids;
    }

    @GetMapping("find")
    public List<RebateInfo> find() {
        return service.findUnrebated();
    }

}
