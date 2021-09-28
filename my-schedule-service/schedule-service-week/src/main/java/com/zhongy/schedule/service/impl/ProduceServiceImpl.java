package com.zhongy.schedule.service.impl;

import com.zhongy.schedule.dao.DailyDetailMapper;
import com.zhongy.schedule.dao.DailyMapper;
import com.zhongy.schedule.mq.DelayMQConfig;
import com.zhongy.schedule.pojo.Daily;
import com.zhongy.schedule.pojo.DailyDetail;
import com.zhongy.schedule.service.ProduceService;
import entity.DateUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class ProduceServiceImpl implements ProduceService {
    @Autowired
    private DailyDetailMapper dailyDetailMapper;
    @Autowired
    private DailyMapper dailyMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public boolean saveOrUpdatePlan(DailyDetail dailyDetail) {
        try {
            //判断更新还是新增
            Integer id = dailyDetail.getId();
            if (id != null) {
//                这里可以前端传数据，也可以我这边改
                Integer old = dailyDetail.getMsgVersion();
                dailyDetail.setMsgVersion(old + 1);
                dailyDetailMapper.updateByPrimaryKeySelective(dailyDetail);
            } else {
                //新增 初始化版本号
                dailyDetailMapper.insert(dailyDetail);
            }
            //获取数据库最新数据
            DailyDetail newDailyDetail = dailyDetailMapper.selectByPrimaryKey(dailyDetail.getId());
            //判断开始时间是不是比当前时间大且消息提醒是生效
            Daily daily = dailyMapper.selectByPrimaryKey(dailyDetail.getDaily_id());
            Date todayTime = daily.getTodayTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dailyTime = sdf.format(todayTime);
            //计算需要延时多久发送
            long x = (DateUtils.parseString2Date(dailyTime + " " + newDailyDetail.getStart_time(), "yyyy-MM-dd HH:mm:ss").getTime() - System.currentTimeMillis());
            if (x > 0 && dailyDetail.getMsgStatus() == 1 && dailyDetail.getHasDone().equals("0")) {
                //计划加入消息队列中
                rabbitTemplate.convertAndSend(DelayMQConfig.DELAY_EXCHANGE_NAME, DelayMQConfig.DELAY_ROUTE_KEY, newDailyDetail, msg -> {
                    //设置延迟
                    msg.getMessageProperties().setDelay((int) x);
                    return msg;
                });
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean autoFinishPlan(DailyDetail dailyDetail) {
        try {
            //判断更新还是新增
            Integer id = dailyDetail.getId();
            if (id != null) {
//                这里可以前端传数据，也可以我这边改
                Integer old = dailyDetail.getAutoVersion();
                dailyDetail.setAutoVersion(old + 1);
                dailyDetailMapper.updateByPrimaryKeySelective(dailyDetail);
            } else {
                //新增 初始化版本号
                dailyDetailMapper.insert(dailyDetail);
            }
            //获取数据库最新数据
            DailyDetail newDailyDetail = dailyDetailMapper.selectByPrimaryKey(dailyDetail.getId());
            //判断开始时间是不是比当前时间大且消息提醒是生效
            Daily daily = dailyMapper.selectByPrimaryKey(dailyDetail.getDaily_id());
            Date todayTime = daily.getTodayTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dailyTime = sdf.format(todayTime);
            //计算需要延时多久发送
            long x = (DateUtils.parseString2Date(dailyTime + " " + newDailyDetail.getEnd_time(), "yyyy-MM-dd HH:mm:ss").getTime() - System.currentTimeMillis());
            if (x > 0 && dailyDetail.getAutoStatus() == 1 && dailyDetail.getHasDone().equals("0")) {
                //计划加入消息队列中
                rabbitTemplate.convertAndSend(DelayMQConfig.AUTO_DELAY_EXCHANGE_NAME, DelayMQConfig.DELAY_ROUTE_KEY, newDailyDetail, msg -> {
                    //设置延迟
                    msg.getMessageProperties().setDelay((int) x);
                    return msg;
                });
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
