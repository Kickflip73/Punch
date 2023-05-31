package com.yrmjhtdjxh.punch.form;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author ：GO FOR IT
 * @description：
 * @date ：2021/3/25 17:31
 */
@Data
public class StudentUpdateForm {
    /**
     * 密码
     */
    @Size(min = 6)
    private String password;

    /**
     * 性别 1：男 0：女
     */
    @Max(1)
    @Min(0)
    private int sex;

    /**
     * 学生的年级
     */
    @NotNull
    private int grade;
}
