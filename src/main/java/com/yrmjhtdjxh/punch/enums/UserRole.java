package com.yrmjhtdjxh.punch.enums;

import lombok.Getter;

/**
 * @author dengg
 */
@Getter
public enum  UserRole {

    /**
     *
     */
    SUPER_ADMINISTRATOR(1,"超级管理员"),
    ADMINISTRATOR(2,"普通管理员"),
    STUDENT(3,"普通学生");

    private Integer value;

    private String tips;

    UserRole(Integer value, String tips) {
        this.value = value;
        this.tips = tips;
    }

}
