package com.zhongy.schedule.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "schedule_daily_detail")
@Data
public class DailyDetail implements Serializable {

    @Id
    @ApiModelProperty(value = "每日计划表明细的id")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "JDBC")
    @Column(name = "id")
    private Integer id;

    @ApiModelProperty(value = "每日计划表明细的内容",required = true)
    @Column(name = "content")
    private String content;

    @ApiModelProperty(value = "每日计划表明细是否完成，默认为0，0->未完成，1->已完成",required = true)
    @Column(name = "hasDone")
    private String hasDone;

    @ApiModelProperty(value = "该任务的类别",required = true)
    @Column(name = "type")
    private String type;

    @ApiModelProperty(value = "关联每日计划表",required = true)
    @Column(name = "daily_id")
    private Integer daily_id;

    @ApiModelProperty(value = "开始时间",required = true)
    @JsonFormat(pattern="HH:mm:ss", timezone = "GMT+8")
    @Column(name = "start_time")
    private String start_time;

    @ApiModelProperty(value = "结束时间",required = true)
    @JsonFormat(pattern="HH:mm:ss", timezone = "GMT+8")
    @Column(name = "end_time")
    private String end_time;

    @ApiModelProperty(value = "邮箱提醒是否生效",required = true)
    @Column(name = "msgStatus")
    private Integer msgStatus;

    @ApiModelProperty(value = "消息版本号",required = true)
    @Column(name = "msgVersion")
    private Integer msgVersion;

    @ApiModelProperty(value = "自动完成是否生效",required = true)
    @Column(name = "autoStatus")
    private Integer autoStatus;

    @ApiModelProperty(value = "完成版本号",required = true)
    @Column(name = "autoVersion")
    private Integer autoVersion;
}
