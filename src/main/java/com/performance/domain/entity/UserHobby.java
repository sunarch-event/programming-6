package com.performance.domain.entity;

import lombok.Data;

@Data
public class UserHobby {

    private Long id;
    private String hobby1;
    private String hobby2;
    private String hobby3;
    private String hobby4;
    private String hobby5;

    @Override
    public String toString() {
        
        return hobby1 + hobby2 + hobby3 + hobby4 + hobby5;
    }
}
