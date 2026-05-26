package com.pilpil.web.util;

import com.pilpil.comment.entity.po.VideoData;
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

import static com.pilpil.comment.constants.redis.redisContanst.Video.VIDEO_PLAY_PREFIX;

@Slf4j
@RequiredArgsConstructor
@Component
public class PlayCountToMysql {
    private final StringRedisTemplate redisTemplate;
    private final IVideoDataService videoDataService;
    @Scheduled(cron = "0 0/1 * * * ?")
    @Transactional
    public void run(){
        log.info("开始同步播放量到数据库");
        PlayCount();
        log.info("同步播放量到数据库完成");
    }

    @PreDestroy
    public void PreDestroy(){
        log.info("程序退出，开始同步播放量");
        PlayCount();
    }
    private void PlayCount() {
        Set<String> keys = redisTemplate.keys(VIDEO_PLAY_PREFIX + "*");
        for (String key : keys){
            String videoIdStr = key.replace(VIDEO_PLAY_PREFIX, "");
            int videoId = Integer.parseInt(videoIdStr);
            Map<Object, Object> HashData = redisTemplate.opsForHash().entries(key);
            HashData.forEach((sectionObj,countObj)->{
                int sectionId = Integer.parseInt(sectionObj.toString());
                int addCount = Integer.parseInt(countObj.toString());
                VideoData videoData = videoDataService.lambdaQuery()
                        .eq(VideoData::getVideoId, videoId)
                        .eq(VideoData::getSectionId, sectionId)
                        .one();
                if(videoData==null){
                    VideoData videoData1 = new VideoData();
                    videoData1.setVideoId(videoId);
                    videoData1.setSectionId(sectionId);
                    videoData1.setViewCount(addCount);
                    videoData1.setUpdateTime(LocalDate.now());
                    videoDataService.save(videoData1);
                }else{
                    videoDataService.lambdaUpdate()
                            .eq(VideoData::getVideoId, videoId)
                            .eq(VideoData::getSectionId, sectionId)
                            .setSql("view_count = IFNULL(view_count, 0) + " + addCount)
                            .set(VideoData::getUpdateTime, LocalDate.now())
                            .update();

                }
            });
            redisTemplate.delete(key);
        }
    }
}
