package com.zhongy.schedule.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
@Data
@Table(name="schedule_daily")
public class Daily {
    @ApiModelProperty(value = "每日计划表的ID",required = false)
    @Id
    @Column(name = "id")
    private Integer id;

    @ApiModelProperty(value = "每日计划表的日期",required = true)
    @JsonFormat(pattern="yyyy-MM-dd", timezone = "GMT+8")
    @Column(name = "todayTime")
    private Date todayTime;

    @ApiModelProperty(value = "每日计划表由谁创建",required = true)
    @Column(name = "name")
    private String name;

    @ApiModelProperty(value = "每日计划表与周计划的关联",required = true)
    @Column(name = "week_id")
    private Integer week_id;

    @ApiModelProperty(value = "每日完成数量统计",required = true)
    @Column(name = "count")
    private Integer count;

    @ApiModelProperty(value = "与每日计划明细的关联")
    @OneToMany(targetEntity = DailyDetail.class, mappedBy = "daily_id")
    private List<DailyDetail> dailyDetails;

}
