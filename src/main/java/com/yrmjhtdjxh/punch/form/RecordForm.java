package com.yrmjhtdjxh.punch.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * @author wangyujie
 */
@Data
public class RecordForm {

    @NotNull
    @Size(min = 12,max = 12,message = "学号长度应该为12位")
    private Long studentID;


    @NotNull
    private Double recordTime;

}
