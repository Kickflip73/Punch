package com.yrmjhtdjxh.punch.accessctro;

import com.yrmjhtdjxh.punch.enums.UserRole;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *权限
 * @author GO FOR IT
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RoleControl {
    UserRole role();
}
