package com.yrmjhtdjxh.punch.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 首页展示的domain数据类
 */
@Data
@AllArgsConstructor
public class IndexStudent {
    /**
     * 该student的头像
     */
    private String avatar;

    /**
     * 姓名
     */
    private String name;

    /**
     * 学号
     */
    private Long studentID;

    /**
     * 年级
     */
    private int grade;

    /**
     * 最低打卡时间
     */
    private int standard;

    /**
     * 本周打卡时间或假期打卡时间
     */
    private Double punchedTime;

    /**
     * 今日打卡时间
     */
    private Double todayTime;

    /**
     * 本周剩余打卡时间或假期剩余打卡时间
     */
    private Double LeftTime;

    /**
     * 是否正在打卡
     */
    private boolean isPunch;


    public IndexStudent() {
    }
}
