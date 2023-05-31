package com.yrmjhtdjxh.punch.VO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author ：GO FOR IT
 * @description：
 * @date ：2021/4/8 17:13
 */
@Data
public class PunchedTimeVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("总时间")
    private Double totalTime;

    @ApiModelProperty("姓名")
    private String name;

    @ApiModelProperty("年级")
    private Integer grade;

    @ApiModelProperty("是否完成")
    private Boolean isFinished;

}
