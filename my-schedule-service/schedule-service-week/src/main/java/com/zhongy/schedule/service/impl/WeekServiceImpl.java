package com.zhongy.schedule.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhongy.schedule.dao.DailyDetailMapper;
import com.zhongy.schedule.dao.DailyMapper;
import com.zhongy.schedule.dao.WeekMapper;
import com.zhongy.schedule.pojo.Daily;
import com.zhongy.schedule.pojo.DailyDetail;
import com.zhongy.schedule.pojo.Week;
import com.zhongy.schedule.provider.UpdateBatchByPrimaryKeySelectiveMapper;
import com.zhongy.schedule.service.WeekService;
import entity.DateUtil;
import entity.DateUtils;
import entity.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

@Service
public class WeekServiceImpl implements WeekService {

    @Autowired
    private WeekMapper weekMapper;

    @Autowired
    private DailyMapper dailyMapper;

    @Autowired
    private DailyDetailMapper dailyDetailMapper;


    @Override
    public PageInfo<Week> findPage(Week week, int page, int size) {
        return null;
    }

    @Override
    public PageInfo<Week> findPage(int page, int size) {
        //静态分页
        PageHelper.startPage(page,size);
        //分页查询
        return new PageInfo<Week>(weekMapper.selectAll());
    }

    @Override
    public List<Week> findList(Week week) {
        return null;
    }

    @Override
    public void delete(Integer id) {
        weekMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void update(Week week) {
        weekMapper.updateByPrimaryKeySelective(week);
    }

    //TODO 没有必要写日期，会造成大量冗余，直接查就可以了 && 每日表可以使用批量插入的方式
    @Override
    public void add(Week week) {
        try {
            //计算本周周一和周日的日期范围
            Date date = new Date();
            String format_time = DateUtil.date2Str(date);
            Map<String, String> dateMap = DateUtil.getMonAndSun(format_time, "yyyy-MM-dd");

            String username = JwtUtil.getUserInfo();

            String[] strings = {dateMap.get("monday"), dateMap.get("tuesday"),
                    dateMap.get("wednesday"), dateMap.get("thursday"), dateMap.get("friday"),
                    dateMap.get("saturday"), dateMap.get("sunday")};

            String sunday = dateMap.get("sunday");
            Date sun = DateUtils.parseString2Date(sunday);
            week.setSunday_date(sun);
            weekMapper.insertSelective(week);

            List<Daily> dailies = new ArrayList<>();
            //添加周计划表的同时将这周所涵盖的每日计划表添加
            for (int i = 0; i < strings.length; i++) {
                Daily daily = new Daily();
                Date new_date = DateUtils.parseString2Date(strings[i]);
                daily.setTodayTime(new_date);
                daily.setName(username);
                daily.setWeek_id(week.getId());
                daily.setCount(0);
                dailies.add(daily);
            }
            dailyMapper.insertList(dailies); //将daily放入集合中，实现批量插入

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Week findById(Integer id) {
        Week week = weekMapper.selectByPrimaryKey(id);
        return week;
    }

    @Override
    public List<Week> findAll() {
        return null;
    }

    @Override
    public Week findByName(String name) {
        try {
            Week week = weekMapper.findByNameAndSun(name);
            return week;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 查询本周所有的每日任务id集合
     */
    @Override
    public List<Integer> findAll2ListId(Integer id, String name) {
        return weekMapper.findAll2ListId(id, name);
    }

    /**
     * 根据每日任务集合 查询每天的任务详细
     */
    @Override
    public List<Map<Integer, List>> findThisWeekAll(String name) {
        //创建集合 装每日任务
        List<Map<Integer, List>> list = new ArrayList<>();
        //创建Map 装每日任务详细
        Map<Integer, List> map = new HashMap<>();

        //先找到这周week的id
        Week week = findByName(name);
        if (week != null) {
            int w_id = week.getId();
            List<Integer> listId = findAll2ListId(w_id, name);
            /**
             * 遍历id集合找到所有的每日任务明细
             */
            for (Integer id : listId) {
                Daily daily = dailyMapper.selectByPrimaryKey(id);
                List<DailyDetail> dailyDetailList = dailyMapper.findTodayAll(name, DateUtil.date2Str(daily.getTodayTime()));
                map.put(id, dailyDetailList);
            }
            list.add(map);
            return list;
        }
        return null;
    }

    /**
     * 一键清除所有任务功能
     * @param username
     */
    @Override
    public void clear(String username) {
        Integer id = weekMapper.findIdByName(username);
        List<Integer> dailyIds = findAll2ListId(id, username);
        Daily daily = new Daily();
        daily.setWeek_id(id);
        List<Daily> dailyList = dailyMapper.select(daily);
        //将count清零
        for (Daily d : dailyList) {
            d.setCount(0);
            dailyMapper.updateByPrimaryKey(d);
        }
        //删掉所有的dailydetail
        for (Integer dailyId : dailyIds) {
            dailyDetailMapper.deleteByDailyId(dailyId);
        }
    }

    /**
     * 将redis中存储的上周数据一键复原
     * @param username
     */
    @Override
    public Integer recover(String username) {
        clear(username);//先将这周数据清空，再进行还原

        Week lastWeek = weekMapper.findLastWeek(username);
        Week week = weekMapper.findByNameAndSun(username);
        if (lastWeek == null) {
            return 0;
        }

        //找到上周每天的id集合
        List<Integer> lastDailyIds = weekMapper.findAll2ListId(lastWeek.getId(), username);
        List<Integer> dailyIds = weekMapper.findAll2ListId(week.getId(), username);

        int count = 0;
        for (Integer lastDailyId : lastDailyIds) {

            List<DailyDetail> details = dailyDetailMapper.findByDailyId(lastDailyId);
            if (details.size() == 0) {
                count++;
                continue;
            }
            for (DailyDetail detail : details) {
                //消息提醒模式
                if (week.getMsg_remind() == 1) {
                    detail.setMsgStatus(1);
                } else {
                    detail.setMsgStatus(0);
                }
                //自动完成模式
                if (week.getAuto_complete() == 1) {
                    detail.setAutoStatus(1);
                } else {
                    detail.setAutoStatus(0);
                }
                detail.setId(null);
                detail.setMsgVersion(0);
                detail.setHasDone("0");
                detail.setDaily_id(dailyIds.get(count));
            }
            dailyDetailMapper.insertList(details); //批量处理
            count++;//对下一组数据
        }
        return 1;
    }

    /**
     * 开启消息提醒功能
     * @return
     */
    @Override
    public List<DailyDetail> openMsgRemind(Integer id) {
        String username = JwtUtil.getUserInfo();
        Week week = weekMapper.findByNameAndSun(username);
        Integer msg_remind = week.getMsg_remind();
        List<DailyDetail> details = dailyDetailMapper.findDetails(id, username);
        if (msg_remind == 0) { // 开启
            week.setMsg_remind(1);
            weekMapper.updateByPrimaryKeySelective(week);

            if (details == null) {
                return null;
            }
            for (DailyDetail detail : details) {
                detail.setMsgStatus(1);
                detail.setMsgVersion(detail.getMsgVersion() + 1);
            }
        }
        return details;
    }

    /**
     * 关闭消息提醒
     */
    @Override
    public Integer closeMsgRemind(Integer id) {
        Week week = weekMapper.selectByPrimaryKey(id);
        Integer msg_remind = week.getMsg_remind();
        String username = JwtUtil.getUserInfo();
        List<DailyDetail> details = dailyDetailMapper.findDetails(id, username);

        if (msg_remind != 0) {
            week.setMsg_remind(2); //现阶段技术，只让他开启一次，不然会一直往rabbitmq里生产信息。
            weekMapper.updateByPrimaryKeySelective(week);

            if (details.size() == 0) {
                return 1;
            }
            for (DailyDetail detail : details) {
                detail.setMsgStatus(0);
            }
        }
        dailyDetailMapper.updateBatchByPrimaryKeySelective(details);
        return 1;
    }

    @Override
    public List<DailyDetail> openAutoComplete(Integer id, String username) {
        Week week = weekMapper.findByNameAndSun(username);
        Integer autoComplete = week.getAuto_complete();
        List<DailyDetail> details = dailyDetailMapper.findDetails(id, username);
        if (autoComplete == 0) { // 开启
            week.setAuto_complete(1);
            weekMapper.updateByPrimaryKeySelective(week);

            if (details == null) {
                return null;
            }
            for (DailyDetail detail : details) {
                detail.setAutoStatus(1);
                detail.setAutoVersion(detail.getAutoVersion() + 1);
            }
        }
        return details;
    }

    @Override
    public Integer closeAutoComplete(Integer id, String username) {
        Week week = weekMapper.selectByPrimaryKey(id);
        Integer autoComplete = week.getAuto_complete();
        List<DailyDetail> details = dailyDetailMapper.findDetails(id, username);

        if (autoComplete != 0) {
            week.setAuto_complete(2); //现阶段技术，只让他开启一次，不然会一直往rabbitmq里生产信息。
            weekMapper.updateByPrimaryKeySelective(week);

            if (details.size() == 0) {
                return 1;
            }
            for (DailyDetail detail : details) {
                detail.setAutoStatus(0);
            }
        }
        dailyDetailMapper.updateBatchByPrimaryKeySelective(details);
        return 1;
    }


    /**
     * Week构建查询对象
     * @param week
     * @return
     */
    public Example createExample(Week week){
        Example example=new Example(DailyDetail.class);
        Example.Criteria criteria = example.createCriteria();
        if(week!=null){
            // ID
            if(!StringUtils.isEmpty(week.getId())){
                criteria.andLike("id","%"+week.getId()+"%");
            }
            // 属于谁的计划
            if(!StringUtils.isEmpty(week.getName())){
                criteria.andEqualTo("name",week.getName());
            }
            // 本周星期日的日期
            if(!StringUtils.isEmpty(week.getSunday_date())){
                criteria.andEqualTo("sunday_date",week.getSunday_date());
            }
        }
        return example;
    }
}
