package com.zhongy.schedule.service.impl;

import com.github.pagehelper.PageInfo;
import com.zhongy.schedule.dao.DailyMapper;
import com.zhongy.schedule.pojo.Daily;
import com.zhongy.schedule.pojo.DailyDetail;
import com.zhongy.schedule.service.DailyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class DailyServiceImpl implements DailyService {

    @Autowired
    private DailyMapper dailyMapper;

    @Override
    public PageInfo<Daily> findPage(Daily daily, int page, int size) {
        return null;
    }

    @Override
    public PageInfo<Daily> findPage(int page, int size) {
        return null;
    }

    @Override
    public List<Daily> findList(Daily daily) {
        return null;
    }

    @Override
    public void delete(Integer id) {

    }

    @Override
    public void update(Daily daily) {
        dailyMapper.updateByPrimaryKeySelective(daily);
    }

    @Override
    public void add(Daily daily) {
        dailyMapper.insert(daily);
    }

    @Override
    public Daily findById(Integer id) {
        Daily daily = dailyMapper.selectByPrimaryKey(id);
        return daily;
    }

    @Override
    public List<Daily> findAll() {
        return null;
    }

    @Override
    public List<DailyDetail> findTodayAll(String name, String todayTime) {
        List<DailyDetail> list = dailyMapper.findTodayAll(name, todayTime);
        return list;
    }


    @Override
    public Daily findByUsername(String username) {
        Daily daily = dailyMapper.findByUsername(username);
        return daily;
    }

    @Override
    public Daily findToday(String name) {
        Daily today = dailyMapper.findToday(name);
        return today;
    }

    @Override
    public List<Integer> selectCount(String username, Integer id) {
        return dailyMapper.findCounts(username, id);
    }

}
