package com.yrmjhtdjxh.punch.mapper;

import com.yrmjhtdjxh.punch.domain.PunchStandard;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author GO FOR IT
 */
@Repository
public interface PunchStandardMapper {
    int deleteByPrimaryKey(Integer id);

    int selectByGrade(Integer grade);

    int insert(PunchStandard record);

    PunchStandard selectByPrimaryKey(Integer id);

    List<PunchStandard> selectAll();

    int updateByPrimaryKey(PunchStandard record);

    /**
     * 只能取消假期的打卡时间设置（即stu_grade = 5）
     * @param id
     * @return
     */
    int cancelPunchTime(Integer id);

    /**
     * 获取已执行的打卡标准
     * @return
     */
    List<PunchStandard> selectAllExe();

    /**
     * 查找处于正常周的打卡标准
     * @return
     */
    PunchStandard selectAllExe2(int stuGrade);

    /**
     * 查询当前假期打卡标准的记录
     * @return
     */
    List<PunchStandard> selectAllVacation(int grade);

}