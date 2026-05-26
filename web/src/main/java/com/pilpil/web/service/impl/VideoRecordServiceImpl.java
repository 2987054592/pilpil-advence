package com.pilpil.web.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.pilpil.comment.entity.dto.VideoRecordDto;
import com.pilpil.comment.entity.po.Video;
import com.pilpil.comment.entity.po.VideoDetail;
import com.pilpil.comment.entity.po.VideoRecord;
import com.pilpil.comment.exception.illegalException;
import com.pilpil.comment.utils.UserHolder;
import com.pilpil.web.mapper.VideoRecordMapper;
import com.pilpil.web.service.IVideoDetailService;
import com.pilpil.web.service.IVideoRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pilpil.web.service.IVideoService;
import com.pilpil.web.util.DelayTaskHnader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import static com.pilpil.comment.constants.Exception.exceptionConstants.Video.VIDEO_NOT_EXIST;
import static com.pilpil.comment.constants.redis.redisContanst.Video.VIDEO_PLAY_PREFIX;
import static com.pilpil.comment.constants.redis.redisContanst.Video.VIDEO_RECORD_PREFIX;

/**
 * <p>
 * 视频记录表 服务实现类
 * </p>
 *
 * @author 
 * @since 2026-05-24
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VideoRecordServiceImpl extends ServiceImpl<VideoRecordMapper, VideoRecord> implements IVideoRecordService {
    private final StringRedisTemplate redisTemplate;
    private final DelayTaskHnader delayTaskHnader;
    private final IVideoService videoService;
    private final IVideoDetailService videoDetailService;
    @Override
    public void saveRecord(VideoRecordDto videoRecordDto) {
        Long userId = UserHolder.get().getId();
        VideoRecord videoRecord = searchRecord(videoRecordDto);
        if(videoRecord==null){
            //不存在，新建
            VideoRecord bean = BeanUtil.toBean(videoRecordDto, VideoRecord.class);
            bean.setUserId(userId);
            bean.setCreateTime(videoRecordDto.getCommitTime());
            bean.setUpdateTime(videoRecordDto.getCommitTime());
            save(bean);
        }else{
            //存在，更新
            videoRecord.setMoment(videoRecordDto.getMoment());
            videoRecord.setUpdateTime(videoRecordDto.getCommitTime());
            delayTaskHnader.addtoQueue(videoRecord);
        }

    }
    public VideoRecord searchRecord(VideoRecordDto videoRecordDto){
        Long userId = UserHolder.get().getId();
        VideoRecord videoRecord = delayTaskHnader.RecordFromRedis(userId, videoRecordDto.getVideoId(), videoRecordDto.getSectionId());
        if(videoRecord!=null){
            log.info("redis查询成功：{}",videoRecord);
            return videoRecord;

        }
        VideoRecord record = lambdaQuery().eq(VideoRecord::getVideoId, videoRecordDto.getVideoId())
                .eq(VideoRecord::getSectionId, videoRecordDto.getSectionId())
                .eq(VideoRecord::getUserId, UserHolder.get().getId())
                .one();
        if(record==null){
            return null;
        }
        String key=VIDEO_RECORD_PREFIX+userId+":"+videoRecordDto.getVideoId()+":"+videoRecordDto.getSectionId();
        redisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(record));

        log.info("查询成功：{}",record);
        return record;
    }

    @Override
    public VideoRecord getRecord(Integer sectionId,Integer videoId) {
        return lambdaQuery().eq(VideoRecord::getSectionId, sectionId)
                .eq(VideoRecord::getVideoId, videoId)
                .eq(VideoRecord::getUserId, UserHolder.get().getId())
                .one();
    }

    @Override
    public void playVideo(Integer sectionId, Integer videoId) {
        Video video = videoService.lambdaQuery().eq(Video::getId, videoId).one();
        if(video==null){
            throw new illegalException(VIDEO_NOT_EXIST);
        }
        VideoDetail one = videoDetailService.lambdaQuery().eq(VideoDetail::getVideoId, videoId)
                .eq(VideoDetail::getId, sectionId).one();
        if(one==null){
            throw new illegalException(VIDEO_NOT_EXIST);
        }
        String key=VIDEO_PLAY_PREFIX+videoId;
        redisTemplate.opsForHash().increment(key,sectionId.toString(),1);

    }
}
