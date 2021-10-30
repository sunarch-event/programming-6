package com.performance.domain.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.performance.domain.entity.UserHobby;
import com.performance.domain.entity.UserInfo;

@Repository
public class UserDao {

    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    public UserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public void insertUserInfo (UserInfo entity) {
        String sql = new StringBuilder()
           .append("INSERT INTO user_info (last_name, first_name, prefectures, city, blood_type)")
           .append(" VALUES (")
           .append("'" + entity.getLastName() + "', ")
           .append("'" + entity.getFirstName() + "', ")
           .append("'" + entity.getPrefectures() + "', ")
           .append("'" + entity.getCity() + "', ")
           .append("'" + entity.getBloodType() + "')")
           .toString();
        jdbcTemplate.execute(sql);
    }
    
    @Transactional
    public void insertUserHobby (UserHobby entity) {
        String sql = new StringBuilder()
           .append("INSERT INTO user_hobby (id, hobby1, hobby2, hobby3, hobby4, hobby5)")
           .append(" VALUES (")
           .append("'" + entity.getId() + "', ")
           .append("'" + entity.getHobby1() + "', ")
           .append("'" + entity.getHobby2() + "', ")
           .append("'" + entity.getHobby3() + "', ")
           .append("'" + entity.getHobby4() + "', ")
           .append("'" + entity.getHobby5() + "')")
           .toString();
        jdbcTemplate.execute(sql);
    }
    
    public Long selectId(UserInfo entity) {
        String sql = new StringBuilder()
           .append("SELECT id ")
           .append("FROM user_info ")
           .append("WHERE last_name || first_name = " + "'" + entity.getLastName() + entity.getFirstName() + "'")
           .append(" ORDER BY id desc")
           .append(" LIMIT 1")
           .toString();
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    public List<UserInfo> searchUserInfo() {
        String sql = new StringBuilder()
           .append("SELECT id, last_name, first_name, prefectures, city, blood_type ")
           .append("FROM user_info ")
           .append("WHERE last_name || first_name <> " + "'試験太郎'")
           .append(" ORDER BY id")
           .toString();
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<UserInfo>(UserInfo.class));
    }

    public List<UserHobby> searchUserHobby(UserHobby targetUserHobby) {
        String sql = new StringBuilder()
           .append("SELECT id, hobby1, hobby2, hobby3, hobby4, hobby5 ")
           .append("FROM user_hobby ")
           .append("WHERE id  <> " + targetUserHobby.getId())
           .append(" ORDER BY id")
           .toString();
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<UserHobby>(UserHobby.class));
    }
    
    public UserInfo getTargetUserInfo() {
        String sql = new StringBuilder()
           .append("SELECT id, last_name, first_name, prefectures, city, blood_type ")
           .append("FROM user_info ")
           .append("WHERE last_name = " + "'試験'")
           .append("AND first_name = " + "'太郎'")
           .toString();
        return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<UserInfo>(UserInfo.class));
    }
    
    public UserHobby getTargetUserHobby(UserInfo userInfo) {
        String sql = new StringBuilder()
           .append("SELECT id, hobby1, hobby2, hobby3, hobby4, hobby5 ")
           .append("FROM user_hobby ")
           .append("WHERE id = " + userInfo.getId())
           .toString();
        return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<UserHobby>(UserHobby.class));
    }
    
    public int searchCount() {
        String sql = "SELECT COUNT(*) FROM user_info";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    public void truncateUserInfo() {
        String sql = "TRUNCATE TABLE user_info";
        jdbcTemplate.execute(sql);
    }
    
    public void truncateUserHobby() {
        String sql = "TRUNCATE TABLE user_hobby";
        jdbcTemplate.execute(sql);
    }
    
}
