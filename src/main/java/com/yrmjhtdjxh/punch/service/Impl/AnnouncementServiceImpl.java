package com.yrmjhtdjxh.punch.service.Impl;


import com.yrmjhtdjxh.punch.VO.AnnouncementListVO;
import com.yrmjhtdjxh.punch.VO.AnnouncementVO;
import com.yrmjhtdjxh.punch.VO.ResultVO;
import com.yrmjhtdjxh.punch.VO.StudentVO;
import com.yrmjhtdjxh.punch.domain.Announcement;
import com.yrmjhtdjxh.punch.enums.AnnouncementStatus;
import com.yrmjhtdjxh.punch.enums.ResultEnum;
import com.yrmjhtdjxh.punch.enums.UserRole;
import com.yrmjhtdjxh.punch.form.AnnouncementPublishForm;
import com.yrmjhtdjxh.punch.form.AnnouncementUpdateForm;
import com.yrmjhtdjxh.punch.mapper.AnnouncementMapper;
import com.yrmjhtdjxh.punch.redis.AnnouncementKey;
import com.yrmjhtdjxh.punch.redis.RedisService;
import com.yrmjhtdjxh.punch.service.AnnouncementService;
import com.yrmjhtdjxh.punch.service.StudentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author zty
 */
@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    @Autowired
    private StudentService studentService;

    @Autowired
    private AnnouncementMapper announcementMapper;

    @Autowired
    private RedisService redisService;

    private boolean insertAndPublish(Announcement announcement) {
        if (announcementMapper.insert(announcement) == 1){
            redisService.set(AnnouncementKey.getById, announcement.getId() + "", announcement);
            return true;
        }
        return false;
    }

    public boolean update(Announcement announcement) {
        announcement.setUpdateTime(new Date());
        if (announcementMapper.updateByPrimaryKey(announcement) == 1){
            if (announcement.getStatus().equals(AnnouncementStatus.PUBLISHED.getValue())){
                redisService.set(AnnouncementKey.getById, announcement.getId() + "", announcement);
            }
            return true;
        }
        return false;
    }

    @Override
    public ResultVO delete(Long id) {
        if (announcementMapper.selectByPrimaryKeyAndStatus(id,null) == null){
            throw new IllegalArgumentException("公告不存在");
        }
        redisService.delete(AnnouncementKey.getById, id + "");
        redisService.delete(AnnouncementKey.getClickTimesById, id + "");
        announcementMapper.deleteByPrimaryKey(id);
        return ResultVO.success();
    }

    private Announcement selectByIdAndStatus(Long announcementId, Integer status) {
        Announcement announcement = redisService.get(AnnouncementKey.getById, announcementId + "", Announcement.class);
        if (announcement == null){
            announcement = announcementMapper.selectByPrimaryKeyAndStatus(announcementId, status);
            if (announcement != null && announcement.getStatus().equals(AnnouncementStatus.PUBLISHED.getValue())){
                redisService.set(AnnouncementKey.getById, announcementId + "", announcement);
            }
        }
        return announcement;
    }

    @Override
    public ResultVO publishAnnouncement(AnnouncementPublishForm publishForm) {

        Announcement announcement = convertFormToModel(publishForm,AnnouncementStatus.PUBLISHED);
        announcement.setPublisherId(studentService.getCurrentStudent().getStudentID());
        if (insertAndPublish(announcement)){
            //设置阅读次数
            redisService.set(AnnouncementKey.getClickTimesById, announcement.getId() + "", 0);
            return ResultVO.success();
        }
        return ResultVO.error(ResultEnum.SAVE_WRONG);
    }

    @Override
    public ResultVO readAnnouncementDetail(Long announcementId) {


        // 根据ID得到已经发布的公告 或者没有发布的公告
        Announcement announcement = selectByIdAndStatus(announcementId, AnnouncementStatus.PUBLISHED.getValue());

        // 如果是管理员，已经发布的没有找到，就去找保存但是还没有发布的
        if(announcement == null) {
            announcement = selectByIdAndStatus(announcementId, AnnouncementStatus.SAVE.getValue());
        }

        if (announcement != null){
            AnnouncementVO announcementVO = new AnnouncementVO();
            BeanUtils.copyProperties(announcement, announcementVO);
            if (announcement.getStatus().equals(AnnouncementStatus.PUBLISHED.getValue())) {
                redisService.incr(AnnouncementKey.getClickTimesById, announcementId + "");
                Integer clickTimes = redisService.get(AnnouncementKey.getClickTimesById, announcementId + "", Integer.class);
                announcementVO.setClickTimes(clickTimes);
            }
            return ResultVO.success(announcementVO);
        }
        return ResultVO.error(ResultEnum.NOT_EXIST_ANN);
    }

    @Override
    public ResultVO getList() {
        //使用无条件查询
        List<AnnouncementListVO> listVOS = announcementMapper.selectAllByStatus(null);
        return ResultVO.success(listVOS);
    }

    @Override
    public ResultVO changeInfo(AnnouncementUpdateForm updateForm) {
        Announcement announcement = selectByIdAndStatus(updateForm.getAnnouncementId(),null);
        if (announcement == null){
            return ResultVO.error(ResultEnum.NOT_EXIST_ANN);
        }
        announcement.setTitle(updateForm.getTitle());
        announcement.setContent(updateForm.getContent());
        if (update(announcement)) {
            return ResultVO.success();
        }
        return ResultVO.error(ResultEnum.WRONG_UPDATE);
    }

    @Override
    public ResultVO createAndSave(AnnouncementPublishForm publishForm) {
        Announcement announcement = convertFormToModel(publishForm,AnnouncementStatus.SAVE);
        announcement.setPublisherId(studentService.getCurrentStudent().getStudentID());
        announcementMapper.insert(announcement);
        return ResultVO.success();
    }

    @Override
    public ResultVO publishSavedAnnouncement(Long announcementId) {
        Announcement announcement = announcementMapper.selectByPrimaryKeyAndStatus(announcementId,AnnouncementStatus.SAVE.getValue());
        if (announcement == null){
            return ResultVO.error(ResultEnum.NOT_EXIST_ANN);
        }
        //先更新缓存状态
        announcement.setStatus(AnnouncementStatus.PUBLISHED.getValue());
        redisService.set(AnnouncementKey.getById, announcement.getId() + "", announcement);
        //设置阅读次数
        redisService.set(AnnouncementKey.getClickTimesById, announcement.getId() + "", 0);
        announcementMapper.updateAnnouncementStatusById(AnnouncementStatus.PUBLISHED.getValue(),announcementId);
        return ResultVO.success();
    }

    @Override
    public ResultVO cancelPublish(Long announcementId) {
        redisService.delete(AnnouncementKey.getById, announcementId + "");
        announcementMapper.updateAnnouncementStatusById(AnnouncementStatus.SAVE.getValue(),announcementId);
        return ResultVO.success();
    }


    private Announcement convertFormToModel(AnnouncementPublishForm publishForm,AnnouncementStatus status){
        //可设置发布者
        Announcement announcement = new Announcement();
        announcement.setTitle(publishForm.getTitle());
        announcement.setContent(publishForm.getContent());
        announcement.setPublishTime(new Date());
        announcement.setUpdateTime(new Date());
        announcement.setStatus(status.getValue());
        return announcement;
    }

    private boolean userAuthentication( UserRole userRole) {
        StudentVO studentVO = studentService.getCurrentStudent();
        if (studentVO == null){
            return false;
        }else {
            // userRole 越小权限越大
            return userRole == null || userRole.getValue() >= (studentVO.getUserRole());
        }
    }
}
