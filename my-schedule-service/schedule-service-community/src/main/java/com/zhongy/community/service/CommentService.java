package com.zhongy.community.service;

import com.github.pagehelper.PageInfo;
import com.zhongy.community.pojo.Comment;

import java.util.List;

/****
 * @Author:admin
 * @Description:Comment业务层接口
 * @Date 2019/6/14 0:16
 *****/
public interface CommentService {
    /**
     * 查询父评论
     */
    List<Comment> findParent(List<Comment> comments);

    /**
     * 查询子评论
     */
    public void findChildren(Comment parent, List<Comment> fatherChildren);

    /***
     * Comment多条件分页查询
     * @param comment
     * @param page
     * @param size
     * @return
     */
    PageInfo<Comment> findPage(Comment comment, int page, int size);

    /***
     * Comment分页查询
     * @param page
     * @param size
     * @return
     */
    PageInfo<Comment> findPage(int page, int size);

    /***
     * Comment多条件搜索方法
     * @param comment
     * @return
     */
    List<Comment> findList(Comment comment);

    /***
     * 删除Comment
     * @param id
     */
    void delete(Integer id);

    /***
     * 修改Comment数据
     * @param comment
     */
    void update(Comment comment);

    /***
     * 新增Comment
     * @param comment
     */
    void add(Comment comment);

    /**
     * 根据ID查询Comment
     * @param id
     * @return
     */
     Comment findById(Integer id);

    /***
     * 查询所有Comment
     * @return
     */
    List<Comment> findAll();

    /**
     * 查找这篇文章下所有的评论
     */
    List<Comment> findByArticleId(Integer id);

    /**
     * 找到这条评论的所有回复
     */
    List<Comment> findByCommentId(Integer id);
}
