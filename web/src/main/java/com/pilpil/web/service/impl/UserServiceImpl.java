package com.pilpil.web.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import com.pilpil.common.constants.Exception.exceptionConstants;

import com.pilpil.common.constants.redis.redisContanst;
import com.pilpil.common.entity.UserInfo;
import com.pilpil.common.entity.po.User;
import com.pilpil.common.entity.vo.UserVoDetail;
import com.pilpil.common.enums.LevelType;
import com.pilpil.common.enums.SexType;
import com.pilpil.common.enums.StatusType;
import com.pilpil.common.exception.illegalException;
import com.pilpil.common.utils.UserHolder;
import com.pilpil.web.entity.dto.UserDto;

import com.pilpil.common.entity.vo.UserVo;

import com.pilpil.web.mapper.UserMapper;
import com.pilpil.web.service.IFansService;
import com.pilpil.web.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.pilpil.common.constants.Exception.exceptionConstants.User.*;

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
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    private final StringRedisTemplate redisTemplate;
    private final IFansService fansService;

    @Override
    public void saveUser(UserDto userDto) {
        if(!userDto.getCode().equals(redisTemplate.opsForValue().get(redisContanst.User.REGISTER_CODE_PREFIX+userDto.getEmail()))){
            throw new illegalException(exceptionConstants.User.USER_CODE_ERROR);
        }
        User user = lambdaQuery().eq(User::getEmail, userDto.getEmail()).one();
        if(user!=null){
            throw new illegalException(exceptionConstants.User.USER_EXIST);
        }
        User users = User.builder()
                .email(userDto.getEmail())
                .password(DigestUtil.md5Hex(userDto.getPassword()))
                .nickName(DefaultName())
                .totalCoin(10)
                .currentCoin(10)
                .createTime(LocalDate.now())
                .level(LevelType.LV0)
                .sex(SexType.UNKNOWN)
                .experience(0)
                .status(StatusType.NORMAL)
                .build();
        save(users);
        redisTemplate.delete(redisContanst.User.REGISTER_CODE_PREFIX+userDto.getEmail());

    }
    public String DefaultName(){
        String prefix = "pilpil";
        return prefix+ "_"+RandomUtil.randomNumbers(11);
    }

    @Override
    public String code(String email) {
        if(email==null){
            throw new illegalException(EMAIL_NULL);
        }
        User user = lambdaQuery().eq(User::getEmail, email).one();
        if(user!=null){
            throw new illegalException(EMAIL_EXIST);
        }
        String codes = RandomUtil.randomNumbers(4);
        log.info("code:{}",codes);
        redisTemplate.opsForValue().set(redisContanst.User.REGISTER_CODE_PREFIX+email, codes, 5L, TimeUnit.MINUTES);
        return codes;
    }

    @Override
    public UserVo login(UserDto userDto, HttpServletResponse response) {
        String password = DigestUtil.md5Hex(userDto.getPassword());
        User user = lambdaQuery().eq(User::getEmail, userDto.getEmail()).eq(User::getPassword, password).one();
        if(user==null){
            throw new illegalException(exceptionConstants.User.USER_LOGIN_ERROR);
        }
        user.setLastTime(LocalDate.now());
        updateById(user);
        String token= UUID.randomUUID().toString();
        response.setHeader("Authorization", token);
        UserInfo userInfo = UserInfo.builder()
                .id(user.getId())
                .nickName(user.getNickName())
                .avatar(user.getAvatar())
                .build();
        String json = JSONUtil.toJsonStr(userInfo);
        redisTemplate.opsForValue().set(redisContanst.User.LOGIN_TOKEN_PREFIX+token, json);
        redisTemplate.expire(redisContanst.User.LOGIN_TOKEN_PREFIX+token, 1L, TimeUnit.DAYS);
        Integer fans=fansService.getFansCount(user.getId());
        Integer follow=fansService.getFollowerCount(user.getId());
        return UserVo.builder()
                .nickName(user.getNickName())
                .avatar(user.getAvatar())
                .currentCoin(user.getCurrentCoin())
                .level(user.getLevel())
                .experience(user.getExperience())
                .introduction(user.getIntroduction())
                .fans(fans)
                .follow(follow)
                .build();
    }

    @Override
    public void exit(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if(token!=null) {
            redisTemplate.delete(redisContanst.User.LOGIN_TOKEN_PREFIX + token);
        }
    }

    @Override
    public UserVoDetail me() {
        Long userId = UserHolder.get().getId();
        User user = getById(userId);
        UserVoDetail bean = BeanUtil.toBean(user, UserVoDetail.class);
        bean.setFans(fansService.getFansCount(userId));
        bean.setFollow(fansService.getFollowerCount(userId));
        return bean;
    }

    @Override
    public UserVoDetail info(String name) {
        User user = lambdaQuery().eq(User::getNickName, name).one();
        if(user==null){
            throw new illegalException(exceptionConstants.User.USER_NOT_EXIST);
        }
        if(user.getStatus().equals(StatusType.BAN)){
            throw new illegalException(USER_STATUS_ERROR);
        }
        UserVoDetail bean = BeanUtil.toBean(user, UserVoDetail.class);
        bean.setFans(fansService.getFansCount(user.getId()));
        bean.setFollow(fansService.getFollowerCount(user.getId()));
        return bean;
    }

    @Override
    public UserVo infoSimple(String name) {
        User user = lambdaQuery().eq(User::getNickName, name).one();
        if(user==null){
            throw new illegalException(exceptionConstants.User.USER_NOT_EXIST);
        }
        Long userId = user.getId();
        Integer fans=fansService.getFansCount(userId);
        Integer follow=fansService.getFollowerCount(userId);
        if(user.getStatus().equals(StatusType.BAN)){
            return UserVo.builder()
                    .nickName(USER_STATUS_ERROR)
                    .avatar("")
                    .currentCoin(0)
                    .level(LevelType.LV0)
                    .experience(0)
                    .introduction("")
                    .fans(fans)
                    .follow(follow)
                    .build();
        }
        return UserVo.builder()
                .nickName(user.getNickName())
                .avatar(user.getAvatar())
                .currentCoin(user.getCurrentCoin())
                .level(user.getLevel())
                .experience(user.getExperience())
                .introduction(user.getIntroduction())
                .fans(fans)
                .follow(follow)
                .build();

    }
}
