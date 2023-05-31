package com.yrmjhtdjxh.punch.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.Date;

/**
 * @author GO FOR IT
 */
@Data
public class PunchStandard implements Serializable {
    @Id
    private Integer id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date beginDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endDate;

    private Boolean isExe;

    private Integer stuGrade;

    private Double lowestStandard;

    private String description;

    private static final long serialVersionUID = 1L;

}