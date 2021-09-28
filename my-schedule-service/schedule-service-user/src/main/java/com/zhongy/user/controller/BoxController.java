package com.zhongy.user.controller;

import com.github.pagehelper.PageInfo;
import com.zhongy.user.pojo.IdeasBox;
import com.zhongy.user.pojo.User;
import com.zhongy.user.service.BoxService;
import com.zhongy.user.service.UserService;
import com.zhongy.user.util.SendMail;
import entity.Result;
import entity.StatusCode;
import entity.TokenDecode;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

/****
 * @Author:admin
 * @Description:
 * @Date 2019/6/14 0:18
 *****/
@RestController
@RequestMapping("/box")
public class BoxController {

    @Autowired
    private BoxService boxService;

    @Autowired
    private UserService userService;


    /***
     * 对采纳的意见将通知信息发送给该作者的邮箱中
     * 后期优化思路：
     *  邮箱发送是一个耗时操作，所以我们可以将这个操作放到队列中进行。
     */
    @ApiOperation(value = "采纳作者的意见",notes = "采纳作者的意见")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "id", value = "意见的id", required = true, dataType = "Integer"),
    })
    @GetMapping(value = "/accept/{id}" )
    public Result<PageInfo> accept(@PathVariable Integer id) throws MessagingException {
        //找到idea对象
        IdeasBox idea = boxService.findById(id);
        //找到对应的用户的邮箱地址
        String name = idea.getName();
        User user = userService.findById(name);
        if (user != null) {
//            String email = user.getEmail();
//            Mail mail = new Mail();
//            mail.setTo(email);
//            mail.setMsgId(UUID.randomUUID().toString().replace("-", ""));
//            mail.setTitle("恭喜你");
//            mail.setContent("恭喜您，我们采纳了您的意见，请将您的支付宝账号发送到该邮箱中。");
//            //发送邮件到此地址中
//            produceService.send(mail);
            SendMail.sendIdeaEmail(user.getEmail(), name);
            //将采纳的状态改为1,并更新
            idea.setStatus('1');
            boxService.update(idea);
            return new Result(true,StatusCode.OK,"已采纳!");
        }
        return new Result(false, StatusCode.ERROR,"未知错误");
    }

    /***
     * IdeasBox分页条件搜索实现
     * @param ideasBox
     * @param page
     * @param size
     * @return
     */
    @ApiOperation(value = "IdeasBox条件分页查询",notes = "分页条件查询IdeasBox方法详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "page", value = "当前页", required = true, dataType = "Integer"),
            @ApiImplicitParam(paramType = "path", name = "size", value = "每页显示条数", required = true, dataType = "Integer")
    })
    @PostMapping(value = "/search/{page}/{size}" )
    public Result<PageInfo> findPage(@RequestBody(required = false) @ApiParam(name = "IdeasBox对象",value = "传入JSON数据",required = false) IdeasBox ideasBox, @PathVariable  int page, @PathVariable  int size){
        //调用BoxService实现分页条件查询IdeasBox
        PageInfo<IdeasBox> pageInfo = boxService.findPage(ideasBox, page, size);
        return new Result(true,StatusCode.OK,"查询成功",pageInfo);
    }


    /***
     * IdeasBox分页搜索实现
     * @param page:当前页
     * @param size:每页显示多少条
     * @return
     */
    @ApiOperation(value = "IdeasBox分页查询",notes = "分页查询IdeasBox方法详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "page", value = "当前页", required = true, dataType = "Integer"),
            @ApiImplicitParam(paramType = "path", name = "size", value = "每页显示条数", required = true, dataType = "Integer")
    })
    @GetMapping(value = "/search/{page}/{size}" )
    public Result<PageInfo> findPage(@PathVariable  int page, @PathVariable  int size){
        //调用BoxService实现分页查询IdeasBox
        PageInfo<IdeasBox> pageInfo = boxService.findPage(page, size);
        return new Result<PageInfo>(true,StatusCode.OK,"查询成功",pageInfo);
    }

    /***
     * 多条件搜索品牌数据
     * @param ideasBox
     * @return
     */
    @ApiOperation(value = "IdeasBox条件查询",notes = "条件查询IdeasBox方法详情")
    @PostMapping(value = "/search" )
    public Result<List<IdeasBox>> findList(@RequestBody(required = false) @ApiParam(name = "IdeasBox对象",value = "传入JSON数据",required = false) IdeasBox ideasBox){
        //调用UserService实现条件查询User
        List<IdeasBox> list = boxService.findList(ideasBox);
        return new Result<List<IdeasBox>>(true,StatusCode.OK,"查询成功",list);
    }

    /***
     * 根据ID删除品牌数据
     * @param id
     * @return
     */
    @ApiOperation(value = "IdeasBox根据ID删除",notes = "根据ID删除IdeasBox方法详情")
    @ApiImplicitParam(paramType = "path", name = "id", value = "主键ID", required = true)
    @DeleteMapping(value = "/{id}" )
    public Result delete(@PathVariable Integer id){
        //调用BoxService实现根据主键删除
        boxService.delete(id);
        return new Result(true,StatusCode.OK,"删除成功");
    }

    /***
     * 修改IdeasBox数据
     * @param ideasBox
     * @param id
     * @return
     */
    @ApiOperation(value = "IdeasBox根据ID修改",notes = "根据ID修改IdeasBox方法详情")
    @ApiImplicitParam(paramType = "path", name = "id", value = "主键ID", required = true, dataType = "String")
    @PutMapping(value="/{id}")
    public Result update(@RequestBody @ApiParam(name = "IdeasBox对象",value = "传入JSON数据",required = false) IdeasBox ideasBox,@PathVariable Integer id){
        //设置主键值
        ideasBox.setId(id);
        //调用UserService实现修改User
        boxService.update(ideasBox);
        return new Result(true,StatusCode.OK,"修改成功");
    }

    /***
     * 新增IdeasBox数据
     * @param ideasBox
     * @return
     */
    @ApiOperation(value = "IdeasBox添加",notes = "注册IdeasBox方法详情")
    @PostMapping("/add")
    public Result add(@RequestBody  @ApiParam(name = "IdeasBox对象",value = "传入JSON数据",required = true) IdeasBox ideasBox){
        Map<String, String> userInfo = TokenDecode.getUserInfo();
        String username = userInfo.get("username");
        User user = userService.findById(username);
        if (user != null) {
            ideasBox.setName(username);
            ideasBox.setTime(new Timestamp(new Date().getTime()));
            boxService.add(ideasBox);
            return new Result(true, StatusCode.OK, "添加成功");
        }
        return new Result(false, StatusCode.OK, "添加失败");
    }

    /***
     * 根据ID查询IdeasBox数据
     * @param id
     * @return
     */
    @ApiOperation(value = "IdeasBox根据id查询",notes = "根据ID查询IdeasBox方法详情")
    @ApiImplicitParam(paramType = "path", name = "id", value = "主键id", required = true, dataType = "int")
    @GetMapping("/{id}")
    public Result<IdeasBox> findById(@PathVariable Integer id){
        //调用BoxService实现根据主键查询IdeasBox
        IdeasBox ideasBox = boxService.findById(id);
        return new Result<IdeasBox>(true,StatusCode.OK,"查询成功", ideasBox);
    }

    /***
     * 查询IdeasBox全部数据
     * @return
     */
    @ApiOperation(value = "查询所有IdeasBox",notes = "查询所有IdeasBox方法详情")
    @GetMapping
    public Result<List<IdeasBox>> findAll(){
        //调用BoxService实现查询所有Box
        List<IdeasBox> list = boxService.findAll();
        return new Result<List<IdeasBox>>(true, StatusCode.OK,"查询成功",list) ;
    }
}
