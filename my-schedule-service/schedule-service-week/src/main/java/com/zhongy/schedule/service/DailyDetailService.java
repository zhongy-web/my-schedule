package com.zhongy.schedule.service;

import com.github.pagehelper.PageInfo;
import com.zhongy.schedule.pojo.DailyDetail;
import com.zhongy.schedule.pojo.Type;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface DailyDetailService {

    /***
     * DailyDetail多条件分页查询
     * @param dailyDetail
     * @param page
     * @param size
     * @return
     */
    PageInfo<DailyDetail> findPage(DailyDetail dailyDetail, int page, int size);

    /***
     * DailyDetail分页查询
     * @param page
     * @param size
     * @return
     */
    PageInfo<DailyDetail> findPage(int page, int size);

    /***
     * DailyDetail多条件搜索方法
     * @param dailyDetail
     * @return
     */
    List<DailyDetail> findList(DailyDetail dailyDetail);

    /***
     * 删除DailyDetail
     * @param id
     */
    void delete(Integer id);

    /***
     * 修改DailyDetail数据
     * @param dailyDetail
     */
    void update(DailyDetail dailyDetail);

    /***
     * 新增DailyDetail
     * @param dailyDetail
     */
    void add(DailyDetail dailyDetail, String date, String name) throws Exception;

    /**
     * 根据ID查询DailyDetail
     * @param id
     * @return
     */
    DailyDetail findById(Integer id, Integer daily_id);

    DailyDetail selectById(Integer id);

    /***
     * 查询所有DailyDetail
     * @return
     */
    List<DailyDetail> findAll();

    /**
     * 查询所有类别放入redis中
     */
    List<Type> findTypes();

    /**
     * 每日的饼状图
     */
    List<Map<String,Object>> pieChart();

    /**
     * 发送消息提醒
     */
    void notifyUser(DailyDetail dailyDetail);

    /**
     * 自动完成任务
     */
    void autoFinish(DailyDetail dailyDetail);


}
