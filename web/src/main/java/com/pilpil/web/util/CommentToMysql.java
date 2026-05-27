package com.pilpil.web.util;

import com.pilpil.common.entity.po.Comment;
import com.pilpil.common.entity.po.VideoData;
import com.pilpil.web.service.ICommentService;
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

import static com.pilpil.common.constants.redis.redisContanst.Comment.COMMENT_LIST_PREFIX;
import static com.pilpil.common.constants.redis.redisContanst.Comment.COMMENT_TOTAL;
import static com.pilpil.common.constants.redis.redisContanst.Like.LIKE_COMMENT_PREFIX;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommentToMysql {
    private final StringRedisTemplate redisTemplate;
    private final IVideoDataService videoDataService;
    private final ICommentService commentService;
    @Scheduled(cron = "0 0/1 * * * ?")
    @Transactional
    public void run(){
        log.info("开始同步评论相关");
        Comment();
        CommentLike();
        log.info("评论相关同步完成");
    }

    @PreDestroy
    public void PreDestroy(){
        log.info("程序退出，开始同步评论相关");
        Comment();
    }
    private void Comment() {
        Set<String> keys = redisTemplate.keys(COMMENT_LIST_PREFIX + "*");
        for (String key : keys){
            String videoIdStr = key.replace(COMMENT_LIST_PREFIX, "");
            int videoId = Integer.parseInt(videoIdStr);
            Map<Object, Object> HashData = redisTemplate.opsForHash().entries(key);
            HashData.forEach((sectionObj,countObj)->{
                String section = sectionObj.toString();
                int addCount = Integer.parseInt(countObj.toString());
                if(section.equals(COMMENT_TOTAL)){
                    boolean update = videoDataService.lambdaUpdate()
                            .eq(VideoData::getVideoId, videoId)
                            .setSql("comment_count = IFNULL(comment_count, 0) + " + addCount)
                            .set(VideoData::getUpdateTime, LocalDate.now())
                            .update();
                    if(update){
                        redisTemplate.opsForHash().delete(key,COMMENT_TOTAL);
                    }else{
                        log.info("数据不存在，等待下次更新");
                    }
                }else{
                    int CommentId = Integer.parseInt(section);
                    commentService.lambdaUpdate()
                            .eq(Comment::getId, CommentId)
                            .setSql("reply_count = IFNULL(reply_count, 0) + " + addCount)
                            .update();
                    redisTemplate.opsForHash().delete(key,section);
                }
            });
        }
    }
    private void CommentLike(){
        Set<String> keys = redisTemplate.keys(LIKE_COMMENT_PREFIX + "*");
        if(keys==null || keys.isEmpty()){
            return;
        }
        for (String key : keys){
            Map<Object, Object> HashData = redisTemplate.opsForHash().entries(key);
            HashData.forEach((sectionObj,countObj)->{
                int bizId = Integer.parseInt(sectionObj.toString());
                int addCount = Integer.parseInt(countObj.toString());
                commentService.lambdaUpdate()
                        .eq(Comment::getId, bizId)
                        .setSql("like_count = IFNULL(like_count, 0) + " + addCount)
                        .update();
            });
            redisTemplate.delete(key);
        }
    }

}
