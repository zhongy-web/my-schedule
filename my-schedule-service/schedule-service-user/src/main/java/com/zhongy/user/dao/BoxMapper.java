package com.zhongy.user.dao;
import com.zhongy.user.pojo.IdeasBox;
import com.zhongy.user.pojo.User;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

/****
 * @Author:admin
 * @Description:IdeasBox的Dao
 * @Date 2019/6/14 0:12
 *****/
@Repository
public interface BoxMapper extends Mapper<IdeasBox> {
}
