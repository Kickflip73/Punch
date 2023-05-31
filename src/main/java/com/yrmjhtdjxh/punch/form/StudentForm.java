package com.yrmjhtdjxh.punch.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author GO FOR IT
 */
@Data
public class StudentForm {

    /**
     * 学号
     */
    @NotNull
    private Long studentID;

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
     * 学生的初始照片
     */
    @ApiModelProperty("QQ号,非必须")
    private String qq;


}
