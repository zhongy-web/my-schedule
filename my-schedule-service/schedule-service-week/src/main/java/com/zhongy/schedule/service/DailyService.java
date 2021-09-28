package com.zhongy.schedule.service;

import com.github.pagehelper.PageInfo;
import com.zhongy.schedule.pojo.Daily;
import com.zhongy.schedule.pojo.DailyDetail;

import java.util.Date;
import java.util.List;

public interface DailyService {

    /***
     * Daily多条件分页查询
     * @param daily
     * @param page
     * @param size
     * @return
     */
    PageInfo<Daily> findPage(Daily daily, int page, int size);

    /***
     * Daily分页查询
     * @param page
     * @param size
     * @return
     */
    PageInfo<Daily> findPage(int page, int size);

    /***
     * Daily多条件搜索方法
     * @param daily
     * @return
     */
    List<Daily> findList(Daily daily);

    /***
     * 删除Daily
     * @param id
     */
    void delete(Integer id);

    /***
     * 修改Daily数据
     * @param daily
     */
    void update(Daily daily);

    /***
     * 新增Daily
     * @param daily
     */
    void add(Daily daily);

    /**
     * 根据ID查询Daily
     * @param id
     * @return
     */
    Daily findById(Integer id);

    /***
     * 查询所有Daily
     * @return
     */
    List<Daily> findAll();

    /**
     * 查找今天的所有计划
     */
    List<DailyDetail> findTodayAll(String name, String todayTime);

    /**
     * 根据用户名查询计划
     */
    Daily findByUsername(String username);

    /**
     * 查询今日任务对象（不是dailydetail）
     */
    Daily findToday(String name);

    /**
     * 查找今日完成任务的数量（饼状图）
     */
//    int selectCountById(Integer id);
    List<Integer> selectCount(String username, Integer id);

}
