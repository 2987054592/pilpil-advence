package com.pilpil.web.mq;

import com.pilpil.common.constants.mq.mqConstans;
import com.pilpil.common.entity.po.VideoDoc;
import com.pilpil.common.utils.Escommpent;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VideoMq {
    @Autowired
    private Escommpent escommpent;
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = mqConstans.Queue.VIDEO_QUEUE, durable = "true"),
                    exchange = @Exchange(value = mqConstans.Exchange.VIDEO_EXCHANGE, durable = "true"),
                    key = mqConstans.Key.VIDEO_KEY
            )
    )
    public void receive(VideoDoc videoDoc) {
        escommpent.save(videoDoc);
    }

}
