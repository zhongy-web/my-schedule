package com.zhongy.sport.service;

import com.github.pagehelper.PageInfo;
import com.zhongy.sport.pojo.Sport;

import java.util.List;

/****
 * @Author:admin
 * @Description:Sport业务层接口
 * @Date 2019/6/14 0:16
 *****/
public interface SportService {

    /***
     * Sport多条件分页查询
     * @param sport
     * @param page
     * @param size
     * @return
     */
    PageInfo<Sport> findPage(Sport sport, int page, int size);

    /***
     * Sport分页查询
     * @param page
     * @param size
     * @return
     */
    PageInfo<Sport> findPage(int page, int size);

    /***
     * Sport多条件搜索方法
     * @param sport
     * @return
     */
    List<Sport> findList(Sport sport);

    /***
     * 删除Sport
     * @param id
     */
    void delete(Integer id);

    /***
     * 修改Sport数据
     * @param sport
     */
    void update(Sport sport);

    /***
     * 新增Sport
     * @param sport
     */
    void add(Sport sport);

    /**
     * 根据ID查询Sport
     * @param id
     * @return
     */
     Sport findById(Integer id);

    /***
     * 查询所有Sport
     * @return
     */
    List<Sport> findAll();
}
