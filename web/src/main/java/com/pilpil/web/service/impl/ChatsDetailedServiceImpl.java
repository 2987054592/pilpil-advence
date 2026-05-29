package com.pilpil.web.service.impl;


import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.pilpil.common.entity.UserInfo;
import com.pilpil.common.entity.po.ChatsDetailed;
import com.pilpil.common.entity.po.User;
import com.pilpil.common.enums.ChatType;
import com.pilpil.common.enums.StatusType;
import com.pilpil.common.exception.illegalException;
import com.pilpil.common.utils.UserHolder;
import com.pilpil.web.entity.dto.ChatDtoWeb;
import com.pilpil.web.entity.dto.DanmuDto;
import com.pilpil.web.entity.vo.WebDanmuVo;
import com.pilpil.web.mapper.ChatsDetailedMapper;
import com.pilpil.web.service.IChatsDetailedService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pilpil.web.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.pilpil.common.constants.Exception.exceptionConstants.User.LOGIN_EXPIRE;
import static com.pilpil.common.constants.Exception.exceptionConstants.User.USER_STATUS_ERROR;
import static com.pilpil.common.constants.redis.redisContanst.User.LOGIN_TOKEN_PREFIX;

/**
 * <p>
 * 聊天详情表 服务实现类
 * </p>
 *
 * @author 
 * @since 2026-05-29
 */
@Service
@RequiredArgsConstructor
public class ChatsDetailedServiceImpl extends ServiceImpl<ChatsDetailedMapper, ChatsDetailed> implements IChatsDetailedService {
    private final StringRedisTemplate redisTemplate;
    private final IUserService userService;
    @Override
    public void saveChat(ChatDtoWeb bean, Long id) {
        Long targetId = bean.getTargetId();
        String content = bean.getContent();
        ChatsDetailed chatsDetailed = new ChatsDetailed();
        chatsDetailed.setContent(content);
        chatsDetailed.setTargetId(targetId);
        chatsDetailed.setUserId(id);
        chatsDetailed.setTime(LocalDateTime.now());
        save(chatsDetailed);
    }

    @Override
    public Map<ChatType,List<ChatsDetailed>> listRecord(Long targetId) {
        Long userId = UserHolder.get().getId();
        User user1 = userService.getById(userId);
        if(user1.getStatus().equals(StatusType.BAN)){
            throw new illegalException(USER_STATUS_ERROR);
        }
        List<ChatsDetailed> list = lambdaQuery()
                .and(w -> w.eq(ChatsDetailed::getUserId, userId)
                        .eq(ChatsDetailed::getTargetId, targetId)
                        .or()
                        .eq(ChatsDetailed::getTargetId, userId)
                        .eq(ChatsDetailed::getUserId, targetId)
                ).orderByAsc(ChatsDetailed::getTime).list();


        List<ChatsDetailed> meList = new ArrayList<>();
        List<ChatsDetailed> otherList = new ArrayList<>();
        for(ChatsDetailed chatsDetailed : list){
            if(chatsDetailed.getUserId().equals(userId)){
                meList.add(chatsDetailed);
            }else{
                otherList.add(chatsDetailed);
            }

        }
        return Map.of(ChatType.ME,meList,ChatType.OTHER,otherList);
    }
}
