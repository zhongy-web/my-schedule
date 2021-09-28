package com.zhongy.community.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/****
 * @Author:admin
 * @Description:Article构建
 * @Date 2019/6/14 19:13
 *****/
@Data
@ApiModel(description = "Article",value = "Article")
@Table(name="tb_article")
public class Article implements Serializable{

	@ApiModelProperty(value = "文章id",required = false)
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
	private Integer id;//文章id

	@ApiModelProperty(value = "文章标题",required = false)
    @Column(name = "title")
	private String title;//文章标题

	@ApiModelProperty(value = "作者头像",required = false)
	@Column(name = "head_url")
	private String headUrl;//作者头像

	@ApiModelProperty(value = "文章作者",required = false)
    @Column(name = "name")
	private String name;//文章作者

	@ApiModelProperty(value = "文章内容",required = false)
    @Column(name = "content")
	private String content;//文章内容

	@ApiModelProperty(value = "访问人数",required = false)
    @Column(name = "watch_num", insertable = false)
	private Integer watchNum;//访问人数

	@ApiModelProperty(value = "点赞人数",required = false)
    @Column(name = "like_num", insertable = false)
	private Integer likeNum;//点赞人数

	@ApiModelProperty(value = "评论人数",required = false)
    @Column(name = "comment_num", insertable = false)
	private Integer commentNum;//评论人数

	@JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "发送文章的时间",required = false)
    @Column(name = "send_time")
	private Date sendTime;//发送文章的时间

}
