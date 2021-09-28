package com.zhongy.pay.service.impl;

import com.github.wxpay.sdk.WXPayUtil;
import com.zhongy.pay.service.WXPayService;
import entity.HttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class WXPayServiceImpl implements WXPayService {

    @Value("${weixin.appid}")
    private String appid;

    @Value("${weixin.partner}")
    private String partner;

    @Value("${weixin.partnerkey}")
    private String partnerkey;

    @Value("${weixin.notifyurl}")
    private String notifyurl;

    @Override
    public Map createNative(Map<String, String> parameterMap) {
        try {
            //参数
            Map<String,String> paramMap = new HashMap<>();
            paramMap.put("appid", appid);
            paramMap.put("mch_id", partner);
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
            paramMap.put("body", "good");
            //订单号
            paramMap.put("out_trade_no", parameterMap.get("outtradeno"));
            //交易金额
            paramMap.put("total_fee", parameterMap.get("totalfee"));

            //终端地址
            parameterMap.put("spbill_create_ip", "127.0.0.1");

            //交易结果回调通知地址
            paramMap.put("notify_url", notifyurl);

            //交易类型
            paramMap.put("trade_type", "NATIVE");

            //Map转成XML字符串，可以携带签名
            String generateSignedXml = WXPayUtil.generateSignedXml(paramMap, partnerkey);

            //URL地址
            String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
            HttpClient httpClient = new HttpClient(url);

            //提交方式
            httpClient.setHttps(true);

            //请求参数
            httpClient.setXmlParam(generateSignedXml);

            //提交参数
            httpClient.post();

            //获取返回的数据
            String content = httpClient.getContent();

            //返回数据转成Map
            Map<String, String> stringMap = WXPayUtil.xmlToMap(content);
            return stringMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Map queryStatus(String outtradeno) {
        try {
            //参数
            Map<String,String> paramMap = new HashMap<>();
            paramMap.put("appid", appid);
            paramMap.put("mch_id", partner);
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
            //订单号
            paramMap.put("out_trade_no", outtradeno);

            //Map转成XML字符串，可以携带签名
            String generateSignedXml = WXPayUtil.generateSignedXml(paramMap, partnerkey);

            //URL地址
            String url = "https://api.mch.weixin.qq.com/pay/orderquery";
            HttpClient httpClient = new HttpClient(url);

            //提交方式
            httpClient.setHttps(true);

            //请求参数
            httpClient.setXmlParam(generateSignedXml);

            //提交参数
            httpClient.post();

            //获取返回的数据
            String content = httpClient.getContent();

            //返回数据转成Map
            Map<String, String> stringMap = WXPayUtil.xmlToMap(content);
            return stringMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
