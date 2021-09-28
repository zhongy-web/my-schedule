package com.zhongy.community.controller;

import com.github.pagehelper.PageInfo;
import com.zhongy.community.annotation.TokenAuth;
import com.zhongy.community.pojo.Article;
import com.zhongy.community.pojo.Comment;
import com.zhongy.community.service.ArticleService;
import com.zhongy.community.service.CommentService;
import entity.JwtUtil;
import entity.Result;
import entity.StatusCode;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private RedisTemplate redisTemplate;

    /***
     * Article分页条件搜索实现
     * @param article
     * @param page
     * @param size
     * @return
     */
    @ApiOperation(value = "Article条件分页查询",notes = "分页条件查询Article方法详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "page", value = "当前页", required = true, dataType = "Integer"),
            @ApiImplicitParam(paramType = "path", name = "size", value = "每页显示条数", required = true, dataType = "Integer")
    })
    @PostMapping(value = "/search/{page}/{size}" )
    public Result<PageInfo> findPage(@RequestBody(required = false) @ApiParam(name = "Article对象",value = "传入JSON数据",required = false) Article article, @PathVariable  int page, @PathVariable  int size){
        //调用ArticleService实现分页条件查询Article
        PageInfo<Article> pageInfo = articleService.findPage(article, page, size);
        return new Result(true,StatusCode.OK,"查询成功",pageInfo);
    }

    /***
     * Article分页搜索实现
     * @param page:当前页
     * @param size:每页显示多少条
     * @return
     */
    @ApiOperation(value = "Article分页查询",notes = "分页查询Article方法详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "page", value = "当前页", required = true, dataType = "Integer"),
            @ApiImplicitParam(paramType = "path", name = "size", value = "每页显示条数", required = true, dataType = "Integer")
    })
    @GetMapping(value = "/search/{page}/{size}" )
    public Result<PageInfo> findPage(@PathVariable  int page, @PathVariable  int size){
        //调用ArticleService实现分页查询Article
        PageInfo<Article> pageInfo = articleService.findPage(page, size);
        return new Result<PageInfo>(true,StatusCode.OK,"查询成功",pageInfo);
    }

    /***
     * 多条件搜索品牌数据
     * @param article
     * @return
     */
    @ApiOperation(value = "Article条件查询",notes = "条件查询Article方法详情")
    @PostMapping(value = "/search" )
    public Result<List<Article>> findList(@RequestBody(required = false) @ApiParam(name = "Article对象",value = "传入JSON数据",required = false) Article article){
        //调用ArticleService实现条件查询Article
        List<Article> list = articleService.findList(article);
        return new Result<List<Article>>(true,StatusCode.OK,"查询成功",list);
    }

    /***
     * 根据ID删除品牌数据
     * @param id
     * @return
     */
    @ApiOperation(value = "Article根据ID删除",notes = "根据ID删除Article方法详情")
    @ApiImplicitParam(paramType = "path", name = "id", value = "主键ID", required = true, dataType = "Integer")
    @DeleteMapping(value = "/{id}" )
    public Result delete(@PathVariable Integer id){
        //调用ArticleService实现根据主键删除
        articleService.delete(id);
        //删除跟这篇文章有关的所有数据
        List<Comment> comments = commentService.findByArticleId(id);
        for (Comment comment : comments) {
            commentService.delete(comment.getId());
        }
        return new Result(true,StatusCode.OK,"删除成功");
    }

    /***
     * 修改Article数据
     * @param article
     * @param id
     * @return
     */
    @ApiOperation(value = "Article根据ID修改",notes = "根据ID修改Article方法详情")
    @ApiImplicitParam(paramType = "path", name = "id", value = "主键ID", required = true, dataType = "Integer")
    @PutMapping(value="/{id}")
    public Result update(@RequestBody @ApiParam(name = "Article对象",value = "传入JSON数据",required = false) Article article,@PathVariable Integer id){
        //设置主键值
        article.setId(id);
        //调用ArticleService实现修改Article
        articleService.update(article);
        return new Result(true,StatusCode.OK,"修改成功");
    }

    /***
     * 新增Article数据
     * @param article
     * @return
     */
    @ApiOperation(value = "Article添加",notes = "添加Article方法详情")
    @PostMapping
    public Result<Article> add(@RequestBody  @ApiParam(name = "Article对象",value = "传入JSON数据",required = true) Article article){
        Date date = new Date();
        article.setSendTime(date);
        String username = JwtUtil.getUserInfo();
        article.setName(username);
        articleService.add(article);
        return new Result<Article>(true,StatusCode.OK,"添加成功");
    }

    /***
     * 根据ID查询Article数据
     * @param id
     * @return
     */
    /*@ApiOperation(value = "Article根据ID查询",notes = "根据ID查询Article方法详情")
    @ApiImplicitParam(paramType = "path", name = "id", value = "主键ID", required = true, dataType = "Integer")
    @GetMapping("/{id}")
    public Result<Article> findById(@PathVariable Integer id){
        //调用ArticleService实现根据主键查询Article
        Article article = articleService.findById(id);
        return new Result<Article>(true,StatusCode.OK,"查询成功",article);
    }*/

    /***
     * 查询Article全部数据
     * @return
     */
    @ApiOperation(value = "查询所有Article",notes = "查询所Article有方法详情")
    @GetMapping
    public Result<List<Article>> findAll(){
        //调用ArticleService实现查询所有Article
        List<Article> list = articleService.findAll();
        return new Result<List<Article>>(true, StatusCode.OK,"查询成功",list) ;
    }

    /**
     * 给文章点赞
     */
    @ApiOperation(value = "给文章点赞",notes = "给文章点赞")
    @PostMapping("/like/{id}")
    public Result<Integer> like(@PathVariable Integer id){
        //TODO 先这样写着，等社区基本功能做完再来完善。
        //先查询该文章是否被该用户点赞
        //用redis先存 然后一段时间后存入mysql
        String username = JwtUtil.getUserInfo();
        String key = username + "!" + id;
        redisTemplate.opsForHash().put("likeOrNot", key, "1");
        Integer articleCount = (Integer) redisTemplate.opsForHash().get("articleCount", id);
        if (articleCount != null) {
            redisTemplate.opsForHash().put("articleCount", id, articleCount++);
        } else {
            redisTemplate.opsForHash().put("articleCount", id, 1);
        }
        return new Result<Integer>(true, StatusCode.OK,"点赞成功");
    }

    /**
     * 取消点赞
     */
    @ApiOperation(value = "取消点赞",notes = "取消点赞")
    @DeleteMapping("/like/{id}")
    public Result notLike(@PathVariable Integer id){
        String username = JwtUtil.getUserInfo();
        String key = username + "!" + id;
        //删除这条点赞记录
        redisTemplate.opsForHash().delete("likeOrNot", key);
        //让点赞总数-1
        Integer articleCount = (Integer) redisTemplate.opsForHash().get("articleCount", id);
        redisTemplate.opsForHash().put("articleCount", id, articleCount--);
        return new Result<List<Article>>(true, StatusCode.OK,"取消点赞");
    }

    /**
     * 查询点赞状态
     */
    @ApiOperation(value = "查询是否点赞",notes = "查询是否点赞")
    @GetMapping("/like/{id}")
    public Result getLike(@PathVariable Integer id){
        String username = JwtUtil.getUserInfo();
        String key = username + "!" + id;
        if (redisTemplate.opsForHash().get("likeOrNot", key) != null) {
            return new Result<List<Article>>(true, StatusCode.OK, "已被点赞", 1);
        } else {
            return new Result<List<Article>>(true, StatusCode.OK, "未点赞", 0);
        }
    }


    /**
     * 文章详情页
     */
    @ApiOperation(value = "文章详情页",notes = "这里可以看评论以及一些回复等，该分为三个部分，文章，评论区，以及点赞情况，注意：不要使用文章里面的like数")
    @GetMapping("/{id}")
    public Result<Map<String, Object>> articleDetail(@PathVariable Integer id){
        Map<String, Object> result = new HashMap<>();
        //TODO
        //查文章详情页，涉及两个表的CRUD
        //增加访问量，每请求一次这个接口就会增加1次访问
        Article article = articleService.findById(id);
        Integer watchNum = article.getWatchNum();
        watchNum++;
        article.setWatchNum(watchNum);
        articleService.update(article);

        //显示详情数据  分为3部分  1.文章 2.点赞（放redis中） 3.评论区

        //1.文章
        result.put("article", article);

        //2.评论
        List<Comment> allComments = commentService.findByArticleId(id);
        List<Comment> comments = new ArrayList<>();
        List<Comment> parents = new ArrayList<>();
        for (Comment comment : allComments) {
            //这是评论
            if (comment.getParentId() == 0) {
                comments.add(comment);
                parents.add(comment);
            } else {
                boolean foundParent = false;
                for (Comment parent : parents) {
                    if (comment.getParentId() == parent.getId()) {
                        if (parent.getChild() == null) {
                            parent.setChild(new ArrayList<>());
                        }
                        parent.getChild().add(comment);
                        parents.add(comment);
                        foundParent = true;
                        break;
                    }
                }
                if (!foundParent) {
                    throw new RuntimeException("can not find the parent comment");
                }
            }
        }
        //对嵌套型处理成两层型。
        List<Comment> newComments = commentService.findParent(comments);
        result.put("comments", newComments);

        //3.点赞
        Map<String,Object> liked = new HashMap<>();
        //先查询redis缓存中有无点赞记录
        String username = JwtUtil.getUserInfo();
        String key = username + "!" + id;
        Object likeOrNot = redisTemplate.opsForHash().get("likeOrNot", key);
        if (likeOrNot != null) {
            liked.put("status", 1);
        } else {
            liked.put("status", 0);
        }
        Integer articleCount = (Integer) redisTemplate.opsForHash().get("articleCount", id);
        liked.put("count", articleCount);
        result.put("liked", liked);
        return new Result<Map<String, Object>>(true, StatusCode.OK,"查询成功", result) ;
    }

    /**
     * 制订社区规则（后台）
     */
    @ApiOperation(value = "社区规则",notes = "社区规则")
    @PostMapping("/makeRules")
    public Result getRules(@RequestBody Map<String,String> map){
        String rule = map.get("rule");
        redisTemplate.opsForValue().set("rule", rule);
        return new Result<List<Article>>(true, StatusCode.OK,"制订规则成功") ;
    }

    /**
     * 社区规则
     */
    @ApiOperation(value = "社区规则",notes = "社区规则")
    @GetMapping("/rules")
    public Result<String> getRules(){
        String rule = (String) redisTemplate.opsForValue().get("rule");
        return new Result<String>(true, StatusCode.OK,"查询成功", rule) ;
    }

    /**
     * 查询文章数和总人数
     */
    @ApiOperation(value = "查询文章数和总人数",notes = "查询文章数和总人数")
    @GetMapping("/getNums")
    public Result<Map<String,Integer>> getNums(){
        return new Result<Map<String,Integer>>(true, StatusCode.OK, "查询成功！", articleService.countMember());
    }

    /**
     * 按最新发布时间排序
     */
    @ApiOperation(value = "按最新发布时间排序",notes = "按最新发布时间排序")
    @GetMapping("/findNew")
    public Result<List<Article>> findNew(){
        List<Article> newList = articleService.findByNew();
        return new Result<List<Article>>(true, StatusCode.OK, "查询成功！", newList);
    }

    /**
     * 按评论最多的排名
     */
    @ApiOperation(value = "按评论最多的排名",notes = "按评论最多的排名")
    @GetMapping("/findMostCom")
    public Result<List<Article>> findMostCom(){
        List<Article> articles = articleService.findMostCom();
        return new Result<List<Article>>(true, StatusCode.OK, "查询成功！", articles);
    }
}
