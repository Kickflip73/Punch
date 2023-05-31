package com.yrmjhtdjxh.punch.VO;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author dengg
 */
@Data
public class PunchChartVO implements Serializable {

    private List<TimeRecordVO> list;
    private static final long serialVersionUID = 1L;

}
