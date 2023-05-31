package com.yrmjhtdjxh.punch.security;

import com.alibaba.fastjson.JSON;
import com.yrmjhtdjxh.punch.VO.ResultVO;
import com.yrmjhtdjxh.punch.enums.ResultEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

/**
 * @author GO FOR IT
 */
@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {

    private static final long serialVersionUID = -8970718410437077606L;

    @Override
    public void commence(HttpServletRequest httpServletRequest,
                         HttpServletResponse httpServletResponse,
                         AuthenticationException e) throws IOException, ServletException {
        httpServletResponse.setHeader("Access_Control_Allow_Origin", "*");
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("text/html; charset=utf-8");
        log.info("需要身份认证:{}");
        httpServletResponse.getWriter().append(JSON.toJSONString(ResultVO.error(ResultEnum.CON_OUT_DATE)));
    }
}
