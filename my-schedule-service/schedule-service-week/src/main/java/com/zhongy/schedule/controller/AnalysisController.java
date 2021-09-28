package com.zhongy.schedule.controller;

import com.zhongy.schedule.pojo.Daily;
import com.zhongy.schedule.pojo.Week;
import com.zhongy.schedule.service.DailyDetailService;
import com.zhongy.schedule.service.DailyService;
import com.zhongy.schedule.service.WeekService;
import com.zhongy.schedule.util.WeekDateUtil;
import entity.JwtUtil;
import entity.Result;
import entity.StatusCode;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/analysis")
public class AnalysisController {

    @Autowired
    private WeekService weekService;

    @Autowired
    private DailyService dailyService;

    @Autowired
    private DailyDetailService dailyDetailService;

    /**
     * 每周柱形图分析 为防止报错设置默认值
     * @param
     * @return
     */
    @ApiOperation(value = "柱形图",notes = "显示的是本周每日完成任务的数量统计，返回的是两个数组，一一对应的，直接使用即可，无需传参")
    @GetMapping("/weekBar")
    public Result<Map<String,List>> analysisWeek() throws ParseException {
        Map<String,List> map = new HashMap<>();

        String username = JwtUtil.getUserInfo();
        Week week = weekService.findByName(username);

        if (week != null) {
            //获取日期 todo 日期获取有一点问题。
            List<String> dates = WeekDateUtil.getWeekDate("MM-dd");
            map.put("dates", dates);
            List<Integer> counts = dailyService.selectCount(username, week.getId());
            //每日统计数量
            map.put("counts", counts);
            return new Result<Map<String,List>>(true, StatusCode.OK, "分析成功", map);
        }
        //还没有建表的情况
        List<String> dates = WeekDateUtil.getWeekDate("MM-dd");
        List<Integer> counts = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            counts.add(0);
        }
        map.put("dates", dates);
        map.put("counts", counts);
        return new Result<Map<String,List>>(true, StatusCode.OK, "你还没有创建表，无法进行分析", map);
    }

    /**
     * 每日饼状图分析
     */
    @ApiOperation(value = "饼状图",notes = "统计每日任务类别的完成数量情况，已将数据处理好，直接使用即可，无需传参")
    @GetMapping("/dailyPie")
    public Result<List<Map<String,Object>>> dailyPieChart() {
        List<Map<String, Object>> maps = dailyDetailService.pieChart();
        return new Result<>(true, StatusCode.OK, "分析成功", maps);
    }
}
