package com.zhongy.user.controller;

import com.github.pagehelper.PageInfo;
import com.zhongy.user.pojo.User;
import com.zhongy.user.service.UserService;
import com.zhongy.user.util.SendMail;
import com.zhongy.user.util.SixIdCode;
import entity.Result;
import entity.StatusCode;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/****
 * @Author:admin
 * @Description:
 * @Date 2019/6/14 0:18
 *****/

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Value("${head.url}")
    private String url;

    @Autowired
    private RedisTemplate redisTemplate;

    /***
     * User分页条件搜索实现
     * @param user
     * @param page
     * @param size
     * @return
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ApiOperation(value = "User条件分页查询",notes = "分页条件查询User方法详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "page", value = "当前页", required = true, dataType = "Integer"),
            @ApiImplicitParam(paramType = "path", name = "size", value = "每页显示条数", required = true, dataType = "Integer")
    })
    @PostMapping(value = "/search/{page}/{size}" )
    public Result<PageInfo> findPage(@RequestBody(required = false) @ApiParam(name = "User对象",value = "传入JSON数据",required = false) User user, @PathVariable  int page, @PathVariable  int size){
        //调用UserService实现分页条件查询User
        PageInfo<User> pageInfo = userService.findPage(user, page, size);
        return new Result(true,StatusCode.OK,"查询成功",pageInfo);
    }


    /***
     * User分页搜索实现
     * @param page:当前页
     * @param size:每页显示多少条
     * @return
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ApiOperation(value = "User分页查询",notes = "分页查询User方法详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "page", value = "当前页", required = true, dataType = "Integer"),
            @ApiImplicitParam(paramType = "path", name = "size", value = "每页显示条数", required = true, dataType = "Integer")
    })
    @GetMapping(value = "/search/{page}/{size}" )
    public Result<PageInfo> findPage(@PathVariable  int page, @PathVariable  int size){
        //调用UserService实现分页查询User
        PageInfo<User> pageInfo = userService.findPage(page, size);
        return new Result<PageInfo>(true,StatusCode.OK,"查询成功",pageInfo);
    }

    /***
     * 多条件搜索User数据
     * @param user
     * @return
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ApiOperation(value = "User条件查询",notes = "条件查询User方法详情")
    @PostMapping(value = "/search" )
    public Result<List<User>> findList(@RequestBody(required = false) @ApiParam(name = "User对象",value = "传入JSON数据",required = false) User user){
        //调用UserService实现条件查询User
        List<User> list = userService.findList(user);
        return new Result<List<User>>(true,StatusCode.OK,"查询成功",list);
    }

    /***
     * 根据ID删除User
     * @param id
     * @return
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ApiOperation(value = "User根据ID删除",notes = "根据ID删除User方法详情")
    @ApiImplicitParam(paramType = "path", name = "username", value = "主键ID", required = true, dataType = "String")
    @DeleteMapping(value = "/{username}" )
    public Result delete(@PathVariable String id){
        //调用UserService实现根据主键删除
        userService.delete(id);
        return new Result(true,StatusCode.OK,"删除成功");
    }

    /***
     * 修改User数据
     * @param user
     * @param id
     * @return
     */
    @ApiOperation(value = "User根据ID修改",notes = "根据ID修改User方法详情")
    @ApiImplicitParam(paramType = "path", name = "username", value = "主键ID", required = true, dataType = "String")
    @PutMapping(value="/{username}")
    public Result update(@RequestBody @ApiParam(name = "User对象",value = "传入JSON数据",required = false) User user,@PathVariable(value = "username") String id){
        //设置主键值
        user.setUsername(id);
        //调用UserService实现修改User
        userService.update(user);
        return new Result(true,StatusCode.OK,"修改成功");
    }

    /***
     * 新增User数据
     * @param
     * @return
     */
    @ApiOperation(value = "User添加",notes = "注册User方法详情")
    @PostMapping("/register")
    public Result register(@RequestBody  @ApiParam(name = "表单对象",value = "传入JSON数据",required = true) Map<String,String> map){
        User user = new User();
        String email = map.get("email");
        user.setUsername(map.get("username"));
        //先对密码进行加密操作
        String password = map.get("password");
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encode_password = encoder.encode(password);
        user.setPassword(encode_password);
        user.setEmail(email);
        //TODO 用户角色暂时还没从数据库赋予权限
        user.setRole(2);
        user.setHeadPicUrl(map.getOrDefault("headPicUrl", url));
        user.setCreated(new Date());
        user.setMobile(map.get("mobile"));
        user.setName(map.get("name")); //昵称
        String code = (String) redisTemplate.opsForValue().get(email);
        if (code.equals(map.get("code"))) {
            //调用UserService实现添加User
            userService.add(user);
            return new Result(true,StatusCode.OK,"注册成功");
        }
        return new Result(false, StatusCode.ERROR, "验证码不正确,请重试");
    }

    /**
     * 发送6位验证码到注册邮箱中，有效期为120s，过了有效期
     */
    @ApiOperation(value = "将验证码发送到指定邮箱",notes = "有效期为120s，过时失效")
    @PostMapping("/idCode")
    public Result sendToEmail(@RequestBody Map<String,String> map) throws MessagingException {
        //产生一个6位验证码  这里异常处理可以做个重发的操作
        String code = SixIdCode.getSixCode();
        String email = map.get("email");
        if(redisTemplate.opsForValue().get(email) != null) {
            return new Result(false, StatusCode.ERROR, "请勿重复发送验证码！");
        }
        SendMail.sendCodeEmail(email, code);
        //将验证码传入redis中，设置120s过期时间
        redisTemplate.opsForValue().set(email, code, 60 * 2, TimeUnit.SECONDS);
        return new Result(true, StatusCode.OK,"验证码生成成功");
    }


    /***
     * 根据ID查询User数据
     * @param username
     * @return
     */
    @ApiOperation(value = "User根据username查询",notes = "根据ID查询User方法详情,这里的主键是用户名，不是int类型的ID！")
    @ApiImplicitParam(paramType = "path", name = "username", value = "主键username", required = true, dataType = "String")
    @GetMapping("/{username}")
    public Result<User> findById(@PathVariable String username){
        //调用UserService实现根据主键查询User
        User user = userService.findById(username);
        return new Result<User>(true,StatusCode.OK,"查询成功", user);
    }

    /***
     * 查询User全部数据
     * @return
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ApiOperation(value = "查询所有User",notes = "查询所有User方法详情")
    @GetMapping
    public Result<List<User>> findAll(){
        //调用UserService实现查询所有User
        List<User> list = userService.findAll();
        return new Result<List<User>>(true, StatusCode.OK,"查询成功",list) ;
    }

    /**
     * 添加qq用户
     */
    @ApiOperation(value = "添加qq用户",notes = "添加qq用户")
    @PostMapping(value = "/addQQUser")
    public Result<User> addQQUser(@RequestBody User user){
        user.setCreated(new Date());
        user.setRole(2);
//        user.setPassword("123456");
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encode_password = encoder.encode("123456");
        user.setPassword(encode_password);
        userService.add(user);
        return new Result<User>(true, StatusCode.OK,"添加成功");
    }

    /**
     * 注册时用于验证用户名
     */
    @ApiOperation(value = "验证用户名和邮箱",notes = "验证用户名和邮箱")
    @GetMapping("/verify")
    public Result<User> findAll(@RequestParam(value = "username")String username){
        //todo 这里给个用户名就好了  不要直接查询整个user数据
        User user = userService.findById(username);
        return new Result<>(true, StatusCode.OK, "查询成功", user);
    }

}
