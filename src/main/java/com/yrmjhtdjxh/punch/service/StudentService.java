package com.yrmjhtdjxh.punch.service;

import com.yrmjhtdjxh.punch.VO.ResultVO;
import com.yrmjhtdjxh.punch.VO.StudentVO;
import com.yrmjhtdjxh.punch.domain.Student;
import com.yrmjhtdjxh.punch.form.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * @author GO FOR IT
 */
public interface StudentService {

    void insert(Student student);

    StudentVO findStudentByID(Long studentID);

    void changePunch(Long studentID, int punchStatus);

    ResultVO updateStudentInfo(StudentUpdateForm studentUpdateForm);

    ResultVO login(LoginForm loginForm, HttpServletResponse response);

    ResultVO getRegisterUserList();

    ResultVO updateUserRole(StudentRoleForm form);

    ResultVO deleteUser(Long userId);

    ResultVO getPunchChart();

    ResultVO getPunchChartByDate(DateForm dateForm);

    StudentVO getCurrentStudent();

    ResultVO addStudent(StudentForm studentForm);

    ResultVO upImage(MultipartFile file) throws IOException;

    ResultVO setPunchTime(PunchStandardForm punchStandardForm);;

    ResultVO cancelPunchTime(Integer id);

    ResultVO getPunchTimeStandard();

    ResultVO modifyPwd(Long studentID);


    ResultVO addRecord(RecordForm recordForm);
}
