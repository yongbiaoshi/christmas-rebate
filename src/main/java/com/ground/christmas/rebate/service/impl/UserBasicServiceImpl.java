package com.ground.christmas.rebate.service.impl;

import com.ground.christmas.rebate.entity.UserInfo;
import com.ground.christmas.rebate.service.UserBasicService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
@Service
public class UserBasicServiceImpl implements UserBasicService {

    @Resource
    JdbcTemplate userJdbcTemplate;

    @Override
    public UserInfo findByUserId(long userId) {
        RowMapper<UserInfo> mapper = new RowMapper<UserInfo>() {
            @Override
            public UserInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
                return UserInfo.builder()
                        .id(rs.getLong("id"))
                        .name(rs.getString("name"))
                        .phoneNum(rs.getString("phone_num"))
                        .age(rs.getInt("age"))
                        .gender(rs.getInt("gender"))
                        .build();
            }
        };
        UserInfo user = null;
        try {
            user = userJdbcTemplate.queryForObject("SELECT id, `name`, picture, gender, age, phone_num FROM user_basic WHERE id = ?;", new Object[]{userId}, mapper);
        } catch (EmptyResultDataAccessException e) {
            log.warn("未查询到用户，用户ID：{}", userId);
        }
        return user;
    }
}
