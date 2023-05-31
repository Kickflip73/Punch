package com.yrmjhtdjxh.punch.security;

import com.alibaba.fastjson.JSON;
import com.yrmjhtdjxh.punch.VO.ResultVO;
import com.yrmjhtdjxh.punch.VO.StudentVO;
import com.yrmjhtdjxh.punch.accessctro.RoleControl;
import com.yrmjhtdjxh.punch.enums.ResultEnum;
import com.yrmjhtdjxh.punch.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class AuthRoleInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private StudentService studentService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println(".....................");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=utf-8");
        Map<String, String> map = new HashMap<>();
        map.put("status", "fail");
        JSON.toJSONString(map);
        StudentVO currentStudent = studentService.getCurrentStudent();
        if (currentStudent == null) {
            return true;
        }
        log.info("============执行权限验证============");
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            RoleControl roleControl = handlerMethod.getMethodAnnotation(RoleControl.class);
            if (roleControl == null) {
                return true;
            }
            Integer roleValue = roleControl.role().getValue();
            Integer userValue = currentStudent.getUserRole();
            log.info("RoleValue:{},userRole:{}", roleValue, userValue);
            if (userValue <= roleValue) {
                return true;
            } else {
                log.info("============权限不足===============");
            }
        }
        response.getWriter().append(JSON.toJSONString(ResultVO.error(ResultEnum.LOW_AUTH)));
        return false;
    }
}
