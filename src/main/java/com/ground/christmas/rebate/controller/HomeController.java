package com.ground.christmas.rebate.controller;

import com.ground.christmas.rebate.entity.RebateInfo;
import com.ground.christmas.rebate.service.ChristmasRebateService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class HomeController {

    @Resource
    ChristmasRebateService service;

    @GetMapping("")
    public String index() {
        return "圣诞返奖服务";
    }

    @GetMapping("rebate")
    public List<Long> rebate() {
        return service.rebate();
    }

    @GetMapping("find")
    public List<RebateInfo> find() {
        return service.findUnrebated();
    }

}
