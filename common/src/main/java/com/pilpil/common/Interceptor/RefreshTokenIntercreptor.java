package com.pilpil.common.Interceptor;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.pilpil.common.entity.UserInfo;
import com.pilpil.common.utils.UserHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.TimeUnit;

import static com.pilpil.common.constants.redis.redisContanst.User.LOGIN_TOKEN_PREFIX;

@RequiredArgsConstructor
public class RefreshTokenIntercreptor implements HandlerInterceptor {
    private final StringRedisTemplate redisTemplate;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");
        
        if (token == null || token.isEmpty()) {
            System.out.println("Token 为空");
            return true;
        }
        
        String json = redisTemplate.opsForValue().get(LOGIN_TOKEN_PREFIX + token);
        
        if (json == null) {
            System.out.println("Redis 中找不到 Token: " + token);
            return true;
        }

        JSONObject jsonObject = JSONUtil.parseObj(json);
        UserInfo user = new UserInfo();
        user.setId(jsonObject.getLong("id"));
        user.setNickName(jsonObject.getStr("nickName"));

        UserHolder.save(user);
        System.out.println("用户信息已设置: " + user.getId());
        
        redisTemplate.expire(LOGIN_TOKEN_PREFIX + token, 1L, TimeUnit.DAYS);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        UserHolder.remove();
    }
}
