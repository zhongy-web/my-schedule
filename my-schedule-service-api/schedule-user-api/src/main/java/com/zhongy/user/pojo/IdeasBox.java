package com.zhongy.user.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

/****
 * @Author:admin
 * @Description:User构建
 * @Date 2019/6/14 19:13
 *****/
@Data
@ApiModel(description = "IdeasBox",value = "IdeasBox")
@Table(name="ideas_box")
public class IdeasBox implements Serializable{

	@ApiModelProperty(value = "id",required = false)
	@Id
    @Column(name = "id")
	private Integer id;//id

	@ApiModelProperty(value = "作者的名字",required = false)
    @Column(name = "name")
	private String name;//作者名

	@ApiModelProperty(value = "意见内容",required = false)
    @Column(name = "content")
	private String content;

	@ApiModelProperty(value = "是否采纳 0未采纳，1采纳",required = false)
	@Column(name = "status")
	private Character status;//采纳状态

	@ApiModelProperty(value = "创建时间",required = false)
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "time")
	private Timestamp time;//创建时间

}
