package com.zhongy.community.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/****
 * @Author:admin
 * @Description:Comment构建
 * @Date 2019/6/14 19:13
 *****/
@Data
@ApiModel(description = "Comment",value = "Comment")
@Table(name="tb_comment")
public class Comment implements Serializable{

	@ApiModelProperty(value = "评论id",required = false)
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
	private Integer id;//评论id

	@ApiModelProperty(value = "评论人头像",required = false)
	@Column(name = "headUrl")
	private String headUrl;//评论人头像

	@ApiModelProperty(value = "评论的人",required = false)
    @Column(name = "name")
	private String name;//评论的人

	@ApiModelProperty(value = "回复哪个人",required = false)
	@Column(name = "toWho")
	private String toWho;//回复哪个人

	@ApiModelProperty(value = "评论内容",required = false)
    @Column(name = "comContent")
	private String comContent;//评论内容

	@ApiModelProperty(value = "评论时间",required = false)
	@JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	@Column(name = "commentTime")
	private Date commentTime;//评论时间

	@ApiModelProperty(value = "关联文章表",required = false)
    @Column(name = "articleId")
	private Integer articleId;//关联文章表

	@ApiModelProperty(value = "关联自己，用作评论的回复信息",required = false)
    @Column(name = "parentId")
	private Integer parentId;//关联自己，用作评论的回复信息

	@ApiModelProperty(value = "子级评论",required = false)
	private List<Comment> child = new ArrayList<>();

}
