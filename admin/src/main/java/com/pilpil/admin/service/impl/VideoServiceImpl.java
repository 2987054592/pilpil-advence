package com.pilpil.admin.service.impl;


import cn.hutool.core.bean.BeanUtil;
import com.pilpil.admin.service.IVideoDataService;
import com.pilpil.common.constants.mq.mqConstans;
import com.pilpil.common.entity.dto.VideoFansMq;
import com.pilpil.common.entity.dto.VideoReview;
import com.pilpil.admin.mapper.VideoMapper;
import com.pilpil.admin.service.IVideoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pilpil.common.entity.dto.queryVideo;
import com.pilpil.common.entity.po.User;
import com.pilpil.common.entity.po.Video;
import com.pilpil.common.entity.po.VideoData;
import com.pilpil.common.entity.po.VideoDetail;
import com.pilpil.common.entity.vo.VideoDetails;
import com.pilpil.common.entity.vo.VideoDocVo;
import com.pilpil.common.entity.vo.VideoVo;
import com.pilpil.common.enums.VideoStatus;
import com.pilpil.common.exception.illegalException;
import com.pilpil.common.utils.Escommpent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.pilpil.common.constants.Exception.exceptionConstants.User.USER_STATUS_ERROR;
import static com.pilpil.common.constants.Exception.exceptionConstants.Video.MAIN_VIDEO_STATUS_ERROR;
import static com.pilpil.common.constants.Exception.exceptionConstants.Video.VIDEO_NOT_EXIST;

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
    private final Escommpent escommpent;
    private final VideoDetailServiceImpl videoDetailService;
    private final UserServiceImpl userService;
    private final IVideoDataService videoDataService;
    private final RabbitTemplate rabbitTemplate;
    @Override
    public VideoDocVo getVideo(queryVideo queryVideo) {
        return escommpent.searchVideo(queryVideo, queryVideo.getPageNum(), queryVideo.getPageSize());
    }

    @Override
    public VideoVo getByIdc(Integer id) {
        Video video = lambdaQuery().eq(Video::getId, id).one();
        if(video==null){
            throw new illegalException(VIDEO_NOT_EXIST);
        }
        User user = userService.getBaseMapper().selectById(video.getAuthorId());


        List<VideoDetail> list = videoDetailService.lambdaQuery().eq(VideoDetail::getVideoId, id).list();
        List<VideoDetails> videoDetails = BeanUtil.copyToList(list, VideoDetails.class);
        VideoData data = videoDataService.lambdaQuery()
                .eq(VideoData::getVideoId, id).one();
        long DanmuCount = Optional.ofNullable(data.getDanmuCount()).orElse(0);
        long PlayCount = Optional.ofNullable(data.getViewCount()).orElse(0);
        long LikeCount = Optional.ofNullable(data.getLikeCount()).orElse(0);
        long CoinCount = Optional.ofNullable(data.getCoinCount()).orElse(0);
        long collectCount = Optional.ofNullable(data.getCollectCount()).orElse(0);
        long commentCount = Optional.ofNullable(data.getCommentCount()).orElse(0);
        VideoVo bean = BeanUtil.toBean(video, VideoVo.class);
        bean.setAuthorName(user==null?USER_STATUS_ERROR:user.getNickName());
        bean.setCoinCount((int) CoinCount);
        bean.setLikeCount((int) LikeCount);
        bean.setFavoriteCount((int) collectCount);
        bean.setPlayCountTotal((int)PlayCount);
        bean.setDanmakuCountTotal((int)DanmuCount);
        bean.setCommentCount((int)commentCount);
        bean.setVideoDetails(videoDetails);
        return bean;

    }

    @Override
    public void reviewVideo(VideoReview videoReview) {
        Integer id = videoReview.getId();
        Video video = lambdaQuery().eq(Video::getId, id).one();
        List<VideoDetail> list = videoDetailService.lambdaQuery().eq(VideoDetail::getVideoId, id).list();
        if (videoReview.getStatus().equals(VideoStatus.BAN)) {

            List<Integer> detailIds = list.stream()
                    .map(VideoDetail::getId)
                    .toList();

            videoDetailService.lambdaUpdate()
                    .set(VideoDetail::getStatus, VideoStatus.BAN)
                    .in(VideoDetail::getId, detailIds)
                    .update();
        }
        video.setStatus(videoReview.getStatus());
        updateById( video);
        escommpent.reviewVideo(videoReview);

        if (video.getStatus().equals(VideoStatus.NORMAL)) {
            VideoFansMq fansMq = VideoFansMq.builder()
                    .authorId(video.getAuthorId())
                    .videoId(video.getId())
                    .build();
            rabbitTemplate.convertAndSend(
                    mqConstans.Exchange.VIDEO_FANS_EXCHANGE,
                    mqConstans.Key.VIDEO_FANS_KEY,
                    fansMq
            );
        }

    }

    @Override
    public void reviewVideos(VideoReview videoReview) {
        Integer id = videoReview.getId();
        VideoDetail one = videoDetailService.lambdaQuery().eq(VideoDetail::getId, id).one();
        Video video = lambdaQuery().eq(Video::getId, one.getVideoId()).one();
        if(!video.getStatus().equals(VideoStatus.NORMAL)){
            throw new illegalException(MAIN_VIDEO_STATUS_ERROR);
        }
        one.setStatus(videoReview.getStatus());
        videoDetailService.updateById(one);
    }
}
