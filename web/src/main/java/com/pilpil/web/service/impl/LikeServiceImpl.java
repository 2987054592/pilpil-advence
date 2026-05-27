package com.pilpil.web.service.impl;

import com.pilpil.common.enums.LikeBisType;
import com.pilpil.common.enums.LikeType;
import com.pilpil.common.exception.illegalException;
import com.pilpil.common.entity.po.Like;
import com.pilpil.common.utils.UserHolder;
import com.pilpil.web.entity.dto.likeDto;
import com.pilpil.web.mapper.LikeMapper;
import com.pilpil.web.service.ILikeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.pilpil.common.constants.Exception.exceptionConstants.Like.LIKE_EXIST;
import static com.pilpil.common.constants.Exception.exceptionConstants.Like.LIKE_NOT_EXIST;
import static com.pilpil.common.constants.redis.redisContanst.Like.*;

/**
 * <p>
 * 点赞表 服务实现类
 * </p>
 *
 * @author 
 * @since 2026-05-27
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LikeServiceImpl extends ServiceImpl<LikeMapper, Like> implements ILikeService {
    private final StringRedisTemplate redisTemplate;
    private final static String COMMENT_KEY = LIKE_COMMENT_PREFIX;
    private final static String DANMU_KEY = LIKE_DANMU_PREFIX;
    private final static String VIDEO_KEY= LIKE_VIDEO_PREFIX;
    @Override
    public void savelike(likeDto likeDto) {
        if(likeDto.getLikeType().equals(LikeType.LIKE)){
            // 点赞
            boolean result=Tolike(likeDto.getLikeBizType(),likeDto.getBizId(),likeDto.getVideoId());
            if(!result){
                throw new illegalException(LIKE_EXIST);
            }
        }else{
            // 取消点赞
            boolean result=cannelLike(likeDto.getLikeBizType(),likeDto.getBizId(),likeDto.getVideoId());
            if(!result){
                throw new illegalException(LIKE_NOT_EXIST);
            }
        }
    }

    private boolean Tolike(LikeBisType likeBizType, Integer bizId, Integer videoId) {
        Long userId = UserHolder.get().getId();
        Like one = lambdaQuery().eq(Like::getBizId, bizId)
                .eq(Like::getUserId, userId).one();
        if(one!=null){
            return false;
        }
        Like like = Like.builder()
                .userId(userId)
                .likeBizType(likeBizType)
                .videoId(videoId)
                .bizId(bizId).build();

        if (likeBizType.equals(LikeBisType.COMMENT)) {
            // 点赞评论
            redisTemplate.opsForHash().increment(COMMENT_KEY+videoId,bizId.toString(),1);
        }else if(likeBizType.equals(LikeBisType.DANMU)){
            // 点赞弹幕
            redisTemplate.opsForHash().increment(DANMU_KEY+videoId,bizId.toString(),1);
        }else{
            // 点赞视频
            redisTemplate.opsForHash().increment(VIDEO_KEY+videoId,bizId.toString(),1);
        }
        save(like);
        return true;
    }

    private boolean cannelLike(LikeBisType likeBizType, Integer bizId, Integer videoId) {
        Long userId = UserHolder.get().getId();
        boolean remove = lambdaUpdate().eq(Like::getBizId, bizId)
                .eq(Like::getUserId, userId)
                .remove();
        if(!remove){
            return false;
        }
        if (likeBizType.equals(LikeBisType.COMMENT)) {
            // 取消点赞评论
            redisTemplate.opsForHash().increment(COMMENT_KEY+videoId,bizId.toString(),-1);
        }else if(likeBizType.equals(LikeBisType.DANMU)){
            // 取消点赞弹幕
            redisTemplate.opsForHash().increment(DANMU_KEY+videoId,bizId.toString(),-1);
        }else{
            // 取消点赞视频
            redisTemplate.opsForHash().increment(VIDEO_KEY+videoId,bizId.toString(),-1);
        }
        return true;
    }

    @Override
    public Map<LikeBisType, List<Integer>> getlike(Integer videoId) {
        List<Like> list = lambdaQuery()
                .eq(Like::getUserId, UserHolder.get().getId())
                .eq(Like::getVideoId, videoId)
                .list();
        return list.stream().collect(Collectors.groupingBy(
                Like::getLikeBizType,
                Collectors.mapping(
                        Like::getBizId, Collectors.toList()
                )
        ));
    }
}
