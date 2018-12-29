package com.ground.christmas.rebate.service.impl;

import com.ground.christmas.rebate.entity.RebateInfo;
import com.ground.christmas.rebate.entity.RechargeDto;
import com.ground.christmas.rebate.entity.UserInfo;
import com.ground.christmas.rebate.service.ChristmasRebateService;
import com.ground.christmas.rebate.service.UserBasicService;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ChrismasRebateServiceImpl implements ChristmasRebateService {

    @Resource
    JdbcTemplate incarJdbcTemplate;

    @Resource
    UserBasicService userBasicService;

    @Value("${user.recharge-url}")
    private String rechargeUrl;

    private RestTemplate restTemplate;

    public ChrismasRebateServiceImpl(RestTemplateBuilder builder) {
        this.restTemplate = builder.setConnectTimeout(Duration.ofSeconds(3)).setReadTimeout(Duration.ofSeconds(10)).build();
    }

    @Override
    public List<Long> rebate() {
        collectRebateInfo(); // 收集需要发奖的信息保存到记录表中
        List<RebateInfo> list = findUnrebated();
        list.forEach(info -> {
            UserInfo u = userBasicService.findByUserId(info.getUserId());
            if (u != null) {
                info.setPhoneNum(u.getPhoneNum());
            }
        });

        List<Long> uids = list.stream()
                .map(info -> {
                    RechargeDto dto = RechargeDto.builder()
                            .income(1)
                            .orderId(String.valueOf(info.getOrderId()))
                            .money(info.getReturnAmount())
                            .phoneNum(info.getPhoneNum())
                            //.phoneNum("13848131779") // 测试用
                            .rechargeReasonId(6)
                            .rechargeTypeId(2)
                            .remark("圣诞节活动返现")
                            .sysUserId("1")
                            .build();
                    String r = restTemplate.postForObject(rechargeUrl, dto, String.class);
                    log.info("圣诞活动返现接口，参数 - phoneNum：{}，money：{}，接口返回：{}", dto.getPhoneNum(), dto.getMoney(), r);
                    Integer status = JsonPath.parse(r).read("$.status", Integer.class);
                    log.info("status:{}", status);
                    if (status == 200) {
                        // 发奖成功，更新发奖状态
                        toRebated(info.getOrderId(), info.getPhoneNum());
                        return info.getUserId(); // 返回用户ID，用于发送短信
                    } else {
                        // 发奖失败，记录日志
                        log.error("圣诞活动返现接口调用失败，参数 - 订单号：{}，用户ID：{}，phoneNum：{}，money：{}，接口返回：{}", info.getOrderId(), info.getUserId(), dto.getPhoneNum(), dto.getMoney(), r);
                        return null;
                    }
                })
                .filter(Objects::nonNull).collect(Collectors.toList());


        // List<Long> ids = list.stream().mapToLong(RebateInfo::getUserId).boxed().collect(Collectors.toList());

        log.info("发奖完毕，需要发送短信的用户：{}", uids);
        return uids;
    }

    @Override
    public void collectRebateInfo() {
        String sql = "INSERT INTO christmas_return_record " +
                "(order_id, order_num, user_id, real_price, order_status, return_amount, start_billing_time, rent_type) " +
                "SELECT id, order_num, user_id, real_price, `status`, FLOOR(real_price * 0.1) AS return_amount, start_billing_time, rent_type " +
                "FROM orders o WHERE start_billing_time BETWEEN '2018-12-25 00:00:00' AND '2019-01-01 00:00:00' AND real_price >= 5000 AND rent_type = 0 " +
                "AND NOT EXISTS ( SELECT 1 FROM christmas_return_record crr WHERE o.id = crr.order_id ) ORDER BY id;";
        incarJdbcTemplate.execute(sql);
    }

    @Override
    public List<RebateInfo> findUnrebated() {
        RowMapper<RebateInfo> mapper = (rs, rowNum) -> RebateInfo.builder()
                .orderId(rs.getLong("order_id"))
                .orderNum(rs.getString("order_num"))
                .userId(rs.getLong("user_id"))
                .phoneNum(rs.getString("phone_num"))
                .realPrice(rs.getInt("real_price"))
                .orderStatus(rs.getInt("order_status"))
                .returnAmount(rs.getInt("return_amount"))
                .startBillingTime(rs.getTimestamp("start_billing_time"))
                .rentType(rs.getShort("rent_type"))
                .isReturned(rs.getBoolean("is_returned"))
                .returnTime(rs.getTimestamp("return_time"))
                .lastUpdateTime(rs.getTimestamp("last_update_time"))
                .createTime(rs.getTimestamp("create_time"))
                .build();
        String sql = "SELECT order_id, order_num, user_id, phone_num, real_price, order_status, return_amount, start_billing_time, rent_type, is_returned, return_time, last_update_time, create_time FROM `christmas_return_record` WHERE is_returned = 0;";
        return incarJdbcTemplate.query(sql, mapper);
    }

    @Override
    public void toRebated(long orderId, String phoneNum) {
        int i = incarJdbcTemplate.update("UPDATE christmas_return_record SET is_returned = 1, phone_num = ?, return_time = NOW() WHERE order_id = ?", phoneNum, orderId);
        log.info("返还状态更新，订单号：{}，执行结果：{}", orderId, i);
    }
}
