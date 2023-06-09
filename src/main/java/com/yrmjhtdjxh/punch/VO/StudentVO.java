package com.yrmjhtdjxh.punch.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yrmjhtdjxh.punch.domain.PunchStandard;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @author dengg
 */
@Data
public class StudentVO implements Serializable {

    /**
     * 作为自增主键
     */
    @Id
    @Column(keyColumn = "id")
    private int id;

    /**
     * 学号
     */
    @NotNull
    private Long studentID;

    /**
     * 密码
     */
    private String password;

    /**
     * 学生姓名
     */
    @NotNull
    private String name;

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

    /**
     * 最低标准
     */
    private PunchStandard punchStandard;

    /**
     * 学生的初始照片
     */
    private String photo;

    /**
     * 学生的头像
     */
    private String avatar;

    /**
     * 创建当前学生的日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 是否在打卡
     */
    private boolean isPunch;

    private Integer userRole;

    private static final long serialVersionUID = 1L;


}
