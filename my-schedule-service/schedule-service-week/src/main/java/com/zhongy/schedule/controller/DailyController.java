package com.zhongy.schedule.controller;

import com.zhongy.schedule.pojo.DailyDetail;
import com.zhongy.schedule.service.DailyService;
import entity.JwtUtil;
import entity.Result;
import entity.StatusCode;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/daily")
public class DailyController {
    @Autowired
    private DailyService dailyService;

    /**
     * 获得今日计划的明细
     * @return
     */
    @ApiOperation(value = "查询今日任务",notes = "查询今日任务的详情,应用于每日任务的列表，显示今日所有的任务明细")
    @GetMapping
    public Result<List<DailyDetail>> getToday() {
        String username = JwtUtil.getUserInfo();
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String format_date = sdf.format(date);
        List<DailyDetail> today_plan = dailyService.findTodayAll(username, format_date);
        return new Result<List<DailyDetail>>(true, StatusCode.OK, "查询成功", today_plan);
    }

    /**
     * 获得今日计划的明细 根据传过来的用户名查询今日任务
     * @return
     */
    @ApiOperation(value = "查询今日任务",notes = "查询今日任务的详情,应用于每日任务的列表，显示今日所有的任务明细")
    @GetMapping("/{username}")
    public Result<List<DailyDetail>> getToday(@PathVariable String username) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String format_date = sdf.format(date);
        List<DailyDetail> today_plan = dailyService.findTodayAll(username, format_date);
        return new Result<List<DailyDetail>>(true, StatusCode.OK, "查询成功", today_plan);
    }
}
