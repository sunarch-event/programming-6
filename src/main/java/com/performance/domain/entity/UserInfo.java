package com.performance.domain.entity;

import lombok.Data;

@Data
public class UserInfo {

    private Long id;
    private String lastName;
    private String firstName;
    private String prefectures;
    private String city;
    private String bloodType;

    @Override
    public String toString() {
        
        return lastName + firstName + prefectures + city + bloodType;
    }
}
