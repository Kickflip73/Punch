package com.yrmjhtdjxh.punch.web;

import com.yrmjhtdjxh.punch.VO.ResultVO;
import com.yrmjhtdjxh.punch.service.PunchRecordService;
import com.yrmjhtdjxh.punch.service.StudentAndPunchRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @author etron
 *
 */
@ResponseBody
@Controller
@Api(tags = "主页")
@CrossOrigin
public class IndexController {

    private StudentAndPunchRecordService studentAndPunchRecordService;

    private PunchRecordService punchRecordService;

    @Autowired
    public IndexController(StudentAndPunchRecordService studentAndPunchRecordService, PunchRecordService punchRecordService) {
        this.studentAndPunchRecordService = studentAndPunchRecordService;
        this.punchRecordService = punchRecordService;
    }

    /**
     * 提供首页展示当前学生和目前的打卡信息（所有人的打卡排名情况）
     * @return
     */
    @ApiOperation("获取当前学生的打卡信息和全部学生的打卡信息")
    @GetMapping("/getStudentAndPunchInfo")
    public ResultVO getStudentAndPunchInfo() {
        return studentAndPunchRecordService.getStudentAndPunchInfo();
    }

    /**
     * 开始打卡的接口
     * @return
     */
    @PostMapping("/startPunch")
    @ApiOperation("开始打卡")
    public ResultVO startPunch( HttpServletRequest request) {
        return punchRecordService.startPunch(request);
    }

    /**
     * 结束打卡的接口
     * @return
     */
    @PostMapping("/endPunch")
    @ApiOperation("结束打卡")
    public ResultVO endPunch( HttpServletRequest request){
        return punchRecordService.endPunch(request);
    }

}
