package com.yrmjhtdjxh.punch.VO;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @author GO FOR IT
 */
@Data
public class TimeRecordVO implements Serializable {

    private Float totalHourOfOneDay;

    @ApiModelProperty("时间")
//    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
//    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;

    private static final long serialVersionUID = 1L;
}
