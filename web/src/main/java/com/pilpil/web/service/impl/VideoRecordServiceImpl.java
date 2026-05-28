package com.pilpil.web.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pilpil.common.entity.dto.VideoRecordDto;
import com.pilpil.common.entity.po.User;
import com.pilpil.common.entity.po.Video;
import com.pilpil.common.entity.po.VideoDetail;
import com.pilpil.common.entity.po.VideoRecord;
import com.pilpil.common.exception.illegalException;
import com.pilpil.common.utils.UserHolder;
import com.pilpil.web.entity.dto.VideoRecordDtoWeb;
import com.pilpil.web.entity.vo.ListVideoRecordVo;
import com.pilpil.web.entity.vo.VideoRecordVo;
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

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.pilpil.common.constants.Exception.exceptionConstants.Video.VIDEO_DELETE_ERROR;
import static com.pilpil.common.constants.Exception.exceptionConstants.Video.VIDEO_NOT_EXIST;
import static com.pilpil.common.constants.redis.redisContanst.Video.VIDEO_PLAY_PREFIX;
import static com.pilpil.common.constants.redis.redisContanst.Video.VIDEO_RECORD_PREFIX;

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
        Integer videoId = videoRecordDto.getVideoId();
        Video video = videoService.getById(videoId);
        if(video==null){
            throw new illegalException(VIDEO_NOT_EXIST);
        }
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

    @Override
    public ListVideoRecordVo getVideoRecordList(VideoRecordDtoWeb videoRecordDto) {
        Long userId = UserHolder.get().getId();


        Page<VideoRecord> page = lambdaQuery()
                .eq(VideoRecord::getUserId, userId)
                .page(new Page<>(videoRecordDto.getPageNum(), videoRecordDto.getPageSize()));

        ListVideoRecordVo vo = new ListVideoRecordVo();
        if (page == null || page.getRecords().isEmpty()) {
            vo.setList(Collections.emptyList());
            vo.setTotal(0);
            vo.setPageSize(0);
            return vo;
        }


        List<VideoRecord> records = page.getRecords();
        Set<Integer> videoIds = records.stream()
                .map(VideoRecord::getVideoId)
                .collect(Collectors.toSet());


        Map<Integer, Video> videoMap = videoService.lambdaQuery()
                .in(Video::getId, videoIds)
                .like(videoRecordDto.getVideoName()!=null,Video::getName, videoRecordDto.getVideoName())
                .list()
                .stream()
                .collect(Collectors.toMap(Video::getId, c -> c));

        List<VideoRecordVo> vos=new ArrayList<>();

        for(VideoRecord record:records){
            VideoRecordVo vo1 = new VideoRecordVo();
            vo1.setVideoName(Optional.ofNullable(videoMap.get(record.getVideoId())).map(Video::getName).orElse(VIDEO_NOT_EXIST));
            vo1.setCover(Optional.ofNullable(videoMap.get(record.getVideoId())).map(Video::getCover).orElse(""));
            vo1.setTime(record.getUpdateTime());
            vo1.setMoment(record.getMoment());
            vo1.setDurationTotal(Optional.ofNullable(videoMap.get(record.getVideoId())).map(Video::getDurationTotal).orElse(0L));
            vo1.setVideoId(record.getVideoId());
            vos.add(vo1);
        }
        vo.setList(vos);
        vo.setTotal((int) page.getTotal());
        vo.setPageSize((int) page.getSize());
        return vo;
    }
}
