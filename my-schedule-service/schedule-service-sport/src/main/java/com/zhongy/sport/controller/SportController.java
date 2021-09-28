package com.zhongy.sport.controller;

import com.github.pagehelper.PageInfo;
import com.zhongy.sport.pojo.Sport;
import com.zhongy.sport.service.SportService;
import com.zhongy.user.feign.UserFeign;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/****
 * @Author:admin
 * @Description:
 * @Date 2019/6/14 0:18
 *****/

@RestController
@RequestMapping("/sport")
@CrossOrigin
public class SportController {

    @Autowired
    private SportService sportService;


    /***
     * Sport分页条件搜索实现
     * @param sport
     * @param page
     * @param size
     * @return
     */
    @PostMapping(value = "/search/{page}/{size}" )
    public Result<PageInfo> findPage(@RequestBody(required = false)  Sport sport, @PathVariable  int page, @PathVariable  int size){
        //调用SportService实现分页条件查询Sport
        PageInfo<Sport> pageInfo = sportService.findPage(sport, page, size);
        return new Result(true,StatusCode.OK,"查询成功",pageInfo);
    }

    /***
     * Sport分页搜索实现
     * @param page:当前页
     * @param size:每页显示多少条
     * @return
     */
    @GetMapping(value = "/search/{page}/{size}" )
    public Result<PageInfo> findPage(@PathVariable  int page, @PathVariable  int size){
        //调用SportService实现分页查询Sport
        PageInfo<Sport> pageInfo = sportService.findPage(page, size);
        return new Result<PageInfo>(true,StatusCode.OK,"查询成功",pageInfo);
    }

    /***
     * 多条件搜索品牌数据
     * @param sport
     * @return
     */
    @PostMapping(value = "/search" )
    public Result<List<Sport>> findList(@RequestBody(required = false)  Sport sport){
        //调用SportService实现条件查询Sport
        List<Sport> list = sportService.findList(sport);
        return new Result<List<Sport>>(true,StatusCode.OK,"查询成功",list);
    }

    /***
     * 根据ID删除品牌数据
     * @param id
     * @return
     */
    @DeleteMapping(value = "/{id}" )
    public Result delete(@PathVariable Integer id){
        //调用SportService实现根据主键删除
        sportService.delete(id);
        return new Result(true,StatusCode.OK,"删除成功");
    }

    /***
     * 修改Sport数据
     * @param sport
     * @param id
     * @return
     */
    @PutMapping(value="/{id}")
    public Result update(@RequestBody  Sport sport,@PathVariable Integer id){
        //设置主键值
        sport.setId(id);
        //调用SportService实现修改Sport
        sportService.update(sport);
        return new Result(true,StatusCode.OK,"修改成功");
    }

    /***
     * 新增Sport数据
     * @param sport
     * @return
     */
    @PostMapping
    public Result add(@RequestBody   Sport sport){
        //调用SportService实现添加Sport
        sportService.add(sport);
        return new Result(true,StatusCode.OK,"添加成功");
    }

    /***
     * 根据ID查询Sport数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<Sport> findById(@PathVariable Integer id){
        //调用SportService实现根据主键查询Sport
        Sport sport = sportService.findById(id);
        return new Result<Sport>(true,StatusCode.OK,"查询成功",sport);
    }

    /***
     * 查询Sport全部数据
     * @return
     */
    @GetMapping
    public Result<List<Sport>> findAll(){
        //调用SportService实现查询所有Sport
        List<Sport> list = sportService.findAll();
        return new Result<List<Sport>>(true, StatusCode.OK,"查询成功",list) ;
    }
}
