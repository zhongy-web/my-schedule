package com.zhongy.pay.service;

import java.util.Map;

public interface WXPayService {

    /**
     * 获取二维码
     */
    Map createNative(Map<String,String> parameterMap);

    Map queryStatus(String outtradeno);
}
