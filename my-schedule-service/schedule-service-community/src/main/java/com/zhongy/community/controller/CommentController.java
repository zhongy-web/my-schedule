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
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private ArticleService articleService;

    /***
     * Comment分页条件搜索实现
     * @param comment
     * @param page
     * @param size
     * @return
     */
    @ApiOperation(value = "Comment条件分页查询",notes = "分页条件查询Comment方法详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "page", value = "当前页", required = true, dataType = "Integer"),
            @ApiImplicitParam(paramType = "path", name = "size", value = "每页显示条数", required = true, dataType = "Integer")
    })
    @PostMapping(value = "/search/{page}/{size}" )
    public Result<PageInfo> findPage(@RequestBody(required = false) @ApiParam(name = "Comment对象",value = "传入JSON数据",required = false) Comment comment, @PathVariable  int page, @PathVariable  int size){
        //调用CommentService实现分页条件查询Comment
        PageInfo<Comment> pageInfo = commentService.findPage(comment, page, size);
        return new Result(true,StatusCode.OK,"查询成功",pageInfo);
    }

    /***
     * Comment分页搜索实现
     * @param page:当前页
     * @param size:每页显示多少条
     * @return
     */
    @ApiOperation(value = "Comment分页查询",notes = "分页查询Comment方法详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "page", value = "当前页", required = true, dataType = "Integer"),
            @ApiImplicitParam(paramType = "path", name = "size", value = "每页显示条数", required = true, dataType = "Integer")
    })
    @GetMapping(value = "/search/{page}/{size}" )
    public Result<PageInfo> findPage(@PathVariable  int page, @PathVariable  int size){
        //调用CommentService实现分页查询Comment
        PageInfo<Comment> pageInfo = commentService.findPage(page, size);
        return new Result<PageInfo>(true,StatusCode.OK,"查询成功",pageInfo);
    }

    /***
     * 多条件搜索数据
     * @param comment
     * @return
     */
    @ApiOperation(value = "Comment条件查询",notes = "条件查询Comment方法详情")
    @PostMapping(value = "/search" )
    public Result<List<Comment>> findList(@RequestBody(required = false) @ApiParam(name = "Comment对象",value = "传入JSON数据",required = false) Comment comment){
        //调用CommentService实现条件查询Comment
        List<Comment> list = commentService.findList(comment);
        return new Result<List<Comment>>(true,StatusCode.OK,"查询成功",list);
    }

    /***
     * 根据ID删除评论数据
     * @param id
     * @return
     */
    @TokenAuth //用该注解控制权限
    @ApiOperation(value = "Comment根据ID删除",notes = "根据ID删除Comment方法详情")
    @ApiImplicitParam(paramType = "path", name = "id", value = "主键ID", required = true, dataType = "Integer")
    @DeleteMapping(value = "/{id}" )
    public Result delete(@PathVariable Integer id){
        //调用CommentService实现根据主键删除
        //找到这条评论对应的文章id
        Comment comment = commentService.findById(id);
        Integer articleId = comment.getArticleId();

        //获取当前评论数
        Article article = articleService.findById(articleId);
        Integer commentNum = article.getCommentNum();

        //删除评论
        commentService.delete(id);
        //找到该评论所有回复删除
        List<Comment> comments = commentService.findByCommentId(id);
        for (Comment comm : comments) {
            commentService.delete(comm.getId());
            //让评论数-1后更新
            commentNum--;
        }
        article.setCommentNum(commentNum);
        articleService.update(article);
        return new Result(true,StatusCode.OK,"删除成功");
    }

    /***
     * 修改Comment数据
     * @param comment
     * @param id
     * @return
     */
    @ApiOperation(value = "Comment根据ID修改",notes = "根据ID修改Comment方法详情")
    @ApiImplicitParam(paramType = "path", name = "id", value = "主键ID", required = true, dataType = "Integer")
    @PutMapping(value="/{id}")
    public Result update(@RequestBody @ApiParam(name = "Comment对象",value = "传入JSON数据",required = false) Comment comment,@PathVariable Integer id){
        //设置主键值
        comment.setId(id);
        //调用CommentService实现修改Comment
        commentService.update(comment);
        return new Result(true,StatusCode.OK,"修改成功");
    }

    /***
     * 新增/回复Comment数据
     * @param comment
     * @return Result
     */
    @ApiOperation(value = "Comment添加/回复",notes = "添加/回复Comment方法详情,要传两个参数  articleId --》文章id，以及parentId --》属于哪条评论的回复，如果是评论（最上级）则设置parentId为0")
    @PostMapping("/{articleId}/{parentId}")
    public Result<Comment> add(@RequestBody  @ApiParam(name = "Comment对象",value = "传入JSON数据",required = true) Comment comment, @PathVariable(value = "articleId") Integer articleId, @PathVariable(value = "parentId") Integer parentId){
        //调用CommentService实现添加Comment
        String username = JwtUtil.getUserInfo();
        comment.setName(username);
        comment.setParentId(parentId);
        comment.setArticleId(articleId);
        Date date = new Date();
        comment.setCommentTime(date);
        commentService.add(comment);

        //找到对应的文章
        Article article = articleService.findById(articleId);
        //让这个文章评论数+1
        Integer commentNum = article.getCommentNum();
        article.setCommentNum(++commentNum);
        articleService.update(article);

        return new Result<Comment>(true,StatusCode.OK,"添加成功");
    }

    /***
     * 根据ID查询Comment数据
     * @param id
     * @return Result
     */
    @ApiOperation(value = "Comment根据ID查询",notes = "根据ID查询Comment方法详情")
    @ApiImplicitParam(paramType = "path", name = "id", value = "主键ID", required = true, dataType = "Integer")
    @GetMapping("/{id}")
    public Result<Comment> findById(@PathVariable Integer id){
        //调用CommentService实现根据主键查询Comment
        Comment comment = commentService.findById(id);
        return new Result<Comment>(true,StatusCode.OK,"查询成功",comment);
    }

    /***
     * 查询Comment全部数据
     */
    @ApiOperation(value = "查询所有Comment",notes = "查询所有Comment方法详情")
    @GetMapping
    public Result<List<Comment>> findAll(){
        //调用CommentService实现查询所有Comment
        List<Comment> list = commentService.findAll();

        return new Result<List<Comment>>(true, StatusCode.OK,"查询成功",list) ;
    }
}
