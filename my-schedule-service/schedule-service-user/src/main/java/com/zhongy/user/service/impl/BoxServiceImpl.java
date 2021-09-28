package com.zhongy.user.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhongy.user.dao.BoxMapper;
import com.zhongy.user.dao.UserMapper;
import com.zhongy.user.pojo.IdeasBox;
import com.zhongy.user.pojo.User;
import com.zhongy.user.service.BoxService;
import com.zhongy.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/****
 * @Author:admin
 * @Description:User业务层接口实现类
 * @Date 2019/6/14 0:16
 *****/
@Service
public class BoxServiceImpl implements BoxService {

    @Autowired
    private BoxMapper boxMapper;


    /**
     * IdeasBox条件+分页查询
     * @param ideasBox 查询条件
     * @param page 页码
     * @param size 页大小
     * @return 分页结果
     */
    @Override
    public PageInfo<IdeasBox> findPage(IdeasBox ideasBox, int page, int size){
        //分页
        PageHelper.startPage(page,size);
        //搜索条件构建
        Example example = createExample(ideasBox);
        //执行搜索
        return new PageInfo<IdeasBox>(boxMapper.selectByExample(example));
    }

    /**
     * IdeasBox分页查询
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageInfo<IdeasBox> findPage(int page, int size){
        //静态分页
        PageHelper.startPage(page,size);
        //分页查询
        return new PageInfo<IdeasBox>(boxMapper.selectAll());
    }

    /**
     * IdeasBox条件查询
     * @param ideasBox
     * @return
     */
    @Override
    public List<IdeasBox> findList(IdeasBox ideasBox){
        //构建查询条件
        Example example = createExample(ideasBox);
        //根据构建的条件查询数据
        return boxMapper.selectByExample(example);
    }


    /**
     * IdeasBox构建查询对象
     * @param ideasBox
     * @return
     */
    public Example createExample(IdeasBox ideasBox){
        Example example=new Example(User.class);
        Example.Criteria criteria = example.createCriteria();
        if(ideasBox!=null){
            // id
            if(!StringUtils.isEmpty(ideasBox.getId())){
                    criteria.andLike("id","%"+ideasBox.getId()+"%");
            }
            // 作者名
            if(!StringUtils.isEmpty(ideasBox.getName())){
                    criteria.andEqualTo("name",ideasBox.getName());
            }
            // 意见箱内容
            if(!StringUtils.isEmpty(ideasBox.getContent())){
                    criteria.andEqualTo("content",ideasBox.getContent());
            }
            // 创建时间
            if(!StringUtils.isEmpty(ideasBox.getTime())){
                    criteria.andEqualTo("time",ideasBox.getTime());
            }
        }
        return example;
    }

    /**
     * 删除
     * @param id
     */
    @Override
    public void delete(Integer id){
        boxMapper.deleteByPrimaryKey(id);
    }

    /**
     * 修改IdeasBox
     * @param ideasBox
     */
    @Override
    public void update(IdeasBox ideasBox){
        boxMapper.updateByPrimaryKeySelective(ideasBox);
    }

    /**
     * 增加IdeasBox
     * @param ideasBox
     */
    @Override
    public void add(IdeasBox ideasBox){
        boxMapper.insert(ideasBox);
    }

    /**
     * 根据ID查询IdeasBox
     * @param id
     * @return
     */
    @Override
    public IdeasBox findById(Integer id){
        return  boxMapper.selectByPrimaryKey(id);
    }

    /**
     * 查询User全部数据
     * @return
     */
    @Override
    public List<IdeasBox> findAll() {
        return boxMapper.selectAll();
    }
}
