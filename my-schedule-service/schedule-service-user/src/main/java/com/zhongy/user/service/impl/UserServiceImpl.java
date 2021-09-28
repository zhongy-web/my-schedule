package com.zhongy.user.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhongy.user.dao.UserMapper;
import com.zhongy.user.pojo.User;
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
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;


    /**
     * User条件+分页查询
     * @param user 查询条件
     * @param page 页码
     * @param size 页大小
     * @return 分页结果
     */
    @Override
    public PageInfo<User> findPage(User user, int page, int size){
        //分页
        PageHelper.startPage(page,size);
        //搜索条件构建
        Example example = createExample(user);
        //执行搜索
        return new PageInfo<User>(userMapper.selectByExample(example));
    }

    /**
     * User分页查询
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageInfo<User> findPage(int page, int size){
        //静态分页
        PageHelper.startPage(page,size);
        //分页查询
        return new PageInfo<User>(userMapper.selectAll());
    }

    /**
     * User条件查询
     * @param user
     * @return
     */
    @Override
    public List<User> findList(User user){
        //构建查询条件
        Example example = createExample(user);
        //根据构建的条件查询数据
        return userMapper.selectByExample(example);
    }


    /**
     * User构建查询对象
     * @param user
     * @return
     */
    public Example createExample(User user){
        Example example=new Example(User.class);
        Example.Criteria criteria = example.createCriteria();
        if(user!=null){
            // 用户名
            if(!StringUtils.isEmpty(user.getUsername())){
                    criteria.andLike("username","%"+user.getUsername()+"%");
            }
            // 密码，加密存储
            if(!StringUtils.isEmpty(user.getPassword())){
                    criteria.andEqualTo("password",user.getPassword());
            }
            // 注册邮箱
            if(!StringUtils.isEmpty(user.getEmail())){
                    criteria.andEqualTo("email",user.getEmail());
            }
            // 创建时间
            if(!StringUtils.isEmpty(user.getCreated())){
                    criteria.andEqualTo("created",user.getCreated());
            }
            // 使用状态（1正常 0非正常）
            if(!StringUtils.isEmpty(user.getRole())){
                    criteria.andEqualTo("roles",user.getRole());
            }
            // 头像地址
            if(!StringUtils.isEmpty(user.getHeadPicUrl())){
                    criteria.andEqualTo("headPicUrl",user.getHeadPicUrl());
            }
            // 性别，1男，0女
            if(!StringUtils.isEmpty(user.getSex())){
                    criteria.andEqualTo("sex",user.getSex());
            }
            // 出生年月日
            if(!StringUtils.isEmpty(user.getBirthday())){
                    criteria.andEqualTo("birthday",user.getBirthday());
            }
            // 手机号
            if(!StringUtils.isEmpty(user.getMobile())){
                    criteria.andEqualTo("mobile",user.getMobile());
            }
            // 昵称
            if(!StringUtils.isEmpty(user.getName())){
                criteria.andEqualTo("name",user.getName());
            }
        }
        return example;
    }

    /**
     * 删除
     * @param id
     */
    @Override
    public void delete(String id){
        userMapper.deleteByPrimaryKey(id);
    }

    /**
     * 修改User
     * @param user
     */
    @Override
    public void update(User user){
        userMapper.updateByPrimaryKeySelective(user);
    }

    /**
     * 增加User
     * @param user
     */
    @Override
    public void add(User user){
        userMapper.insert(user);
    }

    /**
     * 根据ID查询User
     * @param id
     * @return
     */
    @Override
    public User findById(String id){
        return  userMapper.selectByPrimaryKey(id);
    }

    /**
     * 查询User全部数据
     * @return
     */
    @Override
    public List<User> findAll() {
        return userMapper.selectAll();
    }
}
