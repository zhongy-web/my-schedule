package com.zhongy.schedule.controller;

import com.github.pagehelper.PageInfo;
import com.zhongy.schedule.pojo.DailyDetail;
import com.zhongy.schedule.pojo.Week;
import com.zhongy.schedule.service.ProduceService;
import com.zhongy.schedule.service.WeekService;
import com.zhongy.schedule.util.WeekDateUtil;
import entity.*;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/week")
public class WeekController {

    @Autowired
    private WeekService weekService;

    @Autowired
    private ProduceService produceService;

    /***
     * DailyDetail分页搜索实现
     * @param page:当前页
     * @param size:每页显示多少条
     * @return
     */
    @ApiOperation(value = "User分页查询",notes = "分页查询User方法详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "page", value = "当前页", required = true, dataType = "Integer"),
            @ApiImplicitParam(paramType = "path", name = "size", value = "每页显示条数", required = true, dataType = "Integer")
    })
    @GetMapping(value = "/search/{page}/{size}" )
    public Result<PageInfo> findPage(@PathVariable int page, @PathVariable int size){
        //调用UserService实现分页查询User
        PageInfo<Week> pageInfo = weekService.findPage(page, size);
        return new Result<PageInfo>(true,StatusCode.OK,"查询成功",pageInfo);
    }

    /**
     * 修改周计划表
     */
    @PostMapping("/update")
    public Result update(@RequestBody Week week) {
        Week weekByName = weekService.findByName(TokenDecode.getUserInfo().get("username"));
        if (weekByName != null) {
            weekService.update(week);
            return new Result(true, StatusCode.OK, "修改成功!");
        }
        return null;
    }

    /**
     * 添加周计划表
     */
    @ApiOperation(value = "添加周计划表",notes = "添加周计划表，每日计划表也会一并添加")
    @ApiImplicitParam(paramType = "path", name = "id", value = "建议前端使用id生成器生成，动态拼接到url上", required = true, dataType = "String")
    @PostMapping("/{id}")
    public Result add(@PathVariable(value = "id") Integer id) throws ParseException {
        //获取用户名
        String username = JwtUtil.getUserInfo();
        Week weekServiceById = weekService.findByName(username);
        if (weekServiceById != null) {
            return new Result(false, StatusCode.ERROR, "您已创建本周计划表，请勿重复创建");
        } else {
            Week week = new Week();
            week.setId(id);
            week.setName(username);
            weekService.add(week);
            return new Result(true, StatusCode.OK, "创建成功");
        }
    }

    /**
     * 查询当前日期所属周的所有日期（已修改）
     * @return
     */
    @ApiOperation(value = "查询本周的所有日期",notes = "已计算出周一->周日的日期，格式可自定义，例如yyyy-MM-dd，大小写有区别！")
    @GetMapping
    public Result<Map<String,List<String>>> findThisWeek(@RequestParam String pattern) throws ParseException {
        Map<String,List<String>> map = new HashMap<>();
        //直接将繁杂代码提取即可
        List<String> dates = WeekDateUtil.getWeekDate(pattern);
        map.put("dates", dates);
        return new Result<Map<String,List<String>>>(true, StatusCode.OK, "查询成功！", map);
    }

    /**
     * 查询周计划表的全部数据
     */
    @ApiOperation(value = "查询周计划表的全部数据",notes = "查询周计划表的全部数据，包括每日任务及其详细")
    @GetMapping("/findAll")
    public Result<List<Map<Integer, List>>> findAll() {
        String name = JwtUtil.getUserInfo();
        List<Map<Integer, List>> thisWeekAll = weekService.findThisWeekAll(name);
        return new Result<List<Map<Integer, List>>>(true, StatusCode.OK, "查询成功", thisWeekAll);
    }

    @ApiOperation(value = "删除周计划",notes = "与查找类似，需要将id动态获取后拼接到url上")
    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable(value = "id") Integer id) {
        weekService.delete(id);
        return new Result(true, StatusCode.OK, "删除成功!");
    }

    /**
     * 一键清除功能
     */
    @ApiOperation(value = "一键清除功能",notes = "清除本周所有任务")
    @DeleteMapping("/clear")
    public Result clear() {
        String username = JwtUtil.getUserInfo();
        weekService.clear(username);
        return new Result(true, StatusCode.OK, "删除成功!");
    }

    /**
     * 一键恢复上周计划功能
     */
    @ApiOperation(value = "一键恢复上周计划功能",notes = "能够把上周的计划复原到这周计划表中（考虑数据库性能问题，只允许恢复一次）")
    @PostMapping("/recover")
    public Result recover() {
        String username = JwtUtil.getUserInfo();
        int isSuccess = weekService.recover(username);
        if (isSuccess == 1) {
            return new Result(true, StatusCode.OK, "还原成功!");
        } else {
            return new Result(false, StatusCode.OK, "还原失败,未查询到您上周记录");
        }
    }

    /**
     * 根据用户名查询对应的周id
     */
    @GetMapping("/getWeek")
    public Result<Integer> getWeek() {
        String username = JwtUtil.getUserInfo();
        Week week = weekService.findByName(username);
        if (week == null) {
            return new Result<>(false, StatusCode.ERROR, "查询失败");
        }
        return new Result<>(true, StatusCode.OK, "查询成功", week);
    }

    /**
     * 开启消息提醒功能
     */
    @ApiOperation(value = "开启消息提醒功能",notes = "开启消息提醒功能")
    @PutMapping("/openMsgStatus/{week_id}")
    public Result<Integer> openMsgStatus(@PathVariable Integer week_id) {
        List<DailyDetail> dailyDetails = weekService.openMsgRemind(week_id);
        for (DailyDetail dailyDetail : dailyDetails) { //todo 应该可以优化 《批量更新》没有用上！！
            produceService.saveOrUpdatePlan(dailyDetail);
        }
        Week week = weekService.findById(week_id);
        if (week.getMsg_remind() == 1) {
            return new Result<>(true, StatusCode.OK, "消息提醒功能已开启", 1);
        } else {
            return new Result<>(true, StatusCode.OK, "开启失败,这周开启次数已使用完", 0);
        }
    }

    /**
     * 关闭消息提醒功能
     */
    @ApiOperation(value = "关闭消息提醒功能",notes = "关闭消息提醒功能")
    @PutMapping("/closeMsgRemind/{week_id}")
    public Result<Integer> closeMsgRemind(@PathVariable Integer week_id) {
        Integer flag = weekService.closeMsgRemind(week_id);
        return new Result<>(true, StatusCode.OK, "已关闭", flag);
    }

    /**
     * 开启自动完成功能
     */
    @ApiOperation(value = "开启自动完成功能",notes = "开启自动完成功能")
    @PutMapping("/openAutoStatus/{week_id}/{username}")
    public Result<Integer> openAutoStatus(@PathVariable(value = "week_id") Integer week_id,
                                          @PathVariable(value = "username") String username) {
        List<DailyDetail> dailyDetails = weekService.openAutoComplete(week_id, username);
        for (DailyDetail dailyDetail : dailyDetails) {
//            produceService.saveOrUpdatePlan(dailyDetail);
            //这里要换交换机和队列
        }
        Week week = weekService.findById(week_id);
        if (week.getAuto_complete() == 1) {
            return new Result<>(true, StatusCode.OK, "自动完成功能已开启", 1);
        } else {
            return new Result<>(true, StatusCode.OK, "开启失败,这周开启次数已使用完", 0);
        }
    }

    /**
     * 关闭自动完成功能
     */
    @ApiOperation(value = "关闭自动完成功能",notes = "关闭自动完成功能")
    @PutMapping("/closeAutoRemind/{week_id}/{username}")
    public Result<Integer> closeAutoRemind(@PathVariable(value = "week_id") Integer week_id,
                                           @PathVariable(value = "username") String username) {
        Integer flag = weekService.closeAutoComplete(week_id, username);
        return new Result<>(true, StatusCode.OK, "已关闭", flag);
    }
}
