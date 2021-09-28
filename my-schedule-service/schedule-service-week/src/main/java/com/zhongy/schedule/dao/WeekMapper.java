package com.zhongy.schedule.dao;

import com.zhongy.schedule.pojo.Week;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;


@Repository
public interface WeekMapper extends Mapper<Week> {

    /**
     * 根据名字和日期查询week
     * @param name
     * @return
     */
    @Select("select * from schedule_week where name = #{name} and DATE_FORMAT(NOW(), '%Y-%m-%d') <= sunday_date")
    Week findByNameAndSun(@Param(value = "name") String name);

    /**
     * 查询本周所有的每日任务id集合
     */
    @Select("select id from schedule_daily where week_id = (select id from schedule_week where id = #{id}) and name = #{name}")
    List<Integer> findAll2ListId(@Param(value = "id") Integer id, @Param(value = "name") String name);

    /**
     * 根据名字查询weekId
     */
    @Select("select week_id from schedule_daily where name = #{name} and todayTime = DATE_FORMAT(NOW(), '%Y-%m-%d')")
    Integer findIdByName(String name);

    /**
     * 找到上周的表
     */
    @Select("select * from schedule_week where name = #{name} and YEARWEEK(date_format(sunday_date,'%Y-%m-%d'), 1) =YEARWEEK(now())-1")
    Week findLastWeek(String name);

}
