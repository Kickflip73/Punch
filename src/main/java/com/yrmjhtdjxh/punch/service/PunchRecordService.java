package com.yrmjhtdjxh.punch.service;


import com.yrmjhtdjxh.punch.VO.ResultVO;
import com.yrmjhtdjxh.punch.domain.PunchRecord;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.Map;

public interface PunchRecordService {

    void insert(PunchRecord punchRecord);

    PunchRecord getUnfinishedPunchByStudentID(Long studentID);

    void endRecordTime(PunchRecord punchRecord);

    void deleteShortPunchTime(Long studentID);

    ResultVO startPunch(HttpServletRequest request);

    ResultVO endPunch(HttpServletRequest request);

    ResultVO signOffAll();

    ResultVO signOffOne(Long studentID);

    boolean checkIP(String stuIP);

    Double getVacationByStudentID(Date beginDate,Date endDate,Long StudentID);

}
