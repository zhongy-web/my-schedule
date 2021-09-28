package com.zhongy.schedule.service;

import com.zhongy.schedule.pojo.DailyDetail;

public interface ProduceService {
    boolean saveOrUpdatePlan(DailyDetail dailyDetail);
    boolean autoFinishPlan(DailyDetail dailyDetail);
}
