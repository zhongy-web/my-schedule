package com.zhongy.schedule.dao;

import com.zhongy.schedule.pojo.Daily;
import com.zhongy.schedule.pojo.DailyDetail;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.special.InsertListMapper;

import java.util.Date;
import java.util.List;

@Repository
public interface DailyMapper extends Mapper<Daily>, InsertListMapper<Daily> {
    @Select("select * from schedule_daily_detail where daily_id = (select id from schedule_daily sd where sd.todayTime= DATE_FORMAT(#{todayTime}, '%Y-%m-%d') and sd.name = #{name}) ORDER BY start_time")
    List<DailyDetail> findTodayAll(
            @Param(value = "name") String name,
            @Param(value = "todayTime") String todayTime
    );

    @Select("select * from schedule_daily where name = #{name}")
    Daily findByUsername(String name);

    @Select("select * from schedule_daily where name = #{name} and todayTime = DATE_FORMAT(NOW(), '%Y-%m-%d')")
    Daily findToday(String name);

    @Select("select * from schedule_daily where name = #{name} and todayTime=DATE_FORMAT(#{todayTime}, '%Y-%m-%d')")
    Daily selectByDateAndName(@Param(value = "name") String name, @Param(value = "todayTime") String todayTime);

    @Select("select count from schedule_daily where id in(select id from schedule_daily where week_id = #{id} and name = #{name})")
    List<Integer> findCounts(@Param(value = "name") String name, @Param(value = "id")Integer id);


}
