package com.ground.christmas.rebate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ChristmasRebateApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChristmasRebateApplication.class, args);
    }

}

