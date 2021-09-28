package com.zhongy.community.dao;
import com.zhongy.community.pojo.Comment;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/****
 * @Author:admin
 * @Description:Comment的Dao
 * @Date 2019/6/14 0:12
 *****/
@Repository
public interface CommentMapper extends Mapper<Comment> {

    //通过文章id找到所有评论
    @Select("select * from tb_comment where articleId = #{id}")
    List<Comment> findByArticleId(Integer id);

    //通过id找到所有子评论
    @Select("select * from tb_comment where parentId = #{id}")
    List<Comment> findByCommentId(Integer id);
}
