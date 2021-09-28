package com.zhongy.community.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhongy.community.dao.ArticleMapper;
import com.zhongy.community.pojo.Article;
import com.zhongy.community.service.ArticleService;
import com.zhongy.user.feign.UserFeign;
import com.zhongy.user.pojo.User;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/****
 * @Author:admin
 * @Description:Article业务层接口实现类
 * @Date 2019/6/14 0:16
 *****/
@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private UserFeign userFeign;


    /**
     * Article条件+分页查询
     * @param article 查询条件
     * @param page 页码
     * @param size 页大小
     * @return 分页结果
     */
    @Override
    public PageInfo<Article> findPage(Article article, int page, int size){
        //分页
        PageHelper.startPage(page,size);
        //搜索条件构建
        Example example = createExample(article);
        //执行搜索
        return new PageInfo<Article>(articleMapper.selectByExample(example));
    }

    /**
     * Article分页查询
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageInfo<Article> findPage(int page, int size){
        //根据时间倒序查询
        String orderBy = "send_time desc";
        //静态分页
        PageHelper.startPage(page,size, orderBy);
        //分页查询
        return new PageInfo<Article>(articleMapper.selectAll());
    }

    /**
     * Article条件查询
     * @param article
     * @return
     */
    @Override
    public List<Article> findList(Article article){
        //构建查询条件
        Example example = createExample(article);
        //根据构建的条件查询数据
        return articleMapper.selectByExample(example);
    }


    /**
     * Article构建查询对象
     * @param article
     * @return
     */
    public Example createExample(Article article){
        Example example=new Example(Article.class);
        Example.Criteria criteria = example.createCriteria();
        if(article!=null){
            // 文章id
            if(!StringUtils.isEmpty(article.getId())){
                    criteria.andEqualTo("id",article.getId());
            }
            // 文章标题
            if(!StringUtils.isEmpty(article.getTitle())){
                    criteria.andLike("title","%"+article.getTitle()+"%");
            }
            // 文章作者
            if(!StringUtils.isEmpty(article.getName())){
                    criteria.andLike("name","%"+article.getName()+"%");
            }
            // 文章内容
            if(!StringUtils.isEmpty(article.getContent())){
                    criteria.andEqualTo("content",article.getContent());
            }
            // 访问人数
            if(!StringUtils.isEmpty(article.getWatchNum())){
                    criteria.andEqualTo("watchNum",article.getWatchNum());
            }
            // 点赞人数
            if(!StringUtils.isEmpty(article.getLikeNum())){
                    criteria.andEqualTo("likeNum",article.getLikeNum());
            }
            // 评论人数
            if(!StringUtils.isEmpty(article.getCommentNum())){
                    criteria.andEqualTo("commentNum",article.getCommentNum());
            }
            // 发送文章的时间
            if(!StringUtils.isEmpty(article.getSendTime())){
                    criteria.andEqualTo("sendTime",article.getSendTime());
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
        articleMapper.deleteByPrimaryKey(id);
    }

    /**
     * 修改Article
     * @param article
     */
    @Override
    public void update(Article article){
        articleMapper.updateByPrimaryKeySelective(article);
    }

    /**
     * 增加Article
     * @param article
     */
    @Override
    public void add(Article article){
        //调用用户微服务
        String name = article.getName();
        Result<User> result = userFeign.findById(name);
        User user = result.getData();
        String headPicUrl = user.getHeadPicUrl();

        //
        article.setHeadUrl(headPicUrl);
        articleMapper.insert(article);
    }

    /**
     * 根据ID查询Article
     * @param id
     * @return
     */
    @Override
    public Article findById(Integer id){
        return  articleMapper.selectByPrimaryKey(id);
    }

    /**
     * 查询Article全部数据
     * @return
     */
    @Override
    public List<Article> findAll() {
        return articleMapper.selectAll();
    }

    /**
     * 计算总人数
     * @return
     */
    @Override
    public Map<String, Integer> countMember() {
        List<User> list = userFeign.findAll().getData();
        Map<String,Integer> map = new HashMap<>();
        //查询总人数
        map.put("社区人数", list.size());
        //文章数
        int articleSize = articleMapper.selectAll().size();
        map.put("文章数", articleSize);
        return map;
    }

    @Override
    public List<Article> findByNew() {
        return articleMapper.findByNew();
    }

    @Override
    public List<Article> findMostCom() {
        return articleMapper.findMostCom();
    }
}
