package com.pilpil.web.Websocket;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.pilpil.common.entity.UserInfo;
import com.pilpil.common.entity.po.User;
import com.pilpil.common.enums.StatusType;
import com.pilpil.common.exception.illegalException;
import com.pilpil.web.entity.dto.ChatDtoWeb;
import com.pilpil.web.service.IChatsDetailedService;
import com.pilpil.web.service.IChatsService;
import com.pilpil.web.service.IUserService;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.pilpil.common.constants.Exception.exceptionConstants.User.LOGIN_EXPIRE;
import static com.pilpil.common.constants.Exception.exceptionConstants.User.USER_STATUS_ERROR;
import static com.pilpil.common.constants.redis.redisContanst.User.LOGIN_TOKEN_PREFIX;

@Component
@Slf4j
@ServerEndpoint(value = "/ws/chat")

public class Chat {
    private static final Map<Long, Session> USER_SESSION_MAP = new ConcurrentHashMap<>();
    private static IChatsService chatsService;
    private static IChatsDetailedService chatsDetailedService;
    private static StringRedisTemplate redisTemplate;
    private static IUserService userService;
    @Autowired
    public void init(IChatsService chatsService,
                     IChatsDetailedService chatsDetailedService,
                     StringRedisTemplate redisTemplate,
                     IUserService userService){
        Chat.chatsService = chatsService;
        Chat.chatsDetailedService = chatsDetailedService;
        Chat.redisTemplate = redisTemplate;
        Chat.userService = userService;
    }
    @OnOpen
    public void onOpen(Session session){
        log.info("用户加入聊天室");
    }
    @OnMessage
    public void onMessage(Session session, String message){
        log.info("收到消息：{}",message);
        JSONObject jsonObject = JSONUtil.parseObj(message);
        ChatDtoWeb bean = JSONUtil.toBean(jsonObject, ChatDtoWeb.class);
        String token = bean.getToken();
        String json = redisTemplate.opsForValue().get(LOGIN_TOKEN_PREFIX + token);
        if (json == null) {
            throw new illegalException(LOGIN_EXPIRE);
        }
        JSONObject jsonObjects = JSONUtil.parseObj(json);
        UserInfo user = new UserInfo();
        user.setId(jsonObjects.getLong("id"));
        User user1 = userService.getBaseMapper().selectById(user.getId());
        if(user1.getStatus().equals(StatusType.BAN)){
            throw new illegalException(USER_STATUS_ERROR);
        }
        USER_SESSION_MAP.put(user.getId(),session);
        Long targetId = bean.getTargetId();

        try {
            Session session1 = USER_SESSION_MAP.get(targetId);
            if(session1 != null && session1.isOpen()){
                session1.getBasicRemote().sendText(message);
            }
            boolean b = USER_SESSION_MAP.containsKey(targetId);

            chatsService.saveChat(bean,user1.getId(),b);
            chatsDetailedService.saveChat(bean,user1.getId());


        }catch (Exception e){
            log.error("聊天发生异常：{}",e.getMessage());
        }
    }
    @OnError
    public void onError(Session session, Throwable error){
        log.error("聊天发生异常：{}",error.getMessage());
    }
    @OnClose
    public void onClose(Session session){

        USER_SESSION_MAP.entrySet().removeIf(entry -> entry.getValue() == session);
        log.info("用户退出聊天室");
    }




}
