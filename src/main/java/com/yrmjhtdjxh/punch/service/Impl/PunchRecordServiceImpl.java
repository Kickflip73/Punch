package com.yrmjhtdjxh.punch.service.Impl;

import com.yrmjhtdjxh.punch.VO.ResultVO;
import com.yrmjhtdjxh.punch.VO.StudentVO;
import com.yrmjhtdjxh.punch.domain.PunchRecord;
import com.yrmjhtdjxh.punch.enums.PunchStatus;
import com.yrmjhtdjxh.punch.enums.ResultEnum;
import com.yrmjhtdjxh.punch.mapper.PunchRecordMapper;
import com.yrmjhtdjxh.punch.mapper.StudentMapper;
import com.yrmjhtdjxh.punch.redis.RecordTimeKey;
import com.yrmjhtdjxh.punch.redis.RedisService;
import com.yrmjhtdjxh.punch.service.PunchRecordService;
import com.yrmjhtdjxh.punch.service.StudentService;
import com.yrmjhtdjxh.punch.util.GetIPAddressUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
public class PunchRecordServiceImpl implements PunchRecordService {

    /**
     * 打卡IP地址集合
     */
    static List<String> IPList = new ArrayList<>();

    static {
        IPList.add("223.85.245");
        IPList.add("117.139.221.55");
        IPList.add("117.139.220.160");
    }

    /**
     * 少于0.5小时，打卡失败
     */
    private final static double MIN_RECORD_TIME = 0.5;

    /**
     * 每次打卡最多6小时
     */
    private final static double MAX_RECORD_TIME = 6.0;


    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private StudentService studentService;

    @Autowired
    private PunchRecordMapper punchRecordMapper;

    @Autowired
    private StudentMapper studentMapper;
    @Autowired
    private RedisService redisService;

    @Override
    public void insert(PunchRecord punchRecord) {
        punchRecordMapper.insert(punchRecord);
    }

    /**
     * 删除错误的记录
     * @param studentID
     * @return
     */
    @Override
    public PunchRecord getUnfinishedPunchByStudentID(Long studentID) {
        List<PunchRecord> unfinishedPunchByStudentID = punchRecordMapper.getUnfinishedPunchByStudentID(studentID);
        for (int i = 1; i < unfinishedPunchByStudentID.size(); i++) {
            punchRecordMapper.deleteById(unfinishedPunchByStudentID.get(i).getId());
        }
        return unfinishedPunchByStudentID.size() == 0 ? null : unfinishedPunchByStudentID.get(0);
    }

    @Override
    public void endRecordTime(PunchRecord punchRecord) {
        punchRecordMapper.endPunchRecord(punchRecord);
    }

    @Override
    public void deleteShortPunchTime(Long studentID) {
        punchRecordMapper.deleteByStudentID(studentID);
    }

    /**
     * 给所有人签退
     * @return
     */
    @Override
    public ResultVO signOffAll() {
        //返回正在打卡的学生的打卡记录
        List<PunchRecord> allChartingStu = punchRecordMapper.getAllCharting();
        //给正在打卡的学生签退
        allChartingStu.forEach(this::endPunchWithoutIPCheck);
        return ResultVO.success();
    }

    @Override
    public ResultVO signOffOne(Long studentId) {
        PunchRecord punchRecord = getUnfinishedPunchByStudentID(studentId);
        if( punchRecord == null ) {
            return ResultVO.error(ResultEnum.NOT_PUNCHING);
        }
        endPunchWithoutIPCheck(punchRecord);
        return ResultVO.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVO startPunch( HttpServletRequest request) {
        // 首先判断有没有登录
        StudentVO student = studentService.getCurrentStudent();
        PunchRecord unfinishedPunchByStudentID = getUnfinishedPunchByStudentID(student.getStudentID());

        if(student.isPunch() || unfinishedPunchByStudentID != null) {
            changeStudentStatus(student.getStudentID(), PunchStatus.PUNCHING);
            return ResultVO.error(ResultEnum.HAVE_PUNCHED);
        }

        String  local = GetIPAddressUtils.getIpAddress(request);
        logger.info("当前的IP地址为：{} 是否等于{}",local,IPList);

        // 如果IP地址不为CINS1007路由器
        if (!checkIP(local)){
            return ResultVO.error(ResultEnum.WRONG_IP);
        }

        // 否则识别为正常的打卡状态
        changeStudentStatus(student.getStudentID(), PunchStatus.PUNCHING);

        // 插入一条打卡信息的数据
        PunchRecord punchRecord = new PunchRecord(0, student.getStudentID(), new Date(), new Date(), (double) 0, new Date());
        insert(punchRecord);
        // 返回即可
        return ResultVO.success();
    }

    void changeStudentStatus(Long studentID, PunchStatus status){
        studentMapper.updatePunchByStudentID(studentID, status.getValue());
        redisService.delete(RecordTimeKey.getRecordTimeKey,studentID.toString());
        redisService.delete(RecordTimeKey.getPunchedTimeKey,studentID.toString());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVO endPunch( HttpServletRequest request) {
        StudentVO student = studentService.getCurrentStudent();
        @NotNull Long studentID = student.getStudentID();

        // 首先将得到数据库中这条打卡的数据
        PunchRecord punchRecord = getUnfinishedPunchByStudentID(student.getStudentID());

        // 如果该学生没有登录或者没有开始打卡
        if( punchRecord == null) {
            studentMapper.updatePunchByStudentID(studentID, PunchStatus.NOT_PUNCHING.getValue());
            return ResultVO.error(ResultEnum.NOT_PUNCHING);
        }

        // 判断IP是否正确
        String  local = GetIPAddressUtils.getIpAddress(request);
        logger.info("当前的IP地址为：{} 是否等于{}",local,IPList);

        // 如果IP地址不为LC2路由器
        if (!checkIP(local)){
            return ResultVO.error(ResultEnum.WRONG_IP);
        }

        // 将本次时间计算出来
        punchRecord.setEndPunchTime(new Date());
        Double d = calculateRecordTime(punchRecord.getBeginPunchTime(), punchRecord.getEndPunchTime());

        //停止打卡
        student.setPunch(false);


        if(d < MIN_RECORD_TIME || !isTheSameDay(punchRecord.getBeginPunchTime(), punchRecord.getEndPunchTime())) {
            // 将student的状态改变
            changeStudentStatus(studentID, PunchStatus.NOT_PUNCHING);
            deleteShortPunchTime(studentID);
            return ResultVO.error(ResultEnum.TOO_LOW_TIME);
        }

        // 如果超过了6个小时，只记录为6个小时
        if(d > MAX_RECORD_TIME) {
            d = MAX_RECORD_TIME;
        }

        // 否则说明打卡成功，存入数据库
        punchRecord.setRecordTime(d);
        endRecordTime(punchRecord);

        // 将student的状态改变
        changeStudentStatus(studentID, PunchStatus.NOT_PUNCHING);
        // 返回即可
        return ResultVO.success();
    }

    /**
     * 计算这次打卡时间
     * 并且保留两位小数
     * @param startTime
     * @param endTime
     * @return
     */
    public Double calculateRecordTime(Date startTime, Date endTime) {
        Double recordTime =(endTime.getTime()-startTime.getTime())/(60*60*1000.0);
        BigDecimal bigDecimal = new BigDecimal(recordTime);
        recordTime = bigDecimal.setScale(2, BigDecimal.ROUND_UP).doubleValue();
        return recordTime;
    }

    /**
     * 是否是隔天数据
     * @return
     */
    public boolean isTheSameDay(Date startTime, Date endTime){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String start = simpleDateFormat.format(startTime);
        String end = simpleDateFormat.format(endTime);
        return start.equals(end);
    }

    /**
     *
     * @param punchRecord
     */
    public void endPunchWithoutIPCheck(PunchRecord punchRecord){
        Long studentID = punchRecord.getStudentID();

        redisService.delete(RecordTimeKey.getRecordTimeKey,studentID.toString());
        redisService.delete(RecordTimeKey.getPunchedTimeKey,studentID.toString());
        //设置当前时间为结束时间
        punchRecord.setEndPunchTime(new Date());
        //计算此次打卡的总时间
        Double recordTime = calculateRecordTime(punchRecord.getBeginPunchTime(), punchRecord.getEndPunchTime());

        if(recordTime < MIN_RECORD_TIME || !isTheSameDay(punchRecord.getBeginPunchTime(), punchRecord.getEndPunchTime())) {
            // 将student的状态改变
            studentMapper.updatePunchByStudentID(studentID, 0);
            deleteShortPunchTime(studentID);
        }

        // 如果超过了6个小时，只记录为6个小时
        if(recordTime > MAX_RECORD_TIME) {
            recordTime = MAX_RECORD_TIME;
        }
        //设置打卡时间
        punchRecord.setRecordTime(recordTime);

        //保存打卡记录
        endRecordTime(punchRecord);
        log.info(" {} 下卡成功，共计 {} 小时",punchRecord.getStudentID(),recordTime);
        studentMapper.updatePunchByStudentID(punchRecord.getStudentID(),0);
    }

    @Override
    public boolean checkIP(String stuIP) {
        if (IPList.size() == 0){
            return true;
        }
        boolean status = false;
        for (String IP : IPList){
            if (stuIP.contains(IP)){
                return true;
            }
        }
        return status;
    }

    @Override
    public Double getVacationByStudentID(Date beginDate,Date endDate,Long studentID) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return punchRecordMapper.getWeekByStudentID(sdf.format(beginDate), sdf.format(endDate),studentID);
    }
}
