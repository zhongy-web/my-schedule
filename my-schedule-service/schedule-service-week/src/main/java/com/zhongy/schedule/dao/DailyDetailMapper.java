package com.zhongy.schedule.dao;

import com.zhongy.schedule.pojo.DailyDetail;
import com.zhongy.schedule.pojo.Type;
import com.zhongy.schedule.provider.MyMapper;
import com.zhongy.schedule.provider.UpdateBatchByPrimaryKeySelectiveMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.special.InsertListMapper;

import java.util.List;
import java.util.Map;

@Repository
public interface DailyDetailMapper extends Mapper<DailyDetail>, InsertListMapper<DailyDetail>, MyMapper<DailyDetail> {
    @Select("select sdd.*,sd.id from schedule_daily sd,schedule_daily_detail sdd where sd.id = sdd.daily_id")
    Map<String,String> findOne();

    /**
     * 根据id获取dailyDetail
     */
    @Select("select * from schedule_daily_detail where id = #{id} and daily_id = #{daily_id}")
    DailyDetail findById(Integer id, Integer daily_id);

    @Delete("delete from schedule_daily_detail where id = #{id} and daily_id = #{daily_id}")
    int deleteById(Integer id, Integer daily_id);

    @Delete("delete from schedule_daily_detail where daily_id = #{daily_id}")
    void deleteByDailyId(Integer daily_id);

    @Select("select * from schedule_type")
    List<Type> findTypes();

    @Select("select type name,COUNT(id) value from schedule_daily_detail where daily_id = #{daily_id} GROUP BY type")
    List<Map<String, Object>> pieChart(Integer daily_id);

    @Select("SELECT * from schedule_daily_detail where daily_id = #{daily_id}")
    List<DailyDetail> findByDailyId(Integer daily_id);

    @Select("select * from schedule_daily_detail a INNER JOIN (select id from schedule_daily where week_id = #{week_id} and name = #{name}) b on a.daily_id = b.id")
    List<DailyDetail> findDetails(Integer week_id, String name);

}
