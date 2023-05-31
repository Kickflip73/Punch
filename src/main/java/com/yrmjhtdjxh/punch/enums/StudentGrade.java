package com.yrmjhtdjxh.punch.enums;

import lombok.Getter;

@Getter
public enum StudentGrade {

    /**
     *
     */
    FRESHMAN(0,"大一"),
    SOPH_AND_ABOVE(1,"大二及以上"),
    ALL(2,"全部");

    private Integer value;

    private String tips;

    StudentGrade(Integer value, String tips) {
        this.value = value;
        this.tips = tips;
    }
}
