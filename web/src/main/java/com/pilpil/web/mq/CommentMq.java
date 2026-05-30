package com.pilpil.web.mq;

import com.pilpil.common.constants.mq.mqConstans;
import com.pilpil.common.entity.dto.VideoFansMq;
import com.pilpil.common.entity.po.VideoDoc;
import com.pilpil.web.service.IUserNoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentMq {
    private final IUserNoticeService userNoticeService;
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = mqConstans.Queue.VIDEO_COMMENT_QUEUE, durable = "true"),
                    exchange = @Exchange(value = mqConstans.Exchange.VIDEO_COMMENT_EXCHANGE, durable = "true"),
                    key = mqConstans.Key.VIDEO_COMMENT_KEY
            )
    )
    public void receive(VideoFansMq fansMq) {
        userNoticeService.noticeVideoComment(fansMq);
    }
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = mqConstans.Queue.COMMENT_REPLAY_QUEUE, durable = "true"),
                    exchange = @Exchange(value = mqConstans.Exchange.COMMENT_REPLAY_EXCHANGE, durable = "true"),
                    key = mqConstans.Key.COMMENT_REPLAY_KEY
            )
    )
    public void receiveComment(VideoFansMq fansMq) {
        userNoticeService.receiveComment(fansMq);
    }

}
