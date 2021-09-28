package com.zhongy.schedule.util;

import entity.DateUtil;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class WeekDateUtil {

    public static List<String> getWeekDate(String pattern) throws ParseException {
        //计算本周周一和周日的日期范围
        Date date = new Date();
        String format_time = DateUtil.date2Str(date);
        Map<String, String> dateMap = DateUtil.getMonAndSun(format_time, pattern);
        String monday = dateMap.get("monday");
        String tuesday = dateMap.get("tuesday");
        String wednesday = dateMap.get("wednesday");
        String thursday = dateMap.get("thursday");
        String friday = dateMap.get("friday");
        String saturday = dateMap.get("saturday");
        String sunday = dateMap.get("sunday");
        List<String> list = Arrays.asList(monday, tuesday, wednesday, thursday, friday, saturday, sunday);
        return list;
    }
}
