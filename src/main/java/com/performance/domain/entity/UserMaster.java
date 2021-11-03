package com.performance.domain.entity;

import lombok.Data;

@Data
public class UserMaster {

    private Long id;
    private String lastName;
    private String firstName;
    private String prefectures;
    private String city;
    private String bloodType;
    private String hobby1;
    private String hobby2;
    private String hobby3;
    private String hobby4;
    private String hobby5;

    @Override
    public String toString() {
        
        return lastName + firstName + prefectures + city + bloodType + hobby1 + hobby2 + hobby3 + hobby4 + hobby5;
    }
}
