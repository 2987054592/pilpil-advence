package com.pilpil.admin.service.impl;


import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import com.pilpil.admin.entity.dto.UserDto;
import com.pilpil.admin.mapper.UserMapper;
import com.pilpil.admin.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pilpil.comment.constants.redis.redisContanst;
import com.pilpil.comment.entity.UserInfo;
import com.pilpil.comment.entity.po.User;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 用户信息 服务实现类
 * </p>
 *
 * @author 
 * @since 2026-05-20
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    private final StringRedisTemplate redisTemplate;
    private static final String ADMIN_PASSWORD="e10adc3949ba59abbe56e057f20f883e";
    @Override
    public boolean login(UserDto userDto, HttpServletResponse response) {
        String password = DigestUtil.md5Hex(userDto.getPassword());
        if(userDto.getEmail().equals("admin@qq.com")&&password.equals(ADMIN_PASSWORD)){
            String token = UUID.randomUUID().toString();
            response.setHeader("Authorization",token);
            User one = lambdaQuery().eq(User::getEmail, userDto.getEmail()).one();
            UserInfo userInfo = UserInfo.builder()
                    .id(one.getId())
                    .nickName(one.getNickName())
                    .avatar(one.getAvatar())
                    .build();
            String jsonStr = JSONUtil.toJsonStr(userInfo);
            redisTemplate.opsForValue().set(redisContanst.User.LOGIN_TOKEN_PREFIX+token,jsonStr);
            redisTemplate.expire(redisContanst.User.LOGIN_TOKEN_PREFIX+token, 1L, TimeUnit.DAYS);
            return true;
        }
        return false;
    }
}
