package com.yrmjhtdjxh.punch.util;

import com.yrmjhtdjxh.punch.mapper.PunchStandardMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;

/**
 * @author ：GO FOR IT
 * @description：
 * @date ：2021/3/18 16:53
 */
public class GradeUtil {
    @Autowired
    private static PunchStandardMapper punchStandardMapper;
    public static Integer getStandardTimeByStuID(Long studentID){
        String s = String.valueOf(studentID);
        String grade = s.substring(0, 3);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        Period between = Period.between(LocalDate.now(), LocalDate.parse(grade + "-09"));
        int years = between.getYears();
        return punchStandardMapper.selectByGrade(years);
    }
}
