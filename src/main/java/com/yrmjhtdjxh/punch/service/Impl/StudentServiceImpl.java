package com.yrmjhtdjxh.punch.service.Impl;

import com.yrmjhtdjxh.punch.VO.ResultVO;
import com.yrmjhtdjxh.punch.VO.StudentVO;
import com.yrmjhtdjxh.punch.VO.TimeRecordVO;
import com.yrmjhtdjxh.punch.domain.PunchRecord;
import com.yrmjhtdjxh.punch.domain.PunchStandard;
import com.yrmjhtdjxh.punch.domain.Student;
import com.yrmjhtdjxh.punch.enums.ResultEnum;
import com.yrmjhtdjxh.punch.enums.UserRole;
import com.yrmjhtdjxh.punch.form.*;
import com.yrmjhtdjxh.punch.mapper.PunchRecordMapper;
import com.yrmjhtdjxh.punch.mapper.PunchStandardMapper;
import com.yrmjhtdjxh.punch.mapper.StudentMapper;
import com.yrmjhtdjxh.punch.mapper.StudentRoleMapper;
import com.yrmjhtdjxh.punch.redis.RecordTimeKey;
import com.yrmjhtdjxh.punch.redis.RedisService;
import com.yrmjhtdjxh.punch.security.JwtProperties;
import com.yrmjhtdjxh.punch.security.JwtUserDetailServiceImpl;
import com.yrmjhtdjxh.punch.service.StudentService;
import com.yrmjhtdjxh.punch.util.JwtTokenUtil;
import com.yrmjhtdjxh.punch.util.OssUtil;
import com.yrmjhtdjxh.punch.util.TimeToListUtil;
import jdk.nashorn.internal.runtime.linker.LinkerCallSite;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnailator;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.management.relation.Role;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StudentServiceImpl implements StudentService {

    @Value("${img.location}")
    private String location;

    @Value("${img.url}")
    private String url;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailServiceImpl jwtUserDetailService;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private StudentRoleMapper studentRoleMapper;

    @Autowired
    private PunchRecordMapper punchRecordMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PunchStandardMapper punchStandardMapper;
    @Autowired
    private StudentService studentService;
    @Autowired
    private RedisService redisService;

    @Override
    public void insert(Student student) {
        studentMapper.insert(student);
    }


    @Override
    public StudentVO findStudentByID(Long studentID) {
        return studentMapper.findStudentByID(studentID);
    }

    @Override
    public ResultVO getRegisterUserList() {
        return ResultVO.success(studentMapper.getAllByRole(null));
    }

    @Override
    public ResultVO deleteUser(Long userId) {
        StudentVO studentVO = getCurrentStudent();
//        if (userId == studentVO.getId()) {
//            return ResultVO.error(500,"删除自己？");
//        }
        //删除角色信息
        studentRoleMapper.deleteByUserId(userId);
        //删除用户信息
        studentMapper.deleteStudentByUserId(userId);
        //删除打卡信息
        punchRecordMapper.deleteByStudentID(userId);
        return ResultVO.success();
    }

    @Override
    public ResultVO updateUserRole(StudentRoleForm form) {
        if (studentMapper.findStudentByID(form.getUserId()) == null){
            return ResultVO.error(ResultEnum.NOT_EXIST_USER);
        }
        studentRoleMapper.updateUserRole(form.getUserId(),form.getUserRole());
        return ResultVO.success();
    }

    @Override
    public ResultVO updateStudentInfo(StudentUpdateForm student) {
        StudentVO currentStudent = studentService.getCurrentStudent();
        student.setPassword(passwordEncoder.encode(student.getPassword()));
        student.setGrade(currentStudent.getGrade());
        studentMapper.updateStudentInfo(student,currentStudent.getStudentID());
        return ResultVO.success();
    }


    @Override
    public ResultVO login(LoginForm loginForm, HttpServletResponse response) {
        String studentID = loginForm.getStudentID();
        String password = loginForm.getPassword();
        log.info("尝试登录，学号：" + studentID + "密码：" + password);

        //判断用户是否存在，并返回为UserDetails类
        UserDetails userDetails = jwtUserDetailService.loadUserByUsername(studentID);
        StudentVO student = findStudentByID(Long.valueOf(studentID));

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(studentID, password, userDetails.getAuthorities());
        Authentication authentication = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        final String realToken = jwtTokenUtil.generateToken(userDetails);
        response.addHeader(jwtProperties.getTokenName(), realToken);

        Map<String, String> map = new HashMap<>(5);
        map.put("token",realToken);
        map.put("role", String.valueOf(student.getUserRole()));
        return ResultVO.success(map);
    }

    /**
     * 根据学号提取出年级
     * @param studentID
     * @return
     */
    public int getGrade(Long studentID) {
        return Integer.parseInt(studentID.toString().substring(0,4));
    }

    @Override
    public void changePunch(Long studentID, int punchStatus) {
        studentMapper.updatePunchByStudentID(studentID, punchStatus);
    }

    @Override
    public ResultVO getPunchChart() {
        DateForm dateForm = new DateForm();
        dateForm.setBeginDate(new Date(System.currentTimeMillis()-30*86400000L));
        dateForm.setEndDate(new Date());
        return getPunchChartByDate(dateForm);
    }

    @Override
    public ResultVO getPunchChartByDate(DateForm dateForm) {
        StudentVO studentVO = getCurrentStudent();
        Map map = redisService.get(RecordTimeKey.getRecordTimeKey, studentVO.getStudentID().toString(), Map.class);
        if (map != null){
            return ResultVO.success(map);
        }else {
            List<TimeRecordVO> list = punchRecordMapper.getChartByTimeAndUser(studentVO.getStudentID(),dateForm.getBeginDate(), dateForm.getEndDate());
            log.info("list = {}",list);
            Map relMap = TimeToListUtil.timeRecordVO2List(list);
            redisService.set(RecordTimeKey.getRecordTimeKey,studentVO.getStudentID().toString(),relMap);
            return ResultVO.success(relMap);
        }
    }

    /**
     * 获取当前登录的学生
     * @return
     */
    @Override
    public StudentVO getCurrentStudent() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String studentID = authentication.getName();
        String key = "anonymousUser";
        if (!studentID.equals(key)) {
            return findStudentByID(Long.valueOf(studentID));
        }
        return null;
    }

    /*    @Override
    public List<String> getAllStudentID() {
        return studentMapper.getAllStudentID();
    }*/

    /**
     * 超级管理员添加学生
     * @param studentForm
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVO addStudent(StudentForm studentForm) {
        Student student = new Student();
        BeanUtils.copyProperties(studentForm,student);
        // 如果已经存在该学号
        if(findStudentByID(studentForm.getStudentID()) != null ){
            return ResultVO.error(ResultEnum.USER_HAS_EXIST);
        }
        // 设置默认头像
        if (studentForm.getQq()==null || studentForm.getQq().length() == 0){
            if(student.getSex() == 1) {
                student.setAvatar(url + "/images/boyAvatar.jpg");
            } else {
                student.setAvatar(url + "/images/girlAvatar.png");
            }
        }else {
            student.setAvatar("https://tenapi.cn/qqimg?qq=" + studentForm.getQq());
            student.setPhoto("https://tenapi.cn/qqimg?qq=" + studentForm.getQq());
        }

        // 刚注册默认未开始打卡
        student.setCreateTime(new Date());
        student.setPunch(false);
        student.setPassword(passwordEncoder.encode("123456"));
        int studentId = getGrade(studentForm.getStudentID());
        BeanUtils.copyProperties(studentForm, student);
        student.setGrade(studentId);
        try{
            insert(student);
            studentRoleMapper.insert(student.getStudentID(), UserRole.STUDENT.getValue());
        }catch (Exception e) {
            return ResultVO.error(ResultEnum.SAVE_WRONG);
        }
        return ResultVO.success();
    }

    /**
     * 上传图片
     * @param file
     * @return
     * @throws IOException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVO upImage(MultipartFile file) throws IOException {

        StudentVO currentStudent = getCurrentStudent();
        Map<String, String> map = new HashMap<>(5);
        BufferedImage bi = ImageIO.read(file.getInputStream());
        if(bi == null){
            return ResultVO.error(ResultEnum.WRONG_FORMAT);
        }
        File filePath = new File(location);
        System.out.println("文件的保存路径：" + location);
        if (!filePath.exists() && !filePath.isDirectory()) {
            System.out.println("目录不存在，创建目录:" + filePath);
            filePath.mkdirs();
        }
        String originalFileName = file.getOriginalFilename();
        String type = originalFileName.substring(originalFileName.lastIndexOf("."));
//        String fileName = UUID.randomUUID().toString() + type;
        String fileName = currentStudent.getStudentID() + type;
//        String piUrl =  url+ "/image/" + fileName;
        String piUrl =  url+ location + fileName + "?x-oss-process=style/ziped-image";
        log.info("{}更新了头像，地址={}",currentStudent.getStudentID(),piUrl);

        //修改头像地址，保存到数据库中
        studentMapper.updateAvatar(piUrl, currentStudent.getStudentID());
        File targetFile = new File(location, fileName);
        InputStream inputStream = file.getInputStream();

        OssUtil.upImageToOSS(location + fileName,inputStream);

//        try {
//            Thumbnails.of(inputStream).size(200,200).toFile(targetFile);
////            file.transferTo(targetFile);
//        } catch (IOException e) {
//            e.printStackTrace();
//            return ResultVO.error(ResultEnum.SAVE_WRONG);
//        }
        map.put("url",piUrl);
        return ResultVO.success(map);
    }

    @Override
    public ResultVO setPunchTime(PunchStandardForm punchStandardForm) {
        if (punchStandardForm.getLowestStandard()<=0) {
            return ResultVO.error(ResultEnum.TIME_WRONG);
        }
        List<PunchStandard> punchStandards = punchStandardMapper.selectAllVacation(punchStandardForm.getStuGrade());
        @NotNull Date beginDate = punchStandardForm.getBeginDate();
        @NotNull Date endDate = punchStandardForm.getEndDate();
        //判断设置的时间范围是否与之前设置的时间相交
        for (PunchStandard standard:punchStandards){
            if(!endDate.before(standard.getBeginDate())&&!beginDate.after(standard.getEndDate())){
                return ResultVO.error(ResultEnum.TIME_OVERLAP,standard);
            }
        }
        //如果不相交，则插入数据
        PunchStandard punchStandard = new PunchStandard();
        BeanUtils.copyProperties(punchStandardForm,punchStandard);
        punchStandard.setIsExe(true);
        punchStandardMapper.insert(punchStandard);
        List<StudentVO> students = studentMapper.getAllByRole(null);
        students.forEach(e -> {
            redisService.delete(RecordTimeKey.getRecordTimeKey,e.getStudentID().toString());
            redisService.delete(RecordTimeKey.getPunchedTimeKey,e.getStudentID().toString());
        });

        return ResultVO.success();
    }

    @Override
    public ResultVO cancelPunchTime(Integer id) {
        PunchStandard punchStandard = punchStandardMapper.selectByPrimaryKey(id);
        if (punchStandard == null || !punchStandard.getIsExe()) {
            return ResultVO.error(ResultEnum.CANCEL_FAILED);
        }
        try {
            punchStandardMapper.cancelPunchTime(id);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.error(ResultEnum.CAN_NOT_CANCEL_BASE);
        }
        List<StudentVO> students = studentMapper.getAllByRole(null);
        students.forEach(e -> {
            redisService.delete(RecordTimeKey.getRecordTimeKey,e.getStudentID().toString());
            redisService.delete(RecordTimeKey.getPunchedTimeKey,e.getStudentID().toString());
        });
        return ResultVO.success();
    }

    @Override
    public ResultVO getPunchTimeStandard() {
        List<PunchStandard> punchStandards = punchStandardMapper.selectAll();
        return ResultVO.success(punchStandards);
    }

    @Override
    public ResultVO modifyPwd(Long studentID) {
        log.info("【学号】 ={}",studentID);
        if (studentMapper.modifyPwd(studentID,passwordEncoder.encode("123456")) == 0) {
            return ResultVO.error(ResultEnum.WRONG_UPDATE);
        }
        return ResultVO.success();
    }

    @Override
    public ResultVO addRecord(RecordForm recordForm) {

        redisService.delete(RecordTimeKey.getPunchedTimeKey,recordForm.getStudentID().toString());
        Student student = studentMapper.selectByStudentId(recordForm.getStudentID());
        if(student == null){
            return ResultVO.error(ResultEnum.NO_STUDENT);
        }
        PunchRecord punchRecord  = new PunchRecord();
        BeanUtils.copyProperties(recordForm,punchRecord);
        punchRecord.setBeginPunchTime(new Date());
        punchRecord.setEndPunchTime(new Date());
        punchRecord.setDate(new Date());
       punchRecordMapper.insert(punchRecord);
       return  ResultVO.success();
    }
}
