package com.zhongy.community.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhongy.community.dao.CommentMapper;
import com.zhongy.community.pojo.Comment;
import com.zhongy.community.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

/****
 * @Author:admin
 * @Description:Comment业务层接口实现类
 * @Date 2019/6/14 0:16
 *****/
@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    /**
     * 找到父评论
     *
     * @param comments
     * @return
     */
    @Override
    public List<Comment> findParent(List<Comment> comments) {
        for (Comment comment : comments) {
            ArrayList<Comment> fatherChildren = new ArrayList<>();

            //递归处理子级的回复，即回复中有回复
            findChildren(comment, fatherChildren);

            //递归处理完后的集合放回父级的孩子中
            comment.setChild(fatherChildren);
        }
        return comments;
    }

    /**
     * 找到子评论
     *
     * @param parent
     * @param fatherChildren
     */
    @Override
    public void findChildren(Comment parent, List<Comment> fatherChildren) {
        //找出直接子级
        List<Comment> comments = parent.getChild();
        for (Comment comment : comments) {
            //如果非空，还有子级，继续递归
            if (!comment.getChild().isEmpty()) {
                findChildren(comment, fatherChildren);
            }

            //已经到了最底层的嵌套关系，将该回复放入新建立的集合中
            fatherChildren.add(comment);

            // 容易忽略的地方：将相对底层的子级放入新建立的集合之后
            // 则表示解除了嵌套关系，对应的其父级的子级应该设为空
            comment.setChild(new ArrayList<>());
        }
    }

    /**
     * Comment条件+分页查询
     *
     * @param comment 查询条件
     * @param page    页码
     * @param size    页大小
     * @return 分页结果
     */
    @Override
    public PageInfo<Comment> findPage(Comment comment, int page, int size) {
        //分页
        PageHelper.startPage(page, size);
        //搜索条件构建
        Example example = createExample(comment);
        //执行搜索
        return new PageInfo<Comment>(commentMapper.selectByExample(example));
    }

    /**
     * Comment分页查询
     *
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageInfo<Comment> findPage(int page, int size) {
        //根据时间倒序查询
        String orderBy = "commentTime desc";
        //静态分页
        PageHelper.startPage(page, size, orderBy);
        //分页查询
        return new PageInfo<Comment>(commentMapper.selectAll());
    }

    /**
     * Comment条件查询
     *
     * @param comment
     * @return
     */
    @Override
    public List<Comment> findList(Comment comment) {
        //构建查询条件
        Example example = createExample(comment);
        //根据构建的条件查询数据
        return commentMapper.selectByExample(example);
    }


    /**
     * Comment构建查询对象
     *
     * @param comment
     * @return
     */
    public Example createExample(Comment comment) {
        Example example = new Example(Comment.class);
        Example.Criteria criteria = example.createCriteria();
        if (comment != null) {
            // 评论id
            if (!StringUtils.isEmpty(comment.getId())) {
                criteria.andEqualTo("id", comment.getId());
            }
            // 评论的人
            if (!StringUtils.isEmpty(comment.getName())) {
                criteria.andLike("name", "%" + comment.getName() + "%");
            }
            // 回复谁
            if (!StringUtils.isEmpty(comment.getToWho())) {
                criteria.andLike("toWho", "%" + comment.getToWho() + "%");
            }
            // 评论内容
            if (!StringUtils.isEmpty(comment.getComContent())) {
                criteria.andEqualTo("comContent", comment.getComContent());
            }
            // 关联文章表
            if (!StringUtils.isEmpty(comment.getArticleId())) {
                criteria.andEqualTo("articleId", comment.getArticleId());
            }
            // 属于哪一级评论
            if (!StringUtils.isEmpty(comment.getParentId())) {
                criteria.andEqualTo("parentId", comment.getParentId());
            }
            // 评论时间
            if (!StringUtils.isEmpty(comment.getCommentTime())) {
                criteria.andEqualTo("commentTime", comment.getCommentTime());
            }
        }
        return example;
    }

    /**
     * 删除
     *
     * @param id
     */
    @Override
    public void delete(Integer id) {
        commentMapper.deleteByPrimaryKey(id);
    }

    /**
     * 修改Comment
     *
     * @param comment
     */
    @Override
    public void update(Comment comment) {
        commentMapper.updateByPrimaryKey(comment);
    }

    /**
     * 增加Comment
     *
     * @param comment
     */
    @Override
    public void add(Comment comment) {
        commentMapper.insert(comment);
    }

    /**
     * 根据ID查询Comment
     *
     * @param id
     * @return
     */
    @Override
    public Comment findById(Integer id) {
        return commentMapper.selectByPrimaryKey(id);
    }

    /**
     * 查询Comment全部数据
     *
     * @return
     */
    @Override
    public List<Comment> findAll() {
        return commentMapper.selectAll();
    }

    /**
     * 查询这个文章下所有的评论
     *
     * @param id
     * @return
     */
    @Override
    public List<Comment> findByArticleId(Integer id) {
        List<Comment> comments = commentMapper.findByArticleId(id);
        return comments;
    }

    @Override
    public List<Comment> findByCommentId(Integer id) {
        return commentMapper.findByCommentId(id);
    }

}
