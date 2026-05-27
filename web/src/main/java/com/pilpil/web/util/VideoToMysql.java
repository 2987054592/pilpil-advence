package com.pilpil.web.util;

import com.pilpil.common.entity.po.VideoData;
import com.pilpil.web.service.IVideoDataService;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

import static com.pilpil.common.constants.redis.redisContanst.Like.LIKE_VIDEO_PREFIX;
import static com.pilpil.common.constants.redis.redisContanst.Video.VIDEO_PLAY_PREFIX;

@Slf4j
@RequiredArgsConstructor
@Component
public class VideoToMysql {
    private final StringRedisTemplate redisTemplate;
    private final IVideoDataService videoDataService;
    @Scheduled(cron = "0 0/1 * * * ?")
    @Transactional
    public void run(){
        log.info("开始同步视频相关到数据库");
        PlayCount();
        VideoLike();
        log.info("同步视频相关到数据库完成");
    }

    @PreDestroy
    public void PreDestroy(){
        log.info("程序退出，开始同步视频相关");
        PlayCount();
    }
    private void PlayCount() {
        Set<String> keys = redisTemplate.keys(VIDEO_PLAY_PREFIX + "*");
        for (String key : keys){
            String videoIdStr = key.replace(VIDEO_PLAY_PREFIX, "");
            int videoId = Integer.parseInt(videoIdStr);
            Map<Object, Object> HashData = redisTemplate.opsForHash().entries(key);
            HashData.forEach((sectionObj,countObj)->{
                int addCount = Integer.parseInt(countObj.toString());
                    videoDataService.lambdaUpdate()
                            .eq(VideoData::getVideoId, videoId)
                            .setSql("view_count = IFNULL(view_count, 0) + " + addCount)
                            .set(VideoData::getUpdateTime, LocalDate.now())
                            .update();

            });
            redisTemplate.delete(key);
        }
    }
    private void VideoLike(){
        Set<String> keys = redisTemplate.keys(LIKE_VIDEO_PREFIX + "*");
        if(keys==null||keys.isEmpty()){
            return;
        }
        for (String key : keys){
            Map<Object, Object> HashData = redisTemplate.opsForHash().entries(key);
            HashData.forEach((videoIdObj,likeCountObj)->{
                int videoId = Integer.parseInt(videoIdObj.toString());
                int likeCount = Integer.parseInt(likeCountObj.toString());
                videoDataService.lambdaUpdate()
                        .eq(VideoData::getVideoId, videoId)
                        .setSql("like_count = IFNULL(like_count, 0) + " + likeCount)
                        .update();
            });
            redisTemplate.delete(key);
        }
    }
}
