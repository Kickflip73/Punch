package com.yrmjhtdjxh.punch.web;

import com.yrmjhtdjxh.punch.VO.ResultVO;
import com.yrmjhtdjxh.punch.accessctro.RoleControl;
import com.yrmjhtdjxh.punch.domain.Student;
import com.yrmjhtdjxh.punch.enums.UserRole;
import com.yrmjhtdjxh.punch.form.DateForm;
import com.yrmjhtdjxh.punch.form.LoginForm;
import com.yrmjhtdjxh.punch.form.StudentForm;
import com.yrmjhtdjxh.punch.form.StudentUpdateForm;
import com.yrmjhtdjxh.punch.service.StudentService;
import com.yrmjhtdjxh.punch.util.OssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author etron
 * 主要是用于student相关的控制器
 * 登录，注册，信息修改
 */
@ResponseBody
@Controller
@Api(tags = "用户模块")
@CrossOrigin
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }


    /**
     * 登录的接口
     * @param loginForm
     * @param response
     * @return
     */
    @PostMapping("/login")
    @ApiOperation("登录")
    public ResultVO login(@RequestBody @Valid LoginForm loginForm, HttpServletResponse response){
        return studentService.login(loginForm, response);
    }

    /**
     * 修改个人信息的接口
     */
    @ApiOperation("修改个人信息的接口")
    @PostMapping("/updateStudentInfo")
    public ResultVO update(@RequestBody @Valid StudentUpdateForm studentUpdateForm) {
        return studentService.updateStudentInfo(studentUpdateForm);
    }


    /**
     *  获取注册用户列表
     */
    @ApiOperation("获取所有用户")
    @GetMapping("/getRegisterUserList")
    @RoleControl(role = UserRole.ADMINISTRATOR)
    public ResultVO getRegisterUserList() {
        return studentService.getRegisterUserList();
    }

    /**
     * 获取当前用户的打卡信息，前端展示位走向图
     * @return
     */
    @ApiOperation("获取打卡走向图")
    @GetMapping("/getPunchChart")
    public ResultVO getPunchChart(){
        return studentService.getPunchChart();
    }

    /**
     * 获取当前用户的打卡信息，前端展示位走向图
     * @return
     */
    @ApiOperation("获取日期范围内的打卡走向图")
    @PostMapping("/getPunchChartByDate")
    public ResultVO getPunchChartByDate(@RequestBody @Valid DateForm dateForm){
        return studentService.getPunchChartByDate(dateForm);
    }

    /**
     * 用户登录的时候上传图片
     * @param file
     * @return
     * @throws IOException
     */
    @ApiOperation("上传图片")
    @PostMapping("/upImage")
    public ResultVO upImage(@RequestParam("file") MultipartFile file) throws IOException {
        return studentService.upImage(file);
    }
}
