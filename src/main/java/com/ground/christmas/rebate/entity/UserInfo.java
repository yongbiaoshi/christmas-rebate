package com.ground.christmas.rebate.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserInfo {

    private long id;
    private String name;
    private long picture;
    private int gender;
    private int age;
    private String phoneNum;

}
