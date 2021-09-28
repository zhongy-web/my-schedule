package com.zhongy.user.service;

import com.github.pagehelper.PageInfo;
import com.zhongy.user.pojo.IdeasBox;
import com.zhongy.user.pojo.User;

import java.util.List;

/****
 * @Author:admin
 * @Description:User业务层接口
 * @Date 2019/6/14 0:16
 *****/
public interface BoxService {

    /***
     * User多条件分页查询
     * @param ideasBox
     * @param page
     * @param size
     * @return
     */
    PageInfo<IdeasBox> findPage(IdeasBox ideasBox, int page, int size);

    /***
     * User分页查询
     * @param page
     * @param size
     * @return
     */
    PageInfo<IdeasBox> findPage(int page, int size);

    /***
     * IdeasBox多条件搜索方法
     * @param ideasBox
     * @return
     */
    List<IdeasBox> findList(IdeasBox ideasBox);

    /***
     * 删除IdeasBox
     * @param id
     */
    void delete(Integer id);

    /***
     * 修改IdeasBox数据
     * @param ideasBox
     */
    void update(IdeasBox ideasBox);

    /***
     * 新增IdeasBox
     * @param ideasBox
     */
    void add(IdeasBox ideasBox);

    /**
     * 根据ID查询IdeasBox
     * @param id
     * @return
     */
    IdeasBox findById(Integer id);

    /***
     * 查询所有IdeasBox
     * @return
     */
    List<IdeasBox> findAll();
}
