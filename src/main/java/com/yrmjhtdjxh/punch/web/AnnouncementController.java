package com.yrmjhtdjxh.punch.web;


import com.yrmjhtdjxh.punch.VO.ResultVO;
import com.yrmjhtdjxh.punch.form.AnnouncementPublishForm;
import com.yrmjhtdjxh.punch.form.AnnouncementUpdateForm;
import com.yrmjhtdjxh.punch.service.AnnouncementService;
import com.yrmjhtdjxh.punch.util.GetIPAddressUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * @Author: clf
 * @Date: 19-1-25
 * @Description:
 * 公告接口
 */
@RestController
@RequestMapping("/announcement")
@Api(tags = "公告模块")
@CrossOrigin
public class AnnouncementController {

    private AnnouncementService announcementService;

    @Autowired
    public AnnouncementController(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    /**
     * 发布公告
     * @param publishForm
     * @return
     */
    @PostMapping(value = "/publish", name = "发布公告")
    @ApiOperation("发布公告")
    public ResultVO publish(@RequestBody @Valid AnnouncementPublishForm publishForm){
        return announcementService.publishAnnouncement(publishForm);
    }

    /**
     * 阅读公告详情
     * @param announcementId
     * @return
     */
    @GetMapping(value = "/readDetail", name = "阅读公告详情")
    @ApiOperation("阅读公告详情")
    public ResultVO readDetail(@RequestParam Long announcementId){
        return announcementService.readAnnouncementDetail(announcementId);
    }

    /**
     * 公告列表
     * @return
     */
    @GetMapping(value = "/list", name = "公告列表")
    @ApiOperation("获取公告列表")
    public ResultVO list(){
        return announcementService.getList();
    }


    /**
     *  删除公告
     * @param announcementId
     * @return
     */
    @GetMapping(value = "/delete", name = "删除公告")
    @ApiOperation("根据ID删除公告")
    public ResultVO delete(@RequestParam Long announcementId){
        return announcementService.delete(announcementId);
    }

    /**
     * 修改公告
     * @param updateForm
     * @return
     */
    @PostMapping(value = "/update", name = "修改公告")
    @ApiOperation("修改公告")
    public ResultVO update(@Valid @RequestBody AnnouncementUpdateForm updateForm){
        return announcementService.changeInfo(updateForm);
    }

    /**
     * 创建并保存公告
     * @param publishForm
     * @return
     */
    @PostMapping("/createAndSave")
    @ApiOperation("创建并保存公告")
    public ResultVO createAndSave(@RequestBody @Valid AnnouncementPublishForm publishForm){
        return announcementService.createAndSave(publishForm);
    }


    /**
     * 发布未发布的公告
     * @param announcementId
     * @return
     */
    @ApiOperation("发布未发布的公告")
    @GetMapping("/publishSavedAnnouncement")
    public ResultVO publishSavedAnnouncement(@RequestParam Long announcementId){
        return announcementService.publishSavedAnnouncement(announcementId);
    }

    /**
     * 取消发布公告
     * @param announcementId
     * @return
     */
    @ApiOperation("取消发布公告")
    @GetMapping("/cancelPublish")
    public ResultVO cancelPublish(@RequestParam Long announcementId){
        return announcementService.cancelPublish(announcementId);
    }

//    @GetMapping("/test")
//    public ResultVO test(HttpServletRequest request){
//        String ip = GetIPAddressUtils.getIpAddress(request);
//        return ResultVO.success(ip);
//    }

}
