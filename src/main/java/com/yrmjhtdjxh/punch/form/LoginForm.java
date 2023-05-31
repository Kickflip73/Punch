package com.yrmjhtdjxh.punch.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author GO FOR IT
 */
@Getter
@Setter
public class LoginForm {

    @NotNull
    @Size(min = 12,max = 12,message = "学号长度应该为12位")
    private String studentID;

    @NotNull(message = "密码不能为空")
    private String password;
}
