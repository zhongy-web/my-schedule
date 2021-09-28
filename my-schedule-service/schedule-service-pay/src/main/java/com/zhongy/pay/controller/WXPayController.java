package com.zhongy.pay.controller;


import com.alibaba.fastjson.JSON;
import com.github.wxpay.sdk.WXPayUtil;
import com.zhongy.pay.service.WXPayService;
import entity.Result;
import entity.StatusCode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.util.Map;

@RestController
@RequestMapping("/wxPay")
public class WXPayController {

    @Autowired
    private WXPayService wxPayService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 支付结果通知回调方法
     */
    @RequestMapping("/notify/url")
    public String notifyURL(HttpServletRequest request) throws Exception {
        //获取网络输入流
        ServletInputStream is = request.getInputStream();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = is.read(buffer)) != -1) {
            baos.write(buffer,0, len);
        }

        //微信支付结果的字节数组
        byte[] bytes = baos.toByteArray();
        String xmlresult = new String(bytes, "UTF-8");

        //XML字符串-》Map
        Map<String,String> resultMap = WXPayUtil.xmlToMap(xmlresult);
        System.out.println(resultMap);

        //发送支付结果给MQ
        rabbitTemplate.convertAndSend("exchange.order", "queue.order", JSON.toJSONString(resultMap));

        String result = "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
        return result;
    }

    /**
     * 微信支付状态查询
     */
    @GetMapping("/query/status")
    public Result queryStatus(String outtradeno) {
        //查询支付状态
        Map map = wxPayService.queryStatus(outtradeno);
        return new Result(true, StatusCode.OK,"查询成功", map);
    }

    /**
     * 创建支付二维码
     */

    @PostMapping("/create/native")
    public Result createNative(@RequestBody Map<String,String> paramMap) {
        //创建二维码
        Map<String,String> resultMap = wxPayService.createNative(paramMap);
        return new Result(true, StatusCode.OK, "创建二维码预付订单成功", resultMap);
    }
}

