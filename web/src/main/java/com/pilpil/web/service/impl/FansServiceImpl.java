package com.pilpil.web.service.impl;


import com.pilpil.common.entity.UserInfo;
import com.pilpil.common.entity.po.Fans;
import com.pilpil.common.entity.po.User;
import com.pilpil.common.exception.illegalException;
import com.pilpil.common.utils.UserHolder;
import com.pilpil.web.entity.dto.FansVo;
import com.pilpil.web.mapper.FansMapper;
import com.pilpil.web.mapper.UserMapper;
import com.pilpil.web.service.IFansService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pilpil.web.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.pilpil.common.constants.Exception.exceptionConstants.User.USER_NOT_EXIST;
import static com.pilpil.common.constants.redis.redisContanst.Fans.FANS_FOLLOWER_PREFIX;
import static com.pilpil.common.constants.redis.redisContanst.Fans.FANS_FOLLOW_PREFIX;

/**
 * <p>
 * 关注，粉丝表 服务实现类
 * </p>
 *
 * @author 
 * @since 2026-05-28
 */
@Service
@RequiredArgsConstructor
public class FansServiceImpl extends ServiceImpl<FansMapper, Fans> implements IFansService {
    private final StringRedisTemplate redisTemplate;
    private final UserMapper userMapper;
    @Override
    public void saveFans(Long targetId) {
        Long userId = UserHolder.get().getId();
        User user = userMapper.selectById(userId);
        if(user==null){
            throw new illegalException(USER_NOT_EXIST);
        }
        Fans fans = Fans.builder()
                .targetId(targetId)
                .userId(userId)
                .createTime(LocalDate.now())
                .build();
        save(fans);
        String followKey=FANS_FOLLOW_PREFIX+userId;
        String followerKey=FANS_FOLLOWER_PREFIX+targetId;
        redisTemplate.opsForSet().add(followKey,targetId.toString());
        redisTemplate.opsForSet().add(followerKey,userId.toString());
    }

    @Override
    public void cancelFans(Long targetId) {
        User user = userMapper.selectById(targetId);
        if(user==null){
            throw new illegalException(USER_NOT_EXIST);
        }

        Long userId = UserHolder.get().getId();
        lambdaUpdate()
                .eq(Fans::getUserId,userId)
                .eq(Fans::getTargetId,targetId)
                .remove();
        String followKey=FANS_FOLLOW_PREFIX+userId;
        String followerKey=FANS_FOLLOWER_PREFIX+targetId;
        redisTemplate.opsForSet().remove(followKey,targetId.toString());
        redisTemplate.opsForSet().remove(followerKey,userId.toString());
    }

    @Override
    public boolean IsFans(Long targetId) {
        UserInfo userInfo = UserHolder.get();
        if(userInfo==null){
            return false;
        }
        User user = userMapper.selectById(targetId);
        if(user==null){
            throw new illegalException(USER_NOT_EXIST);
        }
        Boolean b = redisTemplate.opsForSet().isMember(FANS_FOLLOW_PREFIX + UserHolder.get().getId(), targetId.toString());
        if(b!=null && b){
            return true;
        }
        Fans fans = lambdaQuery()
                .eq(Fans::getUserId,userInfo.getId() )
                .eq(Fans::getTargetId, targetId)
                .one();
        if(fans!=null){
            redisTemplate.opsForSet().add(FANS_FOLLOW_PREFIX+UserHolder.get().getId(),targetId.toString());
            return true;
        }else{
            return false;
        }
    }


    /**
     * 获取我关注的人的信息
     * @return
     */
    @Override
    public List<FansVo> getFansList(Long userId) {
        String key=FANS_FOLLOW_PREFIX+userId;
        Set<String> ids = redisTemplate.opsForSet().members(key);
        Set<Long> collect;
        if(ids.isEmpty()){
            collect = lambdaQuery().eq(Fans::getUserId, userId).list().stream().map(Fans::getTargetId).collect(Collectors.toSet());
            if(collect.isEmpty()){
                return Collections.emptyList();
            }else{
                for(Long id : collect){
                    redisTemplate.opsForSet().add(key,id.toString());
                }
            }
        }
        collect = ids.stream().map(Long::valueOf).collect(Collectors.toSet());
        List<User> users = userMapper.selectByIds(collect);

        List<FansVo> vo=new ArrayList<>(users.size());
        for (User user : users) {
            vo.add(FansVo.builder()
                    .nickName(user.getNickName())
                    .avatar(user.getAvatar())
                    .build());
        }
        return vo;
    }


    /**
     * 获取关注了我的人的信息(粉丝)
     * @return
     */
    @Override
    public List<FansVo> getFollowerList(Long userId) {
        String key = FANS_FOLLOWER_PREFIX + userId;
        Set<String> ids = redisTemplate.opsForSet().members(key);
        Set<Long> collect;
        if(ids.isEmpty()){
            List<Fans> fans = lambdaQuery().eq(Fans::getTargetId, userId).list();
            collect= fans.stream().map(Fans::getUserId).collect(Collectors.toSet());
            if(collect.isEmpty()){
                return Collections.emptyList();
            }else {
                for (Fans fan : fans) {
                    redisTemplate.opsForSet().add(key, fan.getUserId().toString());
                }

            }
        }else{
            collect=ids.stream().map(Long::valueOf).collect(Collectors.toSet());
        }
        List<User> users = userMapper.selectByIds(collect);
        List<FansVo> vo = new ArrayList<>(users.size());
        for (User user : users) {
            vo.add(FansVo.builder()
                    .nickName(user.getNickName())
                    .avatar(user.getAvatar())
                    .build());
        }
        return vo;
    }

    /**
     * 获取粉丝数
     * @param userId
     * @return
     */
    @Override
    public Integer getFansCount(Long userId) {
        String key = FANS_FOLLOWER_PREFIX + userId;
        Set<String> ids = redisTemplate.opsForSet().members(key);
        if(ids.isEmpty()){
            return Math.toIntExact(lambdaQuery().eq(Fans::getTargetId, userId).count());
        }else{
            return ids.size();
        }
    }

    /**
     * 获取关注数
     * @param userId
     * @return
     */

    @Override
    public Integer getFollowerCount(Long userId) {
        String  key = FANS_FOLLOW_PREFIX + userId;
        Set<String> ids = redisTemplate.opsForSet().members(key);
        if(ids.isEmpty()){
            return Math.toIntExact(lambdaQuery().eq(Fans::getUserId, userId).count());
        }else{
            return ids.size();
        }

    }
}
