package com.performance.domain.dao;

import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.performance.domain.entity.UserHobby;
import com.performance.domain.entity.UserInfo;

@Repository
public class UserDao {

    private final String SQL_FORWARD_CLOSE = "'";
    private final String SQL_CLOSED_BACKWARDS = "',";
    private final String SQL_CLOSED_PARENTHESES = "')";

    private JdbcTemplate jdbcTemplate;
    
    public UserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public void insertUserInfo (UserInfo entity) {
        String sql = new StringBuilder()
           .append("INSERT INTO user_info (last_name, first_name, prefectures, city, blood_type)")
           .append(" VALUES (")
           .append(SQL_FORWARD_CLOSE).append(entity.getLastName()).append(SQL_CLOSED_BACKWARDS)
           .append(SQL_FORWARD_CLOSE).append(entity.getFirstName()).append(SQL_CLOSED_BACKWARDS)
           .append(SQL_FORWARD_CLOSE).append(entity.getPrefectures()).append(SQL_CLOSED_BACKWARDS)
           .append(SQL_FORWARD_CLOSE).append(entity.getCity()).append(SQL_CLOSED_BACKWARDS)
           .append(SQL_FORWARD_CLOSE).append(entity.getBloodType()).append(SQL_CLOSED_PARENTHESES)
           .toString();
        jdbcTemplate.execute(sql);
    }
    
    @Transactional
    public void insertUserHobby (UserHobby entity) {
        String sql = new StringBuilder()
           .append("INSERT INTO user_hobby (id, hobby1, hobby2, hobby3, hobby4, hobby5)")
           .append(" VALUES (")
           .append(SQL_FORWARD_CLOSE).append(entity.getId()).append(SQL_CLOSED_BACKWARDS)
           .append(SQL_FORWARD_CLOSE).append(entity.getHobby1()).append(SQL_CLOSED_BACKWARDS)
           .append(SQL_FORWARD_CLOSE).append(entity.getHobby2()).append(SQL_CLOSED_BACKWARDS)
           .append(SQL_FORWARD_CLOSE).append(entity.getHobby3()).append(SQL_CLOSED_BACKWARDS)
           .append(SQL_FORWARD_CLOSE).append(entity.getHobby4()).append(SQL_CLOSED_BACKWARDS)
           .append(SQL_FORWARD_CLOSE).append(entity.getHobby5()).append(SQL_CLOSED_PARENTHESES)
           .toString();
        jdbcTemplate.execute(sql);
    }
    
    public Long selectId(UserInfo entity) {
        String sql = new StringBuilder()
           .append("SELECT id ")
           .append("FROM user_info ")
           .append("WHERE last_name || first_name = ").append(SQL_FORWARD_CLOSE)
           .append(entity.getLastName()).append(entity.getFirstName()).append(SQL_FORWARD_CLOSE)
           .append(" ORDER BY id desc")
           .append(" LIMIT 1")
           .toString();
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    public List<UserInfo> searchUserInfo() {
        String sql = new StringBuilder()
           .append("SELECT id, last_name, first_name, prefectures, city, blood_type ")
           .append("FROM user_info ")
           .append("WHERE last_name || first_name <> ").append("'試験太郎'")
           .append(" ORDER BY id")
           .toString();
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<UserInfo>(UserInfo.class));
    }

    public List<UserHobby> searchUserHobby(UserHobby targetUserHobby) {
        String sql = new StringBuilder()
           .append("SELECT id, hobby1, hobby2, hobby3, hobby4, hobby5 ")
           .append("FROM user_hobby ")
           .append("WHERE id  <> ").append(targetUserHobby.getId())
           .append(" ORDER BY id")
           .toString();
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<UserHobby>(UserHobby.class));
    }
    
    public UserInfo getTargetUserInfo() {
        String sql = new StringBuilder()
           .append("SELECT id, last_name, first_name, prefectures, city, blood_type ")
           .append("FROM user_info ")
           .append("WHERE last_name = ").append("'試験'")
           .append("AND first_name = ").append("'太郎'")
           .toString();
        return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<UserInfo>(UserInfo.class));
    }
    
    public UserHobby getTargetUserHobby(UserInfo userInfo) {
        String sql = new StringBuilder()
           .append("SELECT id, hobby1, hobby2, hobby3, hobby4, hobby5 ")
           .append("FROM user_hobby ")
           .append("WHERE id = ").append(userInfo.getId())
           .toString();
        return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<UserHobby>(UserHobby.class));
    }

    public UserMaster getTargetUserMaster() {
        String sql = new StringBuilder()
           .append("SELECT user_info.id as id, user_info.last_name as last_name, user_info.first_name as first_name, user_info.prefectures as prefectures, user_info.city as city, user_info.blood_type as blood_type, user_hobby.hobby1 as hobby1, user_hobby.hobby2 as hobby2, user_hobby.hobby3 as hobby3, user_hobby.hobby4 as hobby4, user_hobby.hobby5 as hobby5 ")
           .append("FROM user_info ")
           .append("INNER JOIN user_hobby ")
           .append("ON user_info.id = user_hobby.id ")
           .append("WHERE user_info.last_name = ").append("'試験'")
           .append("AND user_info.first_name = ").append("'太郎'")
           .toString();
        return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<UserMaster>(UserMaster.class));
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
