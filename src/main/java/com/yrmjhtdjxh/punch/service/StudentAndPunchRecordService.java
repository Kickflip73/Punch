package com.yrmjhtdjxh.punch.service;

import com.yrmjhtdjxh.punch.VO.ResultVO;
import com.yrmjhtdjxh.punch.domain.PunchStandard;

import javax.servlet.http.HttpSession;
import java.util.Map;

public interface StudentAndPunchRecordService {

    ResultVO getStudentAndPunchInfo();

    /**
     * 获取已打卡的时间，二维表格
     * @return
     */
    ResultVO getPunchedTimeAll();
}
