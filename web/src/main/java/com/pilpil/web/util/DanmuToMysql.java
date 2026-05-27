package com.pilpil.web.util;

import cn.hutool.json.JSONUtil;
import com.pilpil.common.entity.po.Danmu;
import com.pilpil.common.entity.po.VideoData;
import com.pilpil.web.service.IDanmuService;
import com.pilpil.web.service.IVideoDataService;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.pilpil.common.constants.redis.redisContanst.Danmu.*;
import static com.pilpil.common.constants.redis.redisContanst.Like.LIKE_DANMU_PREFIX;

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
        log.info("开始同步弹幕相关");
        DanmuDataToMysql();
        DanmuStatisticToMysql();
        DanmuLikeToMysql();
        log.info("同步弹幕相关完成");

    }
    @PreDestroy
    public void destroy(){
        log.info("程序退出，开始同步弹幕相关");
        DanmuDataToMysql();
        DanmuStatisticToMysql();
        DanmuLikeToMysql();
    }

    private void DanmuStatisticToMysql() {
        Set<String> keys1 = redisTemplate.keys(DANMU_LIST_PREFIX + "*");
        if(keys1==null || keys1.isEmpty()){
            return;
        }
        for(String key:keys1){
            Map<Object, Object> hasData = redisTemplate.opsForHash().entries(key);
            if(hasData==null || hasData.isEmpty()){
                continue;
            }
            hasData.forEach((sectionObj,countObj)->{
                String sectionIdStr = sectionObj.toString();
                int addCount = Integer.parseInt(countObj.toString());
                String videoIdStr = key.replace(DANMU_LIST_PREFIX, "");
                Integer videoId = Integer.valueOf(videoIdStr);
                boolean success = videoDataService.lambdaUpdate()
                        .eq(VideoData::getVideoId, videoId)
                        .setSql("danmu_count = IFNULL(danmu_count, 0) + " + addCount)
                        .update();
                if(success){
                   redisTemplate.opsForHash().delete(key, sectionIdStr);
                }else {
                    log.info("数据不存在，等待下次更新");
                }
            });

        }
    }

    private boolean DanmuDataToMysql() {
        Set<String> keys = redisTemplate.keys(DAMU_TEMPT_PREFIX + "*");
        if(keys==null || keys.isEmpty()){
            return true;
        }
        for (String key : keys) {
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
        }
        return false;
    }
    private void DanmuLikeToMysql(){
        Set<String> keys = redisTemplate.keys(LIKE_DANMU_PREFIX + "*");
        if(keys==null || keys.isEmpty()){
            return;
        }
        for (String key : keys) {
            Map<Object, Object> HashData = redisTemplate.opsForHash().entries(key);
            HashData.forEach((bizid,count)->{
                int bizId = Integer.parseInt(bizid.toString());
                int count1 = Integer.parseInt(count.toString());
                danmuService.lambdaUpdate()
                        .eq(Danmu::getId, bizId)
                        .setSql("like_count = IFNULL(like_count, 0) + " + count1)
                        .update();
            });
            redisTemplate.delete(key);
        }
    }


}
