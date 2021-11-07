package com.performance.domain.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfo {

    private Long id;
    private String lastName;
    private String firstName;
    private String prefectures;
    private String city;
    private String bloodType;

    public String toString() {
        StringBuilder sb = new StringBuilder();
        return sb.append(lastName).append(firstName).append(prefectures)
                 .append(city).append(bloodType)
                 .toString();
    }

}
