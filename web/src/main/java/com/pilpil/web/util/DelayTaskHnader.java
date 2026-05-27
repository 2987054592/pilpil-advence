package com.pilpil.web.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.pilpil.common.entity.delayTask;
import com.pilpil.common.entity.po.VideoRecord;
import com.pilpil.web.mapper.VideoRecordMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.DelayQueue;

import static com.pilpil.common.constants.redis.redisContanst.Video.VIDEO_RECORD_PREFIX;

@Component
@RequiredArgsConstructor
@Slf4j
public class DelayTaskHnader {
    private final StringRedisTemplate redisTemplate;
    private final DelayQueue<delayTask<DelayDateQueue>> queue=new DelayQueue<>();
    private final VideoRecordMapper videoRecordMapper;
    private static volatile boolean begin=true;
    @PostConstruct
    public void init() {
        CompletableFuture.runAsync(this::handlerDelayTask);

    }
    @PreDestroy
    public void destroy() {
        begin=false;
    }
    private void handlerDelayTask(){
        while(begin){
            try {
                delayTask<DelayDateQueue> task = queue.take();
                log.info("处理延迟任务：{}",task);
                DelayDateQueue data = task.data;
                Long userId1 = data.getUserId();
                Integer videoId = data.getVideoId();
                Integer sectionId = data.getSectionId();
                VideoRecord videoRecord = RecordFromRedis(userId1,videoId,sectionId);
                if(videoRecord==null){
                    continue;
                }if(!Objects.equals(videoRecord.getMoment(), task.data.getMoment())){
                    log.info("时间段不一致，取消上传 - videoid: {}, getSectionId: {}, getMoment: {}", task.data.getVideoId(), task.data.getSectionId(), task.data.getMoment());
                    continue;
                }else {
                    videoRecordMapper.updateById(videoRecord);
                }
            }catch (Exception e){
                log.error("处理延迟任务异常",e);
            }
        }
    }

    public VideoRecord RecordFromRedis(Long userId,Integer videoId,Integer sectionId) {
        String key=VIDEO_RECORD_PREFIX+userId+":"+videoId+":"+sectionId;
        String s = redisTemplate.opsForValue().get(key);
        if(s==null){
            return null;
        }

        VideoRecord record = null;
        try {
            JSONObject jsonObject = JSONUtil.parseObj(s);
            record = BeanUtil.toBean(jsonObject, VideoRecord.class);
            log.info("延迟任务转换成功：{}",record);

        }catch (Exception e){
            log.error("延迟任务转换异常",e);
        }
        return record;
    }

    public void writeRedis(VideoRecord videoRecord,Long userId){
        Integer videoId = videoRecord.getVideoId();
        Integer sectionId = videoRecord.getSectionId();
        String key=VIDEO_RECORD_PREFIX+userId+":"+videoId+":"+sectionId;
        redisTemplate.opsForValue().set(key,JSONUtil.toJsonStr(videoRecord),Duration.ofSeconds(25));
    }
    public void addtoQueue(VideoRecord videoRecord){

        Integer sectionId = videoRecord.getSectionId();
        Integer moment = videoRecord.getMoment();
        Long userId = videoRecord.getUserId();
        Integer videoId = videoRecord.getVideoId();
        //将数据写入redis
        log.info("写入redis：{}",videoRecord);
        writeRedis(videoRecord,userId);
        queue.add(new delayTask<>(new DelayDateQueue(videoId,moment,sectionId,userId), Duration.ofSeconds(20)));

    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DelayDateQueue{
        private Integer videoId;
        private Integer moment;
        private Integer sectionId;
        private Long userId;
    }

}
