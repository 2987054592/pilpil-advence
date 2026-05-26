package com.pilpil.web.service.impl;


import cn.hutool.core.bean.BeanUtil;
import com.pilpil.comment.constants.mq.mqConstans;
import com.pilpil.comment.constants.redis.redisContanst;
import com.pilpil.comment.entity.dto.queryVideo;
import com.pilpil.comment.entity.po.*;
import com.pilpil.comment.entity.vo.VideoDetailDto;
import com.pilpil.comment.entity.vo.VideoDetails;
import com.pilpil.comment.entity.vo.VideoDocVo;
import com.pilpil.comment.entity.vo.VideoVo;
import com.pilpil.comment.enums.StatusType;
import com.pilpil.comment.enums.VideoStatus;
import com.pilpil.comment.exception.illegalException;
import com.pilpil.comment.utils.Escommpent;
import com.pilpil.comment.utils.FileOperater;
import com.pilpil.comment.utils.UserHolder;

import com.pilpil.web.entity.dto.VideoDto;
import com.pilpil.web.mapper.VideoMapper;
import com.pilpil.web.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import com.pilpil.comment.entity.vo.UserVo;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static com.pilpil.comment.constants.Exception.exceptionConstants.Category.CATEGORY_NOT_EXIST;
import static com.pilpil.comment.constants.Exception.exceptionConstants.User.USER_STATUS_ERROR;
import static com.pilpil.comment.constants.Exception.exceptionConstants.Video.VIDEO_NOT_EXIST;
import static com.pilpil.comment.constants.Exception.exceptionConstants.Video.VIDEO_STATUS_ERROR;

/**
 * <p>
 * 视频表 服务实现类
 * </p>
 *
 * @author 
 * @since 2026-05-22
 */
@Service
@RequiredArgsConstructor
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video> implements IVideoService {
    private final IVideoDetailService videoDetailService;
    private final FileOperater fileOperater;
    private final ICategoryService categoryService;
    private final StringRedisTemplate redisTemplate;
    private final IUserService userService;
    private final RabbitTemplate rabbitTemplate;
    private final Escommpent escommpent;
    private final IVideoDataService videoDataService;
    @Override
    public void saveVideo(VideoDto videoDto) {
        User user = userService.getBaseMapper().selectById(UserHolder.get().getId());
        if (user.getStatus().equals(StatusType.BAN)) {
            throw new illegalException(USER_STATUS_ERROR);
        }
        //String durationKey = redisContanst.Video.DURATION_PREFIX + fileId + ":" + uploadId + ":" + ossKey;
        if(videoDto.getCategoryId()!=null) {
            Category category = categoryService.getBaseMapper().selectById(videoDto.getCategoryId());
            if (category == null) {
                throw new illegalException(CATEGORY_NOT_EXIST);
            }
        }
        videoDto.setCreateTime(LocalDate.now());
        List<VideoDetailDto> videoDetailDtos = videoDto.getVideoDetailDtos();
        Video video = BeanUtil.toBean(videoDto, Video.class);
        video.setStatus(VideoStatus.AUDIT);
        System.out.println(UserHolder.get().getId());
        video.setAuthorId(UserHolder.get().getId());
        AtomicLong totalDuration  = new AtomicLong();
        save(video);
        List<String> urls=new ArrayList<>();
        List<VideoDetail> newDate=new ArrayList<>();
        videoDetailDtos.forEach(videoDetailDto -> {
            videoDetailDto.setCreateTime(video.getCreateTime());
            
            String durationKey = redisContanst.Video.DURATION_PREFIX + videoDetailDto.getMd5();
            String s = redisTemplate.opsForValue().get(durationKey);
            
            System.out.println("获取时长 - Key: " + durationKey + ", Value: " + s);
            
            VideoDetail bean = BeanUtil.toBean(videoDetailDto, VideoDetail.class);
            bean.setStatus(VideoStatus.AUDIT);
            urls.add(bean.getVideoUrl());
            
            if (s != null && !s.isEmpty()) {
                try {
                    bean.setDuration(Long.parseLong(s));
                    totalDuration.addAndGet(bean.getDuration());
                    System.out.println("成功设置时长: " + bean.getDuration());
                } catch (NumberFormatException e) {
                    System.out.println("解析时长失败，使用默认值0: " + s);
                    bean.setDuration(0L);
                }finally {
                    redisTemplate.delete(durationKey);
                }
            } else {
                System.out.println("Redis中无时长数据，使用默认值0");
                bean.setDuration(0L);
            }
            
            bean.setVideoId(video.getId());
            newDate.add(bean);
            
            fileOperater.confirmFile(videoDetailDto.getMd5(),videoDetailDto.getUploadId(),videoDetailDto.getOssKey());
        });


        VideoDoc build = VideoDoc.builder()
                .cover(videoDto.getCover())
                .name(videoDto.getName())
                .authorName(user.getNickName())
                .categoryId(videoDto.getCategoryId())
                .tags(videoDto.getTags())
                .createTime(LocalDateTime.now())
                .videoId(video.getId())
                .danmakuCount(0L)
                .playCount(0L)
                .totalDuration(totalDuration.get())
                .videoUrls(urls)
                .status(video.getStatus().getCode())
                .build();
        rabbitTemplate.convertAndSend(
                mqConstans.Exchange.VIDEO_EXCHANGE,
                mqConstans.Key.VIDEO_KEY,
                build
        );
        video.setDurationTotal(totalDuration.get());
        updateById(video);
        videoDetailService.saveBatch(newDate);
    }

    @Override
    public VideoDocVo getVideo(queryVideo queryVideo) {

        Integer pageSize = queryVideo.getPageSize();
        Integer pageNum = queryVideo.getPageNum();
        VideoDocVo vo= escommpent.searchVideo(queryVideo,pageNum,pageSize);
        List<VideoDoc> videoDocs = vo.getVideoDocs();
        List<VideoDoc> list = videoDocs.stream().filter(videoDoc -> videoDoc.getStatus().equals(VideoStatus.NORMAL.getCode())).toList();
        for(VideoDoc videoDoc:list){
            List<VideoData> list1 = videoDataService.lambdaQuery().eq(VideoData::getVideoId, videoDoc.getVideoId()).list();
            long DanmuCount = list1.stream().collect(Collectors.summarizingInt(VideoData::getDanmuCount)).getSum();
            long PlayCount = list1.stream().collect(Collectors.summarizingInt(VideoData::getViewCount)).getSum();
            videoDoc.setPlayCount(PlayCount);
            videoDoc.setDanmakuCount(DanmuCount);
        }
        vo.setVideoDocs(list);
        return vo;
    }

    @Override
    public VideoVo getByIdc(Integer id) {
        Video video = lambdaQuery().eq(Video::getId, id).one();
        if(video==null){
            throw new illegalException(VIDEO_NOT_EXIST);
        }
        if(video.getStatus().equals(VideoStatus.AUDIT) || video.getStatus().equals(VideoStatus.BAN)){
            throw new illegalException(VIDEO_STATUS_ERROR);
        }

        User user = userService.getBaseMapper().selectById(video.getAuthorId());


        List<VideoDetail> list = videoDetailService.lambdaQuery().eq(VideoDetail::getVideoId, id).list();
        List<VideoDetail> list1 = list.stream().filter(
                videoDetail -> videoDetail.getStatus().equals(VideoStatus.NORMAL)
        ).toList();
        List<VideoDetails> videoDetails = BeanUtil.copyToList(list1, VideoDetails.class);
        VideoVo bean = BeanUtil.toBean(video, VideoVo.class);
        UserVo uservo = BeanUtil.toBean(user, UserVo.class);
        long DanmuCount = videoDataService.lambdaQuery().eq(VideoData::getVideoId, id).list().stream().collect(Collectors.summarizingInt(VideoData::getDanmuCount)).getSum();
        long PlayCount = videoDataService.lambdaQuery().eq(VideoData::getVideoId, id).list().stream().collect(Collectors.summarizingInt(VideoData::getViewCount)).getSum();
        long LikeCount = videoDataService.lambdaQuery().eq(VideoData::getVideoId, id).list().stream().collect(Collectors.summarizingInt(VideoData::getLikeCount)).getSum();
        long CoinCount = videoDataService.lambdaQuery().eq(VideoData::getVideoId, id).list().stream().collect(Collectors.summarizingInt(VideoData::getCoinCount)).getSum();
        long collectCount = videoDataService.lambdaQuery().eq(VideoData::getVideoId, id).list().stream().collect(Collectors.summarizingInt(VideoData::getCollectCount)).getSum();
        long commentCount = videoDataService.lambdaQuery().eq(VideoData::getVideoId, id).list().stream().collect(Collectors.summarizingInt(VideoData::getCommentCount)).getSum();
        //TODO粉丝，关注
        uservo.setFans(0);
        uservo.setFollow(0);
        bean.setAuthorName(user.getNickName());
        bean.setCoinCount((int) CoinCount);
        bean.setLikeCount((int) LikeCount);
        bean.setFavoriteCount((int) collectCount);
        bean.setPlayCountTotal((int) PlayCount);
        bean.setDanmakuCountTotal((int) DanmuCount);
        bean.setUserVo(uservo);
        bean.setVideoDetails(videoDetails);
        bean.setCommentCount((int) commentCount);
        return bean;

    }
}
