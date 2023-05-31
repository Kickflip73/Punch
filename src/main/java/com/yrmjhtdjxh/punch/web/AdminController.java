package com.yrmjhtdjxh.punch.web;

import com.yrmjhtdjxh.punch.VO.ResultVO;
import com.yrmjhtdjxh.punch.accessctro.RoleControl;
import com.yrmjhtdjxh.punch.enums.UserRole;
import com.yrmjhtdjxh.punch.form.PunchStandardForm;
import com.yrmjhtdjxh.punch.form.RecordForm;
import com.yrmjhtdjxh.punch.form.StudentForm;
import com.yrmjhtdjxh.punch.form.StudentRoleForm;
import com.yrmjhtdjxh.punch.service.PunchRecordService;
import com.yrmjhtdjxh.punch.service.StudentAndPunchRecordService;
import com.yrmjhtdjxh.punch.service.StudentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author GO FOR IT
 */
@RestController
@RequestMapping("/admin")
@Api(tags = "管理员接口")
@CrossOrigin
public class AdminController {

    @Autowired
    private PunchRecordService punchRecordService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentAndPunchRecordService studentAndPunchRecordService;

    /**
     * 删除用户及相关信息
     * @param userId
     * @return
     */
    @GetMapping("/deleteUser")
    @ApiOperation("删除用户")
    @RoleControl(role = UserRole.ADMINISTRATOR)
    public ResultVO deleteUser(@RequestParam Long userId){
        return studentService.deleteUser(userId);
    }

    /**
     * 管理员设置某个时间段打卡
     * @param punchStandardForm
     * @return
     */
    @ApiOperation("设置打卡时间")
    @PostMapping("/setPunchTime")
    @RoleControl(role = UserRole.SUPER_ADMINISTRATOR)
    public ResultVO setPunchTime(@RequestBody @Valid PunchStandardForm punchStandardForm ){
        return studentService.setPunchTime(punchStandardForm);
    }

    /**
     * 管理员取消某个时间段打卡
     * @param id
     * @return
     */
    @ApiOperation("取消已设置打卡时间")
    @GetMapping("/cancelPunchTime")
    @RoleControl(role = UserRole.SUPER_ADMINISTRATOR)
    public ResultVO cancelPunchTime(@RequestParam Integer id){
        return studentService.cancelPunchTime(id);
    }

    /**
     * 给所有正在打卡的学生签退
     * @return
     */
    @GetMapping("/signOffAll")
    @ApiOperation("给所有正在打卡的学生签退")
    @RoleControl( role = UserRole.SUPER_ADMINISTRATOR)
    public ResultVO signOffAll(){
        return punchRecordService.signOffAll();
    }

    /**
     * 根据学号签退指定学生
     * @param studentID
     * @return
     */
    @GetMapping("/signOffOne")
    @ApiOperation("签退指定学生")
    @RoleControl(role = UserRole.SUPER_ADMINISTRATOR)
    public ResultVO signOffOne(@RequestParam Long studentID){
        return punchRecordService.signOffOne(studentID);
    }

    @ApiOperation("更新用户角色  1、超级管理员 2、普通管理员 3、普通用户")
    @PostMapping("/updateUserRole")
    @RoleControl(role = UserRole.SUPER_ADMINISTRATOR)
    public ResultVO updateUserRole(@RequestBody @Valid StudentRoleForm form){
        return studentService.updateUserRole(form);
    }

    @ApiOperation("超级管理员添加学生")
    @PostMapping("/addStudent")
    @RoleControl(role = UserRole.SUPER_ADMINISTRATOR)
    public ResultVO addStudent(@RequestBody @Valid StudentForm studentForm){
        return studentService.addStudent(studentForm);
    }

    @ApiOperation("超级管理员初始化密码")
    @GetMapping("/modifyPwd")
    @RoleControl(role = UserRole.SUPER_ADMINISTRATOR)
    public ResultVO modifyPwd(@RequestParam("studentID") Long studentID){
        return studentService.modifyPwd(studentID);
    }

    /**
     * 获取所有的打卡时间标准
     * @return
     */
    @ApiOperation("获取所有的打卡时间标准")
    @GetMapping("/getPunchStandard")
    public ResultVO getPunchStandard(){
        return studentService.getPunchTimeStandard();
    }

    @ApiOperation("获取上周打卡时间")
    @GetMapping("/getLastPunchedTimeAll")
    @RoleControl(role = UserRole.SUPER_ADMINISTRATOR)
    public ResultVO getLastPunchedTimeAll(){
        return studentAndPunchRecordService.getPunchedTimeAll();
    }

    @ApiOperation("给学生加打卡时长")
    @PostMapping("/addRecord")
    @RoleControl(role = UserRole.SUPER_ADMINISTRATOR)
    public ResultVO addRecord(@RequestBody RecordForm recordForm){
        return studentService.addRecord(recordForm);
    }

}
