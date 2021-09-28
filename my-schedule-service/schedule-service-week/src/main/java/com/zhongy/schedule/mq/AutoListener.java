package com.zhongy.schedule.mq;

import com.rabbitmq.client.Channel;
import com.zhongy.schedule.pojo.DailyDetail;
import com.zhongy.schedule.service.DailyDetailService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 用于监听自动完成的队列
 */
@Component
public class AutoListener {

    @Autowired
    DailyDetailService dailyDetailService;

    //此处绑定要监听的队列
    @RabbitListener(queues = DelayMQConfig.AUTO_DELAY_QUEUE_NAME)
    @RabbitHandler
    public void onMessage(DailyDetail msg, Message message, Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            //通知用户
            dailyDetailService.autoFinish(msg);
            channel.basicAck(deliveryTag, false);
//            System.out.println("消息被确认！");
        } catch (IOException e) {
            channel.basicNack(deliveryTag, false, false);
//            System.out.println("消息被否定确认！");
        }
    }
}
