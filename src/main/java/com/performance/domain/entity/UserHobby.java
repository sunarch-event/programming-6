package com.performance.domain.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserHobby {

    private Long id;
    private String hobby1;
    private String hobby2;
    private String hobby3;
    private String hobby4;
    private String hobby5;

    public String toString() {
        StringBuilder sb = new StringBuilder();
        return sb.append(hobby1).append(hobby2).append(hobby3)
                 .append(hobby4).append(hobby5)
                 .toString();
    }

    public void clear() {
        this.id = 0L;
        this.hobby1 = "";
        this.hobby2 = "";
        this.hobby3 = "";
        this.hobby4 = "";
        this.hobby5 = "";
    }
}
