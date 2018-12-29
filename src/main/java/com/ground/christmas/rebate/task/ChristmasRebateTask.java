package com.ground.christmas.rebate.task;

import com.ground.christmas.rebate.service.ChristmasRebateService;
import com.ground.christmas.rebate.service.DingTalkRobotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class ChristmasRebateTask {

    private ChristmasRebateService christmasRebateService;
    private DingTalkRobotService dingTalkRobotService;

    public ChristmasRebateTask(ChristmasRebateService christmasRebateService, DingTalkRobotService dingTalkRobotService) {
        this.christmasRebateService = christmasRebateService;
        this.dingTalkRobotService = dingTalkRobotService;
    }

    @Scheduled(cron = "${rebate.cron}")
    public void rebate() {
        log.info("圣诞活动发奖定时任务开始");
        // 发奖
        List<Long> uids = christmasRebateService.rebate();
        // 通知发短信
        dingTalkRobotService.sendText("圣诞活动短信用户ID：" + uids.toString(), true);
        log.info("圣诞活动发奖定时任务结束");
    }

}
