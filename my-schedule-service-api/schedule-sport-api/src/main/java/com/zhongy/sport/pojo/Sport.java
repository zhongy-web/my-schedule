package com.zhongy.sport.pojo;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/****
 * @Author:admin
 * @Description:Sport构建
 * @Date 2019/6/14 19:13
 *****/
@Data
@Table(name="tb_sport")
public class Sport implements Serializable{

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
	private Integer id;//

    @Column(name = "name")
	private String name;//运动项目的名字

    @Column(name = "age")
	private String age;//该运动适宜年龄

    @Column(name = "attention")
	private String attention;//注意事项

    @Column(name = "image")
	private String image;//图片的url地址

}
