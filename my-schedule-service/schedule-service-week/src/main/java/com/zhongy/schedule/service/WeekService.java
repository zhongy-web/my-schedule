package com.zhongy.schedule.service;

import com.github.pagehelper.PageInfo;
import com.zhongy.schedule.pojo.Daily;
import com.zhongy.schedule.pojo.DailyDetail;
import com.zhongy.schedule.pojo.Week;

import java.sql.Date;
import java.util.List;
import java.util.Map;

public interface WeekService {

    /***
     * Daily多条件分页查询
     * @param week
     * @param page
     * @param size
     * @return
     */
    PageInfo<Week> findPage(Week week, int page, int size);

    /***
     * Week分页查询
     * @param page
     * @param size
     * @return
     */
    PageInfo<Week> findPage(int page, int size);

    /***
     * Week多条件搜索方法
     * @param week
     * @return
     */
    List<Week> findList(Week week);

    /***
     * 删除Week
     * @param id
     */
    void delete(Integer id);

    /***
     * 修改Week数据
     * @param week
     */
    void update(Week week);

    /***
     * 新增Week
     * @param week
     */
    void add(Week week);

    /**
     * 根据ID查询Week
     * @param id
     * @return
     */
    Week findById(Integer id);

    /***
     * 查询所有Week
     * @return
     */
    List<Week> findAll();

    /**
     * 根据用户名查找所属的周计划
     */
    Week findByName(String name);

    /**
     * 查询本周的所有每日任务ID集合
     */
    List<Integer> findAll2ListId(Integer id, String name);

    /**
     * 查询本周所有需要展示的数据
     * @return
     */
    List<Map<Integer,List>> findThisWeekAll(String name);

    void clear(String username);

    Integer recover(String username);

    List<DailyDetail> openMsgRemind(Integer id);

    Integer closeMsgRemind(Integer id);

    List<DailyDetail> openAutoComplete(Integer id, String username);

    Integer closeAutoComplete(Integer id, String username);
}
