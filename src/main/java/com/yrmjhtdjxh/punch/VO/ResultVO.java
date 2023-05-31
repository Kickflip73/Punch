package com.yrmjhtdjxh.punch.VO;


import com.yrmjhtdjxh.punch.enums.ResultEnum;
import lombok.Data;

/**
 * @author dengg
 */
@Data
public class ResultVO<T> {

    private Integer code;
    private String msg;
    private T data;

    private ResultVO(T data) {
        this.code=0;
        this.msg="success";
        this.data = data;
    }

    private ResultVO() {
        this.code=0;
        this.msg="success";
    }

    public ResultVO(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ResultVO(Integer code, String msg,T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public ResultVO(ResultEnum resultEnum) {
        this.code = resultEnum.getCode();
        this.msg = resultEnum.getMsg();
    }

    public static <T> ResultVO<T> error(ResultEnum resultEnum){
        return new ResultVO<>(resultEnum.getCode(), resultEnum.getMsg());
    }

    public static <T> ResultVO<T> error(ResultEnum resultEnum,T data){
        return new ResultVO<>(resultEnum.getCode(), resultEnum.getMsg(),data);
    }

    public static <T> ResultVO success(T data) {
        return new ResultVO<>(data);
    }

    public static ResultVO success() {
        return new ResultVO();
    }
}
