package com.yrmjhtdjxh.punch.security;

import com.yrmjhtdjxh.punch.VO.StudentVO;
import com.yrmjhtdjxh.punch.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author GO FOR IT
 */
@Service
@Slf4j
public class JwtUserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private StudentService studentServiceService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        StudentVO studentOne = studentServiceService.findStudentByID(Long.valueOf(username));
        if (studentOne == null) {
            log.info("该学生不存在");
            throw new UsernameNotFoundException(String.format("学号为 %s 的用户不存在", username));
        } else {
            Integer role = studentOne.getUserRole();
            return new JwtUser(username, studentOne.getPassword(), role);
        }
    }
}
