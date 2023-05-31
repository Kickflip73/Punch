package com.yrmjhtdjxh.punch.util;

import com.yrmjhtdjxh.punch.VO.TimeRecordVO;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author GO FOR IT
 */
public class TimeToListUtil {

    public static Map timeRecordVO2List(List<TimeRecordVO> list){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        List<String> dateList = list.stream().map(e -> sdf.format(e.getDate())).collect(Collectors.toList());
        List<Float> totalTimeList = list.stream().map(TimeRecordVO::getTotalHourOfOneDay).collect(Collectors.toList());;
        Map<String,List> map  = new HashMap<>(3);
        map.put("dateList",dateList);
        map.put("totalTimeList",totalTimeList);
        return map;
    }
}
