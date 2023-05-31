package com.yrmjhtdjxh.punch.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author GO FOR IT
 */
@Data
public class PunchStandardForm {

    @NotNull
    @Max(5)
    @Min(0)
    private Integer stuGrade;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date beginDate;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    @NotNull
    private Double lowestStandard;

    @NotNull
    private String description;
}
