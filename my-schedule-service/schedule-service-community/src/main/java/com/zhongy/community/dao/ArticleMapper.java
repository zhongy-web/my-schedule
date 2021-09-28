package com.zhongy.community.dao;
import com.zhongy.community.pojo.Article;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/****
 * @Author:admin
 * @Description:Article的Dao
 * @Date 2019/6/14 0:12
 *****/
@Repository
public interface ArticleMapper extends Mapper<Article> {
    @Select("select * from tb_article ORDER BY send_time desc")
    List<Article> findByNew();

    @Select("select * from tb_article ORDER BY comment_num desc")
    List<Article> findMostCom();
}
