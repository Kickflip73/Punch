package com.yrmjhtdjxh.punch.mapper;

import com.yrmjhtdjxh.punch.VO.StudentVO;
import com.yrmjhtdjxh.punch.domain.Student;
import com.yrmjhtdjxh.punch.form.StudentUpdateForm;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * @author etron
 */
@Repository
public interface StudentMapper {

    void insert(Student student);

    StudentVO findStudentByID(Long studentID);

    List<StudentVO> getAllByRole(@Param("userRole")Integer userRole);

    void updatePunchByStudentID(Long studentID, int punchStatus);

    int deleteStudentByUserId(@Param("userId")Long userId);

    void updateStudentInfo(@Param("student") StudentUpdateForm student,@Param("studentID") Long studentID);

    /**
     * 只是为了更新数据库结构
     * @return
     */
    List<String> getAllStudentID();

    int updateAvatar(@Param("avatarUrl") String avatarUrl, @Param("studentID") Long StudentID);

    int modifyPwd(@Param("studentID") Long studentID,@Param("pwd") String pwd);

    Student selectByStudentId(@Param("studentID") Long studentId);
}
