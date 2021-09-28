package com.zhongy.community.feign;
import com.github.pagehelper.PageInfo;
import com.zhongy.community.pojo.Comment;
import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/****
 * @Author:admin
 * @Description:
 * @Date 2019/6/18 13:58
 *****/
@FeignClient(name="community")
@RequestMapping("/comment")
public interface CommentFeign {

    /***
     * Comment分页条件搜索实现
     * @param comment
     * @param page
     * @param size
     * @return
     */
    @PostMapping(value = "/search/{page}/{size}" )
    Result<PageInfo> findPage(@RequestBody(required = false) Comment comment, @PathVariable int page, @PathVariable  int size);

    /***
     * Comment分页搜索实现
     * @param page:当前页
     * @param size:每页显示多少条
     * @return
     */
    @GetMapping(value = "/search/{page}/{size}" )
    Result<PageInfo> findPage(@PathVariable  int page, @PathVariable  int size);

    /***
     * 多条件搜索品牌数据
     * @param comment
     * @return
     */
    @PostMapping(value = "/search" )
    Result<List<Comment>> findList(@RequestBody(required = false) Comment comment);

    /***
     * 根据ID删除品牌数据
     * @param id
     * @return
     */
    @DeleteMapping(value = "/{id}" )
    Result delete(@PathVariable Integer id);

    /***
     * 修改Comment数据
     * @param comment
     * @param id
     * @return
     */
    @PutMapping(value="/{id}")
    Result update(@RequestBody Comment comment,@PathVariable Integer id);

    /***
     * 新增Comment数据
     * @param comment
     * @return
     */
    @PostMapping
    Result add(@RequestBody Comment comment);

    /***
     * 根据ID查询Comment数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    Result<Comment> findById(@PathVariable Integer id);

    /***
     * 查询Comment全部数据
     * @return
     */
    @GetMapping
    Result<List<Comment>> findAll();
}