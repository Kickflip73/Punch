package com.yrmjhtdjxh.punch.service;

import com.yrmjhtdjxh.punch.form.StudentForm;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StudentServiceTest {
    @Autowired
    private StudentService studentService;

    /**
     * 新增学生
     * */
    @Test
    public void addStudentTest() {
        long studentId = 202131090241L;    //学号
        String name = "巩瀚林";    //姓名
        int sex = 1;      //性别，1：男，2：女
        String qq = "1662648150";    //QQ号
        int grade = Integer.parseInt(String.valueOf(studentId).substring(0, 3));    //年级

        StudentForm studentForm = new StudentForm();
        studentForm.setStudentID(studentId);
        studentForm.setName(name);
        studentForm.setSex(sex);
        studentForm.setGrade(grade);
        studentForm.setQq(qq);

        studentService.addStudent(studentForm);
    }
}
