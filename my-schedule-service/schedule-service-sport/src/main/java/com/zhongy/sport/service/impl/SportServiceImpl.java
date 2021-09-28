package com.zhongy.sport.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhongy.sport.dao.SportMapper;
import com.zhongy.sport.pojo.Sport;
import com.zhongy.sport.service.SportService;
import com.zhongy.user.feign.UserFeign;
import com.zhongy.user.pojo.User;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/****
 * @Author:admin
 * @Description:Sport业务层接口实现类
 * @Date 2019/6/14 0:16
 *****/
@Service
public class SportServiceImpl implements SportService {

    @Autowired
    private SportMapper sportMapper;


    /**
     * Sport条件+分页查询
     * @param sport 查询条件
     * @param page 页码
     * @param size 页大小
     * @return 分页结果
     */
    @Override
    public PageInfo<Sport> findPage(Sport sport, int page, int size){
        //分页
        PageHelper.startPage(page,size);
        //搜索条件构建
        Example example = createExample(sport);
        //执行搜索
        return new PageInfo<Sport>(sportMapper.selectByExample(example));
    }

    /**
     * Sport分页查询
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageInfo<Sport> findPage(int page, int size){
        //静态分页
        PageHelper.startPage(page,size);
        //分页查询
        return new PageInfo<Sport>(sportMapper.selectAll());
    }

    /**
     * Sport条件查询
     * @param sport
     * @return
     */
    @Override
    public List<Sport> findList(Sport sport){
        //构建查询条件
        Example example = createExample(sport);
        //根据构建的条件查询数据
        return sportMapper.selectByExample(example);
    }


    /**
     * Sport构建查询对象
     * @param sport
     * @return
     */
    public Example createExample(Sport sport){
        Example example=new Example(Sport.class);
        Example.Criteria criteria = example.createCriteria();
        if(sport!=null){
            // 
            if(!StringUtils.isEmpty(sport.getId())){
                    criteria.andEqualTo("id",sport.getId());
            }
            // 运动项目的名字
            if(!StringUtils.isEmpty(sport.getName())){
                    criteria.andLike("name","%"+sport.getName()+"%");
            }
            // 该运动适宜年龄
            if(!StringUtils.isEmpty(sport.getAge())){
                    criteria.andEqualTo("age",sport.getAge());
            }
            // 注意事项
            if(!StringUtils.isEmpty(sport.getAttention())){
                    criteria.andEqualTo("attention",sport.getAttention());
            }
            // 图片的url地址
            if(!StringUtils.isEmpty(sport.getImage())){
                    criteria.andEqualTo("image",sport.getImage());
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
        sportMapper.deleteByPrimaryKey(id);
    }

    /**
     * 修改Sport
     * @param sport
     */
    @Override
    public void update(Sport sport){
        sportMapper.updateByPrimaryKey(sport);
    }

    /**
     * 增加Sport
     * @param sport
     */
    @Override
    public void add(Sport sport){
        sportMapper.insert(sport);
    }

    /**
     * 根据ID查询Sport
     * @param id
     * @return
     */
    @Override
    public Sport findById(Integer id){
        return sportMapper.selectByPrimaryKey(id);
    }

    /**
     * 查询Sport全部数据
     * @return
     */
    @Override
    public List<Sport> findAll() {
        return sportMapper.selectAll();
    }
}
