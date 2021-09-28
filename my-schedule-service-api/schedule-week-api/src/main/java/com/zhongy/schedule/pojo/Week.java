package com.zhongy.schedule.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Date;
import java.util.List;

@Data
@Table(name = "schedule_week")
public class Week {
    @Id
    @ApiModelProperty(value = "周计划id")
    @Column(name = "id")
    private Integer id;

    @ApiModelProperty(value = "每日计划表明细是否完成，默认为0，0->未完成，1->已完成",required = true)
    @Column(name = "name")
    private String name;

    @ApiModelProperty(value = "本周日的日期")
    @JsonFormat(pattern="yyyy-MM-dd", timezone = "GMT+8")
    @Column(name = "sunday_date")
    private Date sunday_date;

    @ApiModelProperty(value = "消息提醒功能")
    @Column(name = "msg_remind")
    private Integer msg_remind;

    @ApiModelProperty(value = "自动完成功能")
    @Column(name = "auto_complete")
    private Integer auto_complete;

    @ApiModelProperty(value = "与daily表的关联")
    @OneToMany(targetEntity = Daily.class, mappedBy = "week_id")
    private List<Daily> dailies;

}
