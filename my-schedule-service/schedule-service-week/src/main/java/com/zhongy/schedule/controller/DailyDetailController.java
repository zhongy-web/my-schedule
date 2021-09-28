package com.zhongy.schedule.controller;

import com.github.pagehelper.PageInfo;
import com.zhongy.schedule.pojo.Daily;
import com.zhongy.schedule.pojo.DailyDetail;
import com.zhongy.schedule.pojo.Type;
import com.zhongy.schedule.service.DailyDetailService;
import com.zhongy.schedule.service.DailyService;
import entity.JwtUtil;
import entity.Result;
import entity.StatusCode;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dailyDetail")
public class DailyDetailController {
    @Autowired
    private DailyDetailService dailyDetailService;

    @Autowired
    private DailyService dailyService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 查找任务类别
     */
    @ApiOperation(value = "类别查询",notes = "显示表单的时候要先将这个加载上去")
    @GetMapping("/findTypes")
    public Result<List<Type>> findTypes() {
        List<Type> types = dailyDetailService.findTypes();
        return new Result<List<Type>>(true, StatusCode.OK, "查询成功", types);
    }


    /***
     * DailyDetail分页条件搜索实现
     * @param dailyDetail
     * @param page
     * @param size
     * @return
     */
    @ApiOperation(value = "User条件分页查询",notes = "分页条件查询User方法详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "page", value = "当前页", required = true, dataType = "Integer"),
            @ApiImplicitParam(paramType = "path", name = "size", value = "每页显示条数", required = true, dataType = "Integer")
    })
    @PostMapping(value = "/search/{page}/{size}" )
    public Result<PageInfo> findPage(@RequestBody(required = false) @ApiParam(name = "User对象",value = "传入JSON数据",required = false) DailyDetail dailyDetail, @PathVariable  int page, @PathVariable  int size){
        //调用UserService实现分页条件查询User
        PageInfo<DailyDetail> pageInfo = dailyDetailService.findPage(dailyDetail, page, size);
        return new Result(true,StatusCode.OK,"查询成功",pageInfo);
    }

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
        PageInfo<DailyDetail> pageInfo = dailyDetailService.findPage(page, size);
        return new Result<PageInfo>(true,StatusCode.OK,"查询成功",pageInfo);
    }

    @ApiOperation(value = "添加一个任务明细",notes = "url需要动态获取所点击的任务属于哪个时间，并将时间动态拼接到url中，" +
            "格式：(2021-04-25),需要添加的有content、hasDone '0'-未完成，'1'-完成、start_time,end_time,格式为（2021-4-24 12:22:22）")
    @PostMapping("/{str_date}")
    public Result add(@RequestBody DailyDetail dailyDetail, @PathVariable(value = "str_date") String str_date) throws Exception {
        String username = JwtUtil.getUserInfo();
        dailyDetailService.add(dailyDetail, str_date, username);
        return new Result(true, StatusCode.OK, "添加成功！");
    }

    //这个完成和撤销应该是能放在编辑里一起的。后期会进行优化 TODO
    @ApiOperation(value = "完成任务",notes = "完成任务按钮接在这个接口上,不然daily数据统计不到！")
    @PostMapping("/complete/{id}")
    public Result complete(@PathVariable Integer id) {
        DailyDetail dailyDetail = dailyDetailService.selectById(id);
        Integer daily_id = dailyDetail.getDaily_id();
        //修改完成状态和daily统计数量
        Daily daily = dailyService.findById(daily_id);
        Integer count = daily.getCount();
        count++;
        daily.setCount(count);
        dailyService.update(daily);
        dailyDetail.setHasDone("1");
        dailyDetailService.update(dailyDetail);
        return new Result(true, StatusCode.OK, "操作成功");
    }

    @ApiOperation(value = "撤销完成",notes = "撤销完成")
    @PostMapping("/cancel/{id}")
    public Result cancel(@PathVariable Integer id) {
        DailyDetail dailyDetail = dailyDetailService.selectById(id);
        Integer daily_id = dailyDetail.getDaily_id();
        //修改完成状态和daily统计数量
        Daily daily = dailyService.findById(daily_id);
        Integer count = daily.getCount();
        count--;
        daily.setCount(count);
        dailyService.update(daily);
        dailyDetail.setHasDone("0");
        dailyDetailService.update(dailyDetail);
        return new Result(true, StatusCode.OK, "操作成功");
    }

    @ApiOperation(value = "查找任务详情",notes = "拼接任务详情的id，在查询每周计划时已给出")
    @GetMapping("/{id}")
    public Result<DailyDetail> findById(@PathVariable(value = "id") Integer id) {
        DailyDetail dailyDetail = dailyDetailService.selectById(id);
        return new Result<DailyDetail>(true, StatusCode.OK, "查询成功", dailyDetail);
    }

    @ApiOperation(value = "修改任务详情",notes = "将表单用json格式传过来，DailyDetail的id是必传值，其他选传，daily_id不能改！")
    @PutMapping
    public Result update(@RequestBody DailyDetail dailyDetail) {
        dailyDetailService.update(dailyDetail);
        return new Result(true, StatusCode.OK, "修改成功!");
    }

    @ApiOperation(value = "删除任务详情",notes = "与查找类似，需要将id动态获取后拼接到url上")
    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable(value = "id") Integer id) {
        dailyDetailService.delete(id);
        return new Result(true, StatusCode.OK, "删除成功!");
    }
}
