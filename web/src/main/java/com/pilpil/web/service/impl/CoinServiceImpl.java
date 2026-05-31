package com.pilpil.web.service.impl;


import cn.hutool.core.date.DateUtil;
import com.pilpil.common.entity.po.Coin;
import com.pilpil.common.entity.po.PointRecord;
import com.pilpil.common.entity.po.User;
import com.pilpil.common.entity.vo.ExperienceVo;
import com.pilpil.common.enums.CoinType;
import com.pilpil.common.enums.PointType;
import com.pilpil.common.exception.illegalException;
import com.pilpil.common.utils.UserHolder;
import com.pilpil.web.entity.dto.CoinDto;
import com.pilpil.web.mapper.CoinMapper;
import com.pilpil.web.service.ICoinService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pilpil.web.service.IPointRecordService;
import com.pilpil.web.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import static com.pilpil.common.constants.Exception.exceptionConstants.Coin.COIN_EXIST;
import static com.pilpil.common.constants.Exception.exceptionConstants.Coin.COIN_NOT_ENOUGH;
import static com.pilpil.common.constants.mq.mqConstans.Exchange.EXPERIENCE_EXCHANGE;
import static com.pilpil.common.constants.mq.mqConstans.Key.EXPERIENCE_KEY;
import static com.pilpil.common.constants.redis.redisContanst.Coin.COIN_INCRE_PREFIX;

/**
 * <p>
 * 投币表 服务实现类
 * </p>
 *
 * @author 
 * @since 2026-05-27
 */
@Service
@RequiredArgsConstructor
public class CoinServiceImpl extends ServiceImpl<CoinMapper, Coin> implements ICoinService {
    private final StringRedisTemplate redisTemplate;
    private final IUserService userService;
    private final RabbitTemplate rabbitTemplate;
    private final IPointRecordService pointRecordService;
    @Override
    public void saveCoin(CoinDto coinDto) {
        Integer videoId = coinDto.getVideoId();
        Integer code = coinDto.getNumber().getCode();
        Long UserId = UserHolder.get().getId();
        boolean result = userService.lambdaUpdate()
                .eq(User::getId, UserId)
                .ge(User::getCurrentCoin, code)
                .setSql("current_coin = current_coin - " + code)
                .update();
        if(!result){
            throw new illegalException(COIN_NOT_ENOUGH);
        }
        Coin exist = lambdaQuery().eq(Coin::getVideoId, videoId)
                .eq(Coin::getUserId, UserId)
                .one();
        if(exist!=null){
            throw new illegalException(COIN_EXIST);
        }
        Coin coin = Coin.builder()
                .videoId(videoId)
                .userId(UserId)
                .number(coinDto.getNumber())
                .build();
        save(coin);
        String key=COIN_INCRE_PREFIX+videoId;
        redisTemplate.opsForHash().increment(key,videoId.toString(),code);

        ExperienceVo experienceVo = new ExperienceVo();

        int experience = 0;
        if (coinDto.getNumber().equals(CoinType.ONE)) {
            experience = 10;
        }else{
            experience = 20;
        }
            experienceVo.setUserId(UserId);
            experienceVo.setExperience(experience);
        experienceVo.setPointType(PointType.COIN);
            rabbitTemplate.convertAndSend(
                    EXPERIENCE_EXCHANGE,
                    EXPERIENCE_KEY,
                    experienceVo
            );
    }

    @Override
    public Integer getCoin(Integer videoId) {
        Long userId = UserHolder.get().getId();
        Coin coin = lambdaQuery().eq(Coin::getVideoId, videoId)
                .eq(Coin::getUserId, userId)
                .one();
        if(coin==null){
            return 0;
        }else{
            return coin.getNumber().getCode();
        }

    }
}
