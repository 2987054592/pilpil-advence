package com.pilpil.web.Websocket;

import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.pilpil.common.entity.UserInfo;
import com.pilpil.common.entity.po.Danmu;
import com.pilpil.common.entity.po.User;
import com.pilpil.common.enums.StatusType;
import com.pilpil.common.exception.illegalException;
import com.pilpil.web.entity.dto.DanmuDto;
import com.pilpil.web.entity.vo.WebDanmuVo;
import com.pilpil.web.service.IDanmuService;
import com.pilpil.web.service.IUserService;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.librealsense.error;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.pilpil.common.constants.Exception.exceptionConstants.User.LOGIN_EXPIRE;
import static com.pilpil.common.constants.Exception.exceptionConstants.User.USER_STATUS_ERROR;
import static com.pilpil.common.constants.redis.redisContanst.User.LOGIN_TOKEN_PREFIX;

@Component
@Slf4j
@ServerEndpoint(value = "/ws/danmu/{vid}")
public class DanMu {
    private static IDanmuService danmuService;
    private static StringRedisTemplate redisTemplate;
    private static IUserService userService;
    private static final Map<String, Set<Session>> VIDEO_SESSION_MAP=new ConcurrentHashMap<>();

    @Autowired
    public void init(IDanmuService danmuService,
                     StringRedisTemplate redisTemplate,
                     IUserService userService){
        DanMu.danmuService = danmuService;
        DanMu.redisTemplate = redisTemplate;
        DanMu.userService = userService;
    }
    @OnOpen
    public void onOpen(Session session, @PathParam("vid") String vid){
        VIDEO_SESSION_MAP.computeIfAbsent(vid,k->new ConcurrentHashSet<>()).add(session);
        pushOnlineCount(vid);
    }
    @OnMessage
    public void onMessage(Session session, @PathParam("vid") String vid, String message){
        try {
            JSONObject jsonObject = JSONUtil.parseObj(message);
            WebDanmuVo bean1 = JSONUtil.toBean(jsonObject, WebDanmuVo.class);
            String token = bean1.getToken();
            DanmuDto danmuDto = bean1.getDanmuDto();
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
            danmuService.saveDanmuWebsock(bean1, user);
            String sendMsg = JSONUtil.toJsonStr(danmuDto);
            sendToAllVideoUsers(vid,sendMsg);
        }catch (Exception e){
            log.error("弹幕发送异常：{}",e.getMessage());
        }

    }

    private void sendToAllVideoUsers(String vid, String sendMsg) {
        Set<Session> sessionSet = VIDEO_SESSION_MAP.get(vid);
        if(sessionSet==null||sessionSet.isEmpty()){
            return;
        }
        for (Session session : sessionSet) {
            try {
                if(session.isOpen()){
                    session.getBasicRemote().sendText(sendMsg);
                }
            }catch (Exception e){
                log.error("弹幕发送异常：{}",e.getMessage());
            }
        }


    }

    @OnClose
    public void onClose(Session session, @PathParam("vid") String vid){
        Set<Session> sessionSet = VIDEO_SESSION_MAP.get(vid);
        if(sessionSet!=null){
            sessionSet.remove(session);
            if(sessionSet.isEmpty()){
                VIDEO_SESSION_MAP.remove(vid);
            }
        }
        pushOnlineCount(vid);
    }
    @OnError
    public void onError(Session session, @PathParam("vid") String vid, Throwable error){
        log.error("弹幕异常：{}",error.getMessage());
    }

    private void pushOnlineCount(String vid) {
        Set<Session> sessions = VIDEO_SESSION_MAP.get(vid);
        if (sessions == null || sessions.isEmpty()) return;

        int count = sessions.size();
        String msg = JSONUtil.toJsonStr(Map.of(
                "type", "ONLINE_COUNT",
                "count", count
        ));

        // 群发
        for (Session session : sessions) {
            try {
                if (session.isOpen()) session.getBasicRemote().sendText(msg);
            } catch (IOException ignored) {}
        }
    }

}
