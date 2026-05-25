package com.pilpil.web.util;

import cn.hutool.json.JSONUtil;
import com.pilpil.comment.entity.po.Danmu;
import com.pilpil.comment.entity.po.VideoData;
import com.pilpil.web.service.IDanmuService;
import com.pilpil.web.service.IVideoDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.pilpil.comment.constants.redis.redisContanst.Danmu.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class DanmuToMysql {
    private final StringRedisTemplate redisTemplate;
    private final IDanmuService danmuService;
    private final IVideoDataService videoDataService;
    @Scheduled(cron = "0 0/1 * * * ?")
    @Transactional
    public void Tomysql(){
        log.info("开始同步弹幕到数据库");
        Set<String> keys = redisTemplate.keys(DAMU_TEMPT_PREFIX + "*");
          if(keys==null || keys.isEmpty()){
            return;
        }
        for (String key : keys) {
            String[] split = key.split(":");
            String videoId = split[2];
            String sectionId = split[3];

            List<String> list = redisTemplate.opsForList().leftPop(key,100);
            if(list==null || list.isEmpty()){
                continue;
            }
            List<Danmu> list1 = list.stream().map(item -> {
                        return JSONUtil.toBean(item, Danmu.class);
                    }
            ).toList();
            try {
                danmuService.saveBatch(list1);
            }catch (Exception e){
                log.error("弹幕同步失败，可能是以及存入，不用管"+e.getMessage());
            }
            String countKey = DANMU_LIST_PREFIX + videoId + ":" + sectionId;
            String s = redisTemplate.opsForValue().get(countKey);
            if(s==null){
                continue;
            }
            int addCount = Integer.parseInt(s);
            VideoData videoData = videoDataService.lambdaQuery()
                    .eq(VideoData::getVideoId, videoId)
                    .eq(VideoData::getSectionId, sectionId)
                    .one();
            if(videoData==null){
                VideoData videoData1 = new VideoData();
                videoData1.setVideoId(Integer.valueOf(videoId));
                videoData1.setSectionId(Integer.valueOf(sectionId));
                videoData1.setDanmuCount(addCount);
                videoDataService.save(videoData1);
            }else{
                videoDataService.lambdaUpdate()
                        .eq(VideoData::getVideoId, videoId)
                        .eq(VideoData::getSectionId, sectionId)
                        .setSql("danmu_count = danmu_count + "+addCount)
                        .update();
            }
            redisTemplate.delete(countKey);
        }

    }


}
