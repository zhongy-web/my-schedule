package com.zhongy.user.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/****
 * @Author:admin
 * @Description:User构建
 * @Date 2019/6/14 19:13
 *****/
@Data
@ApiModel(description = "User",value = "User")
@Table(name="tb_user")
public class User implements Serializable{

	@ApiModelProperty(value = "用户名",required = false)
	@Id
    @Column(name = "username")
	private String username;//用户名

	@ApiModelProperty(value = "密码，加密存储",required = false)
    @Column(name = "password")
	private String password;//密码，加密存储

	@ApiModelProperty(value = "昵称",required = false)
	@Column(name = "name")
	private String name;//昵称

	@ApiModelProperty(value = "注册邮箱",required = false)
    @Column(name = "email")
	private String email;//注册邮箱

	@ApiModelProperty(value = "创建时间",required = false)
	@JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    @Column(name = "created")
	private Date created;//创建时间

	@ApiModelProperty(value = "权限id",required = false)
	@Column(name = "role")
	private Integer role;//权限id

	@ApiModelProperty(value = "头像地址",required = false)
    @Column(name = "head_pic_url")
	private String headPicUrl;//头像地址

	@ApiModelProperty(value = "性别，1男，0女",required = false)
    @Column(name = "sex")
	private String sex;//性别，1男，0女

	@ApiModelProperty(value = "出生年月日",required = false)
	@JsonFormat(pattern="yyyy-MM-dd", timezone = "GMT+8")
    @Column(name = "birthday")
	private Date birthday;//出生年月日

	@ApiModelProperty(value = "手机号",required = false)
	@Column(name = "mobile")
	private String mobile;//手机号
}
