package com.zhongy.user.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.io.Serializable;

/****
 * @Author:admin
 * @Description:Role构建
 * @Date 2019/6/14 19:13
 *****/
@ApiModel(description = "Role",value = "Role")
@Table(name="tb_role")
public class Role implements Serializable{

	@ApiModelProperty(value = "角色id",required = false)
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
	private Integer id;//角色id

	@ApiModelProperty(value = "角色名",required = false)
    @Column(name = "role_name")
	private String roleName;//角色名

	@ApiModelProperty(value = "拥有的权限",required = false)
    @Column(name = "role_auth")
	private String roleAuth;//拥有的权限



	//get方法
	public Integer getId() {
		return id;
	}

	//set方法
	public void setId(Integer id) {
		this.id = id;
	}
	//get方法
	public String getRoleName() {
		return roleName;
	}

	//set方法
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	//get方法
	public String getRoleAuth() {
		return roleAuth;
	}

	//set方法
	public void setRoleAuth(String roleAuth) {
		this.roleAuth = roleAuth;
	}


}
