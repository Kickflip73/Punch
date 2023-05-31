package com.yrmjhtdjxh.punch.service;


import com.yrmjhtdjxh.punch.VO.ResultVO;
import com.yrmjhtdjxh.punch.form.AnnouncementPublishForm;
import com.yrmjhtdjxh.punch.form.AnnouncementUpdateForm;


public interface AnnouncementService {
    

    /**
     * 发布公告
     * @param publishForm
     * @return
     */
    ResultVO publishAnnouncement(AnnouncementPublishForm publishForm);

    /**
     * 查看公告详情
     * @param announcementId
     * @return
     */
    ResultVO readAnnouncementDetail(Long announcementId);


    /**
     * 获取公告指定分页数据
     * @param
     * @return
     */
    ResultVO getList();

    ResultVO changeInfo(AnnouncementUpdateForm updateForm);

    ResultVO createAndSave(AnnouncementPublishForm publishForm);

    ResultVO publishSavedAnnouncement(Long announcementId);

    ResultVO cancelPublish(Long announcementId);

    ResultVO delete(Long announcementId);
}
