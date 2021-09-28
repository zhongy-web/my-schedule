package com.zhongy.community.service;

import com.github.pagehelper.PageInfo;
import com.zhongy.community.pojo.Article;

import java.util.List;
import java.util.Map;

/****
 * @Author:admin
 * @Description:Article业务层接口
 * @Date 2019/6/14 0:16
 *****/
public interface ArticleService {

    /***
     * Article多条件分页查询
     * @param article
     * @param page
     * @param size
     * @return
     */
    PageInfo<Article> findPage(Article article, int page, int size);

    /***
     * Article分页查询
     * @param page
     * @param size
     * @return
     */
    PageInfo<Article> findPage(int page, int size);

    /***
     * Article多条件搜索方法
     * @param article
     * @return
     */
    List<Article> findList(Article article);

    /***
     * 删除Article
     * @param id
     */
    void delete(Integer id);

    /***
     * 修改Article数据
     * @param article
     */
    void update(Article article);

    /***
     * 新增Article
     * @param article
     */
    void add(Article article);

    /**
     * 根据ID查询Article
     * @param id
     * @return
     */
     Article findById(Integer id);

    /***
     * 查询所有Article
     * @return
     */
    List<Article> findAll();


    /**
     * 计算总人数
     * @return
     */
    Map<String, Integer> countMember();

    /**
     * 按最新时间查询
     */
    List<Article> findByNew();

    /**
     * 按评论最多查询
     */
    List<Article> findMostCom();
}
