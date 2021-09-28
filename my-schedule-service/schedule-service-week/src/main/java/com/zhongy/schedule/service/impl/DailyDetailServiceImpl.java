package com.zhongy.schedule.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhongy.schedule.dao.DailyDetailMapper;
import com.zhongy.schedule.dao.DailyMapper;
import com.zhongy.schedule.dao.WeekMapper;
import com.zhongy.schedule.pojo.Daily;
import com.zhongy.schedule.pojo.DailyDetail;
import com.zhongy.schedule.pojo.Type;
import com.zhongy.schedule.pojo.Week;
import com.zhongy.schedule.service.DailyDetailService;
import com.zhongy.schedule.service.ProduceService;
import com.zhongy.schedule.util.SendMail;
import com.zhongy.user.feign.UserFeign;
import com.zhongy.user.pojo.User;
import entity.JwtUtil;
import entity.Mail;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import javax.mail.MessagingException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class DailyDetailServiceImpl implements DailyDetailService {

    @Autowired
    private DailyDetailMapper dailyDetailMapper;

    @Autowired
    private DailyMapper dailyMapper;

    @Autowired
    private WeekMapper weekMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ProduceService produceService;

    @Autowired
    private UserFeign userFeign;

    @Override
    public PageInfo<DailyDetail> findPage(DailyDetail dailyDetail, int page, int size) {
        return null;
    }

    @Override
    public PageInfo<DailyDetail> findPage(int page, int size) {
        //静态分页
        PageHelper.startPage(page,size);
        //分页查询
        return new PageInfo<DailyDetail>(dailyDetailMapper.selectAll());
    }

    @Override
    public List<DailyDetail> findList(DailyDetail dailyDetail) {
        return null;
    }

    @Override
    public void delete(Integer id) {
        DailyDetail dailyDetail = dailyDetailMapper.selectByPrimaryKey(id);
        Daily daily = dailyMapper.selectByPrimaryKey(dailyDetail.getDaily_id());
        if (daily.getCount() >= 0) {
            daily.setCount(daily.getCount() - 1);
        }
        dailyMapper.updateByPrimaryKey(daily);
        dailyDetailMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void update(DailyDetail dailyDetail) {
        //todo 这里关掉提醒就放到前端去做吧。 status设置为0就可以关掉了。
        produceService.saveOrUpdatePlan(dailyDetail);
        produceService.autoFinishPlan(dailyDetail);
    }

    /**
     * 先添加后关联
     * @param dailyDetail
     */
    @Override
    public void add(DailyDetail dailyDetail, String date, String name) throws Exception {
        //先确定是哪一天的每日计划
        Daily daily = dailyMapper.selectByDateAndName(name, date);
        dailyDetail.setDaily_id(daily.getId());
        Week week = weekMapper.findByNameAndSun(name);

        if (week.getMsg_remind() == 1) {
            dailyDetail.setMsgStatus(1);
        } else {
            dailyDetail.setMsgStatus(0);
        }

        if (week.getAuto_complete() == 1) {
            dailyDetail.setAutoStatus(1);
        } else {
            dailyDetail.setAutoStatus(0);
        }

        dailyDetail.setMsgVersion(0);
        dailyDetail.setAutoVersion(0);
        produceService.saveOrUpdatePlan(dailyDetail); //发送信息到消息提醒队列
        produceService.autoFinishPlan(dailyDetail); //发送信息到自动完成队列
    }

    @Override
    public DailyDetail findById(Integer id, Integer daily_id) {
        DailyDetail dailyDetail = dailyDetailMapper.findById(id, daily_id);
        return dailyDetail;
    }

    @Override
    public DailyDetail selectById(Integer id) {
        DailyDetail dailyDetail = dailyDetailMapper.selectByPrimaryKey(id);
        return dailyDetail;
    }

    @Override
    public List<DailyDetail> findAll() {
        return dailyDetailMapper.selectAll();
    }

    /**
     * 先查出所有类别将其放入redis中
     */
    @Override
    public List<Type> findTypes() {
        //先从redis中取，如果没有再去mysql中拿
        List<Type> types = redisTemplate.opsForList().range("types", 0, -1);
        if (types == null) {
            List<Type> types1 = dailyDetailMapper.findTypes();
            //先存入redis
            redisTemplate.opsForList().rightPushAll("types", types1);
            return types1;
        }
        return types;
    }

    /**
     * 每日饼状图
     * @param
     * @return
     */
    @Override
    public List<Map<String, Object>> pieChart() {
        String username = JwtUtil.getUserInfo();
        Daily daily = dailyMapper.findToday(username);
        if (daily != null) {
            List<Map<String, Object>> maps = dailyDetailMapper.pieChart(daily.getId());
            return maps;
        }
        return null;
    }

    /**
     * 发送消息给用户
     */
    @Override
    public void notifyUser(DailyDetail dailyDetail) {
        DailyDetail newDetail = dailyDetailMapper.selectByPrimaryKey(dailyDetail.getId());
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        Daily daily = dailyMapper.selectByPrimaryKey(dailyDetail.getDaily_id());
        String username = daily.getName();
        Result<User> result = userFeign.findById(username);
        User user = result.getData();

        if (newDetail != null &&
                newDetail.getMsgStatus() == 1 &&
                dailyDetail.getMsgVersion().equals(newDetail.getMsgVersion()) &&
                newDetail.getHasDone().equals("0")
        ) {
            //发送消息提醒
            /*
                todo  数据库8小时会自动关闭
                 com.mysql.cj.exceptions.ConnectionIsClosedException: No operations allowed after connection closed.
             */
            try {
                System.out.println("发送");
                if (user.getEmail() == null) {
                    System.out.println("你的email为空");
                    return;
                }
                SendMail.sendSchedule(user.getEmail(), dailyDetail.getContent());
            } catch (MessagingException e) {
                e.printStackTrace();
            }

            System.out.println(now + " 你消息提醒：" + dailyDetail.toString());
        } else if(newDetail == null) {
            //还需要考虑删除的情况 不然这边会报空指针异常！
            System.out.println(now + " " + "该计划已被删除！");
        } else {
            System.out.println(now + " " + dailyDetail.getContent() + ": 该消息已取消提醒");
        }
    }

    /**
     * 自动完成任务
     */
    @Override
    public void autoFinish(DailyDetail dailyDetail) {
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        DailyDetail newDetail = dailyDetailMapper.selectByPrimaryKey(dailyDetail.getId());

        if (newDetail != null &&
                newDetail.getAutoStatus() == 1 &&
                dailyDetail.getAutoVersion().equals(newDetail.getAutoVersion()) &&
                newDetail.getHasDone().equals("0")
        ) {
            //完成任务
            newDetail.setHasDone("1");
            dailyDetailMapper.updateByPrimaryKeySelective(newDetail);
            Daily daily = dailyMapper.selectByPrimaryKey(newDetail.getDaily_id());
            daily.setCount(daily.getCount() + 1);
            dailyMapper.updateByPrimaryKeySelective(daily);
            System.out.println(now + " 你已经完成：" + newDetail.toString());
        } else if(newDetail == null) {
            //还需要考虑删除的情况 不然这边会报空指针异常！
            System.out.println(now + " " + "该计划已被删除！");
        } else {
            System.out.println(now + " " + dailyDetail.getContent() + ": 该消息已取消提醒");
        }
    }

    /**
     * DailyDetail构建查询对象
     * @param dailyDetail
     * @return
     */
    public Example createExample(DailyDetail dailyDetail){
        Example example=new Example(DailyDetail.class);
        Example.Criteria criteria = example.createCriteria();
        if(dailyDetail!=null){
            // 用户名
            if(!StringUtils.isEmpty(dailyDetail.getId())){
                criteria.andLike("id","%"+dailyDetail.getId()+"%");
            }
            // 计划内容
            if(!StringUtils.isEmpty(dailyDetail.getContent())){
                criteria.andEqualTo("content",dailyDetail.getContent());
            }
            // 是否完成
            if(!StringUtils.isEmpty(dailyDetail.getHasDone())){
                criteria.andEqualTo("hasDone",dailyDetail.getHasDone());
            }
            // 任务类别
            if(!StringUtils.isEmpty(dailyDetail.getType())){
                criteria.andEqualTo("type",dailyDetail.getType());
            }
            // daily_id
            if(!StringUtils.isEmpty(dailyDetail.getDaily_id())){
                criteria.andEqualTo("daily_id",dailyDetail.getDaily_id());
            }
            // 开始时间
            if(!StringUtils.isEmpty(dailyDetail.getStart_time())){
                criteria.andEqualTo("start_time",dailyDetail.getStart_time());
            }
            // 结束时间
            if(!StringUtils.isEmpty(dailyDetail.getEnd_time())){
                criteria.andEqualTo("end_time",dailyDetail.getEnd_time());
            }
        }
        return example;
    }
}
