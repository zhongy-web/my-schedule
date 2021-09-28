package com.zhongy.schedule.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Table(name = "schedule_type")
@Data
public class Type implements Serializable {
    @Id
    @ApiModelProperty(value = "类别id")
    @Column(name = "id")
    private Integer id;

    @ApiModelProperty(value = "类别名称",required = true)
    @Column(name = "type_name")
    private String type_name;
}
