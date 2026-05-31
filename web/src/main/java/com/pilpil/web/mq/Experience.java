package com.pilpil.web.mq;

import com.pilpil.common.entity.po.User;
import com.pilpil.common.entity.vo.ExperienceVo;
import com.pilpil.web.service.IPointRecordService;
import com.pilpil.web.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static com.pilpil.common.constants.mq.mqConstans.Exchange.EXPERIENCE_EXCHANGE;
import static com.pilpil.common.constants.mq.mqConstans.Key.EXPERIENCE_KEY;
import static com.pilpil.common.constants.mq.mqConstans.Queue.EXPERIENCE_QUEUE;

@Component
@RequiredArgsConstructor
public class Experience {
    private final IUserService userService;




    @RabbitListener(bindings =
            @QueueBinding(
                    value = @Queue(value = EXPERIENCE_QUEUE, durable = "true"),
                    exchange = @Exchange(value = EXPERIENCE_EXCHANGE, durable = "true"),
                    key = EXPERIENCE_KEY
            )
    )
    public void sendExperience(ExperienceVo experienceVo) {
        userService.ExperienceExchange(experienceVo);
    }

}
