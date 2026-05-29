package com.pilpil.web.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.pilpil.common.entity.UserInfo;
import com.pilpil.common.entity.po.Chats;
import com.pilpil.common.entity.po.User;
import com.pilpil.common.enums.StatusType;
import com.pilpil.common.exception.illegalException;
import com.pilpil.common.utils.UserHolder;
import com.pilpil.web.entity.dto.ChatDtoWeb;
import com.pilpil.web.entity.vo.ChatsVo;
import com.pilpil.web.mapper.ChatsMapper;
import com.pilpil.web.service.IChatsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pilpil.web.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.pilpil.common.constants.Exception.exceptionConstants.User.LOGIN_EXPIRE;
import static com.pilpil.common.constants.Exception.exceptionConstants.User.USER_STATUS_ERROR;
import static com.pilpil.common.constants.redis.redisContanst.Chat.CHAT_UNREAD_PREFIX;
import static com.pilpil.common.constants.redis.redisContanst.User.LOGIN_TOKEN_PREFIX;

/**
 * <p>
 * 聊天表 服务实现类
 * </p>
 *
 * @author 
 * @since 2026-05-29
 */
@Service
@RequiredArgsConstructor
public class ChatsServiceImpl extends ServiceImpl<ChatsMapper, Chats> implements IChatsService {
    private final StringRedisTemplate redisTemplate;
    private final IUserService userService;
    @Override
    public void saveChat(ChatDtoWeb bean,Long userId,boolean b) {
        Long targetId = bean.getTargetId();
        String key=CHAT_UNREAD_PREFIX+targetId;
        createSession(userId,targetId);
        createSession(targetId,userId);
        if(!b){
            redisTemplate.opsForHash().increment(key, userId.toString(), 1);
        }
    }
    private void createSession(Long userId,Long targetId){
        boolean exists = lambdaQuery()
                .eq(Chats::getUserId, userId)
                .eq(Chats::getTargetId, targetId)
                .exists();
        if(! exists){
            Chats chats=new Chats();
            chats.setUserId(userId);
            chats.setTargetId(targetId);
            chats.setUnread(0);
            save(chats);
        }
    }

    @Override
    public List<ChatsVo> getChats() {
        Long userId = UserHolder.get().getId();
        Map<Long,Integer> UNREDMAP=new HashMap<>();
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(CHAT_UNREAD_PREFIX + userId);
        entries.forEach((targetIdObj,unreadObj)->{
            Long targetId=Long.parseLong(targetIdObj.toString());
            Integer unread=Integer.parseInt(unreadObj.toString());
            UNREDMAP.put(targetId,unread);
        });

        List<Chats> list = lambdaQuery().eq(Chats::getUserId, userId)
                .list();
        if(list==null || list.isEmpty()){
            return Collections.emptyList();
        }
        List<ChatsVo> vo=new ArrayList<>(list.size());
        Set<Long> targetIds = list.stream().map(Chats::getTargetId).collect(Collectors.toSet());
        Map<Long, User> userMap = userService.lambdaQuery().in(User::getId, targetIds).list().stream().collect(Collectors.toMap(User::getId, u -> u));
        for(Chats chats:list){
            ChatsVo bean = BeanUtil.toBean(chats, ChatsVo.class);
            bean.setUnread(Optional.ofNullable(UNREDMAP.get(chats.getTargetId())).orElse(0));
            bean.setTargetName(Optional.ofNullable(userMap.get(chats.getTargetId())).map(User::getNickName).orElse(""));
            bean.setTargetAvatar(Optional.ofNullable(userMap.get(chats.getTargetId())).map(User::getAvatar).orElse(""));
            vo.add(bean);
        }
        return vo;
    }

    @Override
    public void deletes(Long targetId) {
        Long userId = UserHolder.get().getId();
        String key=CHAT_UNREAD_PREFIX+userId;
        redisTemplate.opsForHash().delete(key,targetId.toString());
        lambdaUpdate()
                .eq(Chats::getUserId, userId)
                .eq(Chats::getTargetId, targetId)
                .set(Chats::getUnread, 0)
                .update();
    }
}
