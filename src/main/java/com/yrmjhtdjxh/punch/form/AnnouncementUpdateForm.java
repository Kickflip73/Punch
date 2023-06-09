package com.yrmjhtdjxh.punch.form;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * @author GO FOR IT
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AnnouncementUpdateForm extends AnnouncementPublishForm {
    @NotNull(message = "公告id不能为空")
    private Long announcementId;
}