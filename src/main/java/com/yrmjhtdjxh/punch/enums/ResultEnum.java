package com.yrmjhtdjxh.punch.enums;

import lombok.Getter;

/**
 * @author GO FOR IT
 */
@Getter
public enum ResultEnum {
    /**
     *
     */
    SAVE_WRONG(1,"保存错误"),
    WRONG_UPDATE(2,"更新失败"),
    LOW_AUTH(3,"权限不足"),
    CON_OUT_DATE(4,"密码错误，请重新登陆"),
    TIME_OVERLAP(5,"打卡时间重叠"),
    CANCEL_FAILED(6,"取消失败或该记录没有实施"),
    USER_HAS_EXIST(7,"用户已经存在"),
    WRONG_FORMAT(8,"上传文件格式错误"),
    NOT_PUNCHING(9,"没有打卡或者没有登录"),
    HAVE_PUNCHED(10,"已经在打卡或者没有登录"),
    WRONG_IP(11,"请尝试连接CINSC1007或CINS1007-2,WIFI密码：你懂的！！！"),
    TOO_LOW_TIME(11,"当前打卡时间过短小于30分钟或者识别为隔天打卡记录，请稍后重新打卡"),
    CAN_NOT_CANCEL_BASE(12,"不能取消平常周的打卡设置"),
    TIME_WRONG(13,"设置时间小于等于0"),
    NO_STUDENT(14,"没有该学生,请检查学号是否正确"),
    NOT_EXIST_ANN(500,"公告不存在"),
    NOT_EXIST_USER(501,"用户不存在");


    private Integer code;
    private String msg;

    ResultEnum(Integer code, String msg){
        this.code = code;
        this.msg = msg;
    }
}
