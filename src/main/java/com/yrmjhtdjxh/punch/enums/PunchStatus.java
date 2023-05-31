package com.yrmjhtdjxh.punch.enums;

import lombok.Getter;

@Getter
public enum PunchStatus {
    /**
     * 打卡状态
     */
    NOT_PUNCHING(0, "未打卡"),
    PUNCHING(1,"打卡中");

    private Integer value;

    private String tips;

    PunchStatus(Integer value, String tips) {
        this.value = value;
        this.tips = tips;
    }
}
