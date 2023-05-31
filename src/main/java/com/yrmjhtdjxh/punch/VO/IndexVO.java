package com.yrmjhtdjxh.punch.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yrmjhtdjxh.punch.domain.IndexStudent;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author ：GO FOR IT
 * @description：
 * @date ：2021/5/15 19:58
 */
@Data
public class IndexVO implements Serializable {

    private StudentVO student;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date unfinishedTime;

    private Double punchedTime;

    private Double leftTime;

    private Double todayTime;

    private Boolean isVacation;

    /**
     * 列表
     */
    private List<IndexStudent> indexStudents;

    private static final long serialVersionUID = 1L;
}
