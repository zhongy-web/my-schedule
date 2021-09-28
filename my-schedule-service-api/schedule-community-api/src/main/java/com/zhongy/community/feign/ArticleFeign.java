package com.zhongy.community.feign;
import com.github.pagehelper.PageInfo;
import com.zhongy.community.pojo.Article;
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
@RequestMapping("/article")
public interface ArticleFeign {

    /***
     * Article分页条件搜索实现
     * @param article
     * @param page
     * @param size
     * @return
     */
    @PostMapping(value = "/search/{page}/{size}" )
    Result<PageInfo> findPage(@RequestBody(required = false) Article article, @PathVariable int page, @PathVariable  int size);

    /***
     * Article分页搜索实现
     * @param page:当前页
     * @param size:每页显示多少条
     * @return
     */
    @GetMapping(value = "/search/{page}/{size}" )
    Result<PageInfo> findPage(@PathVariable  int page, @PathVariable  int size);

    /***
     * 多条件搜索品牌数据
     * @param article
     * @return
     */
    @PostMapping(value = "/search" )
    Result<List<Article>> findList(@RequestBody(required = false) Article article);

    /***
     * 根据ID删除品牌数据
     * @param id
     * @return
     */
    @DeleteMapping(value = "/{id}" )
    Result delete(@PathVariable Integer id);

    /***
     * 修改Article数据
     * @param article
     * @param id
     * @return
     */
    @PutMapping(value="/{id}")
    Result update(@RequestBody Article article,@PathVariable Integer id);

    /***
     * 新增Article数据
     * @param article
     * @return
     */
    @PostMapping
    Result add(@RequestBody Article article);

    /***
     * 根据ID查询Article数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    Result<Article> findById(@PathVariable Integer id);

    /***
     * 查询Article全部数据
     * @return
     */
    @GetMapping
    Result<List<Article>> findAll();
}