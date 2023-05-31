package com.yrmjhtdjxh.punch.service.Impl;

import com.yrmjhtdjxh.punch.VO.IndexVO;
import com.yrmjhtdjxh.punch.VO.PunchedTimeVO;
import com.yrmjhtdjxh.punch.VO.ResultVO;
import com.yrmjhtdjxh.punch.VO.StudentVO;
import com.yrmjhtdjxh.punch.domain.IndexStudent;
import com.yrmjhtdjxh.punch.domain.PunchRecord;
import com.yrmjhtdjxh.punch.domain.PunchStandard;
import com.yrmjhtdjxh.punch.domain.Student;
import com.yrmjhtdjxh.punch.enums.ResultEnum;
import com.yrmjhtdjxh.punch.enums.StudentGrade;
import com.yrmjhtdjxh.punch.mapper.PunchRecordMapper;
import com.yrmjhtdjxh.punch.mapper.PunchStandardMapper;
import com.yrmjhtdjxh.punch.mapper.StudentMapper;
import com.yrmjhtdjxh.punch.redis.RecordTimeKey;
import com.yrmjhtdjxh.punch.redis.RedisService;
import com.yrmjhtdjxh.punch.service.PunchRecordService;
import com.yrmjhtdjxh.punch.service.StudentAndPunchRecordService;
import com.yrmjhtdjxh.punch.service.StudentService;
import com.yrmjhtdjxh.punch.util.GetWeekUtil;
import jdk.nashorn.internal.ir.annotations.Ignore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;

/**
 * @author GO FOR IT
 */
@Service
@Slf4j
public class StudentAndPunchRecordServiceImpl implements StudentAndPunchRecordService {

    @Autowired
    private StudentService studentService;
    private StudentMapper studentMapper;
    private PunchRecordMapper punchRecordMapper;
    private PunchRecordService punchRecordService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private PunchStandardMapper punchStandardMapper;

    private static final double LOWEST_ONE = 28;
    private static final double LOWEST_TWO = 35;

    @Autowired
    public StudentAndPunchRecordServiceImpl(StudentMapper studentMapper, PunchRecordMapper punchRecordMapper,
                                            PunchRecordService punchRecordService) {
        this.studentMapper = studentMapper;
        this.punchRecordMapper = punchRecordMapper;
        this.punchRecordService = punchRecordService;
    }

    @Override
    public ResultVO getStudentAndPunchInfo() {
        IndexVO indexVO = new IndexVO();
        StudentVO student = studentService.getCurrentStudent();
        if(student == null) {
            return ResultVO.error(ResultEnum.NOT_EXIST_USER);
        }
        List<PunchStandard> punchStandards = punchStandardMapper.selectAllVacation(to2Grade(student.getGrade()));
        //判断当前时间是否在假期中
        boolean isVacation = !punchStandards.isEmpty();

        indexVO.setIsVacation(isVacation);

        //设置最低标准
        setStandardTime(student);
        indexVO.setStudent(student);

        // 如果有未完成的打卡记录放入
        PunchRecord punchRecord = punchRecordService.getUnfinishedPunchByStudentID(student.getStudentID());

        //设置开始打卡时间
        indexVO.setUnfinishedTime(punchRecord == null ? new Date():punchRecord.getBeginPunchTime());

        //自己的信息
        IndexStudent stuListInfo = getStuListInfo(student);

        indexVO.setPunchedTime(stuListInfo.getPunchedTime());
        indexVO.setLeftTime(stuListInfo.getLeftTime());
        indexVO.setTodayTime(stuListInfo.getTodayTime());
        // 取出首页包装好的时间排名类
        indexVO.setIndexStudents(getSort());
        return ResultVO.success(indexVO);
    }

    /**
     * 设置最低标准
     * @param student
     */
    public void setStandardTime(StudentVO student){
        List<PunchStandard> punchStandards = punchStandardMapper.selectAllVacation(to2Grade(student.getGrade()));
        //判断当前时间是否在假期中
        boolean isVacation = !punchStandards.isEmpty();
        //获取最低打卡标准
        PunchStandard punchStandardOne = new PunchStandard();
        PunchStandard punchStandardTwo = new PunchStandard();
        if (isVacation){
            for (PunchStandard punchStandard : punchStandards) {
                if (punchStandard.getStuGrade().equals(StudentGrade.FRESHMAN.getValue())){
                    punchStandardOne = punchStandard;
                }else if (punchStandard.getStuGrade().equals(StudentGrade.SOPH_AND_ABOVE.getValue())){
                    punchStandardTwo = punchStandard;
                }else{
                    punchStandardOne = punchStandard;
                    punchStandardTwo = punchStandard;
                }
            }
        }else{
            setWeekTime(punchStandardOne);
            punchStandardOne.setLowestStandard(LOWEST_ONE);
            setWeekTime(punchStandardTwo);
            punchStandardTwo.setLowestStandard(LOWEST_TWO);
        }
        if (to2Grade(student.getGrade()) == StudentGrade.FRESHMAN.getValue()){
            if (punchStandardOne.getLowestStandard() == null){
                setWeekTime(punchStandardOne);
                punchStandardOne.setLowestStandard(LOWEST_ONE);
            }
            student.setPunchStandard(punchStandardOne);
        }else{
            if (punchStandardTwo.getLowestStandard() == null){
                setWeekTime(punchStandardTwo);
                punchStandardTwo.setLowestStandard(LOWEST_TWO);
            }
            student.setPunchStandard(punchStandardTwo);
        }
    }

    public void setWeekTime(PunchStandard punchStandard){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String thisMonday = GetWeekUtil.getThisMonday();
        String nextMonday = GetWeekUtil.getThisWeekSunday();
        try {
            punchStandard.setBeginDate(sdf.parse(thisMonday));
            punchStandard.setEndDate(sdf.parse(nextMonday));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将学生List全部取出来
     * 然后根据学生学号将个人的时间信息取出来
     * 然后包装成IndexStudent的类，按照每周打卡信息排名
     * 进行排好序
     * 返回即可
     * @return
     */
    public List<IndexStudent> getSort(){

        List<IndexStudent> list = new ArrayList<>();

        // 1.先将所有的student信息查出来放到一个List中
        List<StudentVO> students = studentMapper.getAllByRole(null);

        // 2.循环这个List去根据IndexStudent的结构计算 每个student的punchRecordMapper信息
        for(StudentVO student : students) {
            IndexStudent relIndexStudent = redisService.get(RecordTimeKey.getPunchedTimeKey, student.getStudentID().toString(), IndexStudent.class);
            if (relIndexStudent == null){
                setStandardTime(student);
                IndexStudent indexStudent = getStuListInfo(student);
                indexStudent.setStandard(student.getPunchStandard().getLowestStandard().intValue());
                list.add(indexStudent);
                redisService.set(RecordTimeKey.getPunchedTimeKey,student.getStudentID().toString(),indexStudent);
            }else {
                list.add(relIndexStudent);
            }
        }
        // 然后将这个List根据本周或假期打卡时间排序
        Collections.sort(list, new SumComparator());
        return list;
    }

    public IndexStudent getStuListInfo(StudentVO student){
        IndexStudent indexStudent = new IndexStudent();

        BeanUtils.copyProperties(student,indexStudent);
        PunchStandard punchStandard = student.getPunchStandard();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        // 设置今天打卡时间
        indexStudent.setTodayTime(to2Double(punchRecordMapper.getTodayByStudentID(student.getStudentID())));

        // 设置已经打卡的时间
        indexStudent.setPunchedTime(to2Double(punchRecordMapper.getWeekByStudentID(sdf.format(punchStandard.getBeginDate()),
                sdf.format(punchStandard.getEndDate()),
                student.getStudentID())));

        //设置剩余时间
        Double leftTIme = (punchStandard.getLowestStandard() - indexStudent.getPunchedTime() <= 0) ? 0:to2Double(punchStandard.getLowestStandard() - indexStudent.getPunchedTime());
        indexStudent.setLeftTime(leftTIme);

        return indexStudent;
    }

    /**
     * 使得List 集合根据周打卡时间排序
     */
    private class SumComparator implements Comparator<IndexStudent> {
        @Override
        public int compare(IndexStudent o1, IndexStudent o2) {
            //判断是否为null
            if(o1 == null && o2 == null) {
                return 0;
            }
            if(o1 == null) {
                return 1;
            }
            if(o2 == null) {
                return -1;
            }

            Double result = o1.getPunchedTime() - o2.getPunchedTime();
            //判断大小，涵盖-1、1、0三种返回值情况
            if (result > 0) {
                return -1;
            } else if (result < 0) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    /**
     * 保留两位小数
     * @param aDouble
     * @return
     */
    private Double to2Double(Double aDouble) {
        if(aDouble == null) {
            return new Double(0);
        }
        BigDecimal bigDecimal = new BigDecimal(aDouble);
        aDouble = bigDecimal.setScale(2, BigDecimal.ROUND_UP).doubleValue();
        return aDouble;
    }

    /**
     * 将年级（如2019）转化为大一大二等
     * @param grade
     * @return
     * @throws ParseException
     */
    public static int to2Grade(int grade){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            //与入学9月1号相比
            date = sdf.parse(grade + "-09-01");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date currentDate = new Date();
        int g = Integer.valueOf(Period.between(LocalDate.parse(sdf.format(date)), LocalDate.parse(sdf.format(currentDate))).getYears());
        return g > 0 ? 1 : 0;
    }

    @Override
    public ResultVO getPunchedTimeAll() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String today = sdf.format(new Date());
        String thisMonday = GetWeekUtil.getThisMonday();
        Date date = null;
        Date endDate = null;
        try{
            date = sdf.parse(today);
            endDate = sdf.parse(thisMonday);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date lastWeekMonday = GetWeekUtil.getLastWeekMonday(date);
        List<PunchedTimeVO> punchedTimeAll = punchRecordMapper.getPunchedTimeAll(lastWeekMonday, endDate);
        punchedTimeAll.forEach( e -> {
            int standard = 0;
            if (to2Grade(e.getGrade()) == StudentGrade.FRESHMAN.getValue()){
                standard = (int) LOWEST_ONE;
            }else if (to2Grade(e.getGrade()) == StudentGrade.SOPH_AND_ABOVE.getValue()){
                standard = (int) LOWEST_TWO;
            }
            e.setIsFinished(e.getTotalTime() >= standard  ? true:false);
        });
        return ResultVO.success(punchedTimeAll);
    }
}
