package com.pilpil.web.service.impl;

import com.pilpil.common.entity.po.PointRecord;
import com.pilpil.common.entity.po.User;
import com.pilpil.common.entity.vo.ExperienceVo;
import com.pilpil.common.enums.PointType;
import com.pilpil.common.exception.illegalException;
import com.pilpil.common.utils.UserHolder;
import com.pilpil.web.entity.vo.SignRecordVo;
import com.pilpil.web.entity.vo.SignVo;
import com.pilpil.web.service.IPointRecordService;
import com.pilpil.web.service.IUserService;
import com.pilpil.web.service.IsignService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.pilpil.common.constants.Exception.exceptionConstants.Sign.SIGN_EXIST;
import static com.pilpil.common.constants.mq.mqConstans.Exchange.EXPERIENCE_EXCHANGE;
import static com.pilpil.common.constants.mq.mqConstans.Key.EXPERIENCE_KEY;
import static com.pilpil.common.constants.redis.redisContanst.Sign.SIGN_PREFIX;

@Service
@RequiredArgsConstructor
public class SignServiceImpl implements IsignService {
    private final StringRedisTemplate redisTemplate;
    private final RabbitTemplate rabbitTemplate;
    private final IUserService userService;
    private final static DateTimeFormatter  dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMM");
    @Override
    public SignVo saveSign() {
        Long userId = UserHolder.get().getId();
        LocalDate now = LocalDate.now();
        String timeFormat = now.format(dateTimeFormatter);
        String key=SIGN_PREFIX+userId+":"+timeFormat;
        Boolean b = redisTemplate.opsForValue().setBit(key, now.getDayOfMonth() - 1, true);

        if(Boolean.TRUE.equals(b)){
            throw new illegalException(SIGN_EXIST);
        }

        int daysCount=countSigDays(key,now.getDayOfMonth());
        int rewardPoint=0;
        switch (daysCount){
            case 7:
                rewardPoint=10;
                break;
            case 14:
                rewardPoint=20;
                break;
                case 28:
                    rewardPoint=40;
                    break;
        }
        SignVo build = SignVo.builder()
                .rewardPoint(rewardPoint)
                .signDays(daysCount)
                .build();

        ExperienceVo experienceVo = new ExperienceVo();
        experienceVo.setExperience(build.totoalPoint());
        experienceVo.setUserId(userId);
        experienceVo.setPointType(PointType.SIGN_IN);
        rabbitTemplate.convertAndSend(
                EXPERIENCE_EXCHANGE,
                EXPERIENCE_KEY,
                experienceVo
        );



        return build;


    }

    private int countSigDays(String key, int dayOfMonth) {
        List<Long> longs = redisTemplate.opsForValue()
                .bitField(key, BitFieldSubCommands.create().get(BitFieldSubCommands.BitFieldType.unsigned(dayOfMonth)).valueAt(0));
        if(longs==null||longs.isEmpty()){
            return 0;
        }
        Long num = longs.get(0);
        int count=0;
        while((num & 1)==1){
            count++;
            num>>=1;
        }
        return count;
    }

    @Override
    public SignRecordVo getRecord() {
        Long userId = UserHolder.get().getId();
        LocalDate now = LocalDate.now();
        SignRecordVo signRecordVo = new SignRecordVo();
        String timeFormat = now.format(dateTimeFormatter);
        String key=SIGN_PREFIX+userId+":"+timeFormat;
        int daysCount=countSigDays(key,now.getDayOfMonth());
        signRecordVo.setDays(daysCount);

        List<Long> longs = redisTemplate.opsForValue()
                .bitField(key, BitFieldSubCommands.create().get(BitFieldSubCommands.BitFieldType.unsigned(now.getDayOfMonth())).valueAt(0));
        if(longs==null||longs.isEmpty()){
            signRecordVo.setSignRecords(Collections.emptyList());
            return signRecordVo;
        }
        Long l = longs.get(0);
        List<Byte> signRecords = new ArrayList<>(LocalDate.now().getDayOfMonth());
        for (int i = 0; i < now.getDayOfMonth(); i++) {
                signRecords.add((byte) (l & 1));
                l>>>=1;
        }
        Collections.reverse(signRecords);
        signRecordVo.setSignRecords(signRecords);

        return signRecordVo;

    }
}
