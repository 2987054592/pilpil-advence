package com.pilpil.comment.Interceptor;

import cn.hutool.core.bean.BeanUtil;

import com.pilpil.comment.entity.UserInfo;
import com.pilpil.comment.utils.UserHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.pilpil.comment.constants.redis.redisContanst.User.LOGIN_TOKEN_PREFIX;

@RequiredArgsConstructor
public class RefreshTokenIntercreptor implements HandlerInterceptor {
    private final StringRedisTemplate redisTemplate;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");
        String json = redisTemplate.opsForValue().get(LOGIN_TOKEN_PREFIX + token);
        UserInfo user = BeanUtil.toBean(json, UserInfo.class);
        if(user==null){
            return true;
        }
        UserHolder.save(user);
        redisTemplate.expire(LOGIN_TOKEN_PREFIX + token, 1L, TimeUnit.DAYS);
        return true;
    }

}
