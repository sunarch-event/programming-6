package com.performance.domain.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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

    public String toString() {
        StringBuilder sb = new StringBuilder();
        return sb.append(lastName).append(firstName).append(prefectures)
                 .append(city).append(bloodType).append(hobby1)
                 .append(hobby2).append(hobby3).append(hobby4)
                 .append(hobby5)
                 .toString();
    }

}