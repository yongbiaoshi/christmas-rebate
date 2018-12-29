package com.ground.christmas.rebate.service.impl;

import com.ground.christmas.rebate.config.RobotProperties;
import com.ground.christmas.rebate.service.DingTalkRobotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class DingTalkRobotServiceImpl implements DingTalkRobotService {

    private static final String MSG_TO_SEND = "{\"msgtype\":\"text\",\"text\":{\"content\":\"%s\"},\"at\":{\"isAtAll\":%s}}";

    private RobotProperties properties;
    private RestTemplate restTemplate;


    public DingTalkRobotServiceImpl(RobotProperties properties, RestTemplateBuilder builder) {
        this.properties = properties;
        this.restTemplate = builder.build();
    }

    @Override
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000L))
    public void sendText(String content, boolean isAtAll) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Content-Type", "application/json");
        HttpEntity<String> entity = new HttpEntity<>(String.format(MSG_TO_SEND, content, isAtAll), requestHeaders);
        ResponseEntity<String> response = restTemplate.postForEntity(properties.getChristmasUrl(), entity, String.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("钉钉机器人信息发送成功，内容：{}，isAtAll：{}，返回结果：{}", content.replace("\n", "\\n"), isAtAll, response.getBody());
        } else {
            log.error("钉钉机器人信息发送失败，返回结果：{}", response.toString());
        }
    }
}

