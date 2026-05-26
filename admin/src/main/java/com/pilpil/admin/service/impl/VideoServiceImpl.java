package com.pilpil.admin.service.impl;


import cn.hutool.core.bean.BeanUtil;
import com.pilpil.comment.entity.dto.VideoReview;
import com.pilpil.admin.mapper.VideoMapper;
import com.pilpil.admin.service.IVideoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pilpil.comment.entity.dto.queryVideo;
import com.pilpil.comment.entity.po.User;
import com.pilpil.comment.entity.po.Video;
import com.pilpil.comment.entity.po.VideoDetail;
import com.pilpil.comment.entity.vo.VideoDetails;
import com.pilpil.comment.entity.vo.VideoDocVo;
import com.pilpil.comment.entity.vo.VideoVo;
import com.pilpil.comment.enums.VideoStatus;
import com.pilpil.comment.exception.illegalException;
import com.pilpil.comment.utils.Escommpent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.pilpil.comment.constants.Exception.exceptionConstants.Video.MAIN_VIDEO_STATUS_ERROR;
import static com.pilpil.comment.constants.Exception.exceptionConstants.Video.VIDEO_NOT_EXIST;

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
    @Override
    public VideoDocVo getVideo(queryVideo queryVideo) {
        return escommpent.searchVideo(queryVideo, queryVideo.getPageNum(), queryVideo.getPageSize());
    }

    @Override
    public VideoVo getByIdc(Integer id) {
        Video video = lambdaQuery().eq(Video::getId, id).one();
        User user = userService.getBaseMapper().selectById(video.getAuthorId());
        if(video==null){
            throw new illegalException(VIDEO_NOT_EXIST);
        }
        List<VideoDetail> list = videoDetailService.lambdaQuery().eq(VideoDetail::getVideoId, id).list();
        List<VideoDetails> videoDetails = BeanUtil.copyToList(list, VideoDetails.class);
        //TODO播放量，收藏，点赞，投币，弹幕
        VideoVo bean = BeanUtil.toBean(video, VideoVo.class);
        bean.setAuthorName(user.getNickName());
        bean.setCoinCount(0);
        bean.setLikeCount(0);
        bean.setFavoriteCount(0);
        bean.setPlayCountTotal(0);
        bean.setDanmakuCountTotal(0);

        bean.setVideoDetails(videoDetails);
        return bean;

    }

    @Override
    public void reviewVideo(VideoReview videoReview) {
        Integer id = videoReview.getId();
        Video video = lambdaQuery().eq(Video::getId, id).one();
        List<VideoDetail> list = videoDetailService.lambdaQuery().eq(VideoDetail::getVideoId, id).list();
        if(videoReview.getStatus().equals(VideoStatus.BAN)){
            list.forEach(videoDetail -> videoDetail.setStatus(videoReview.getStatus()));
        }
        videoDetailService.updateBatchById(list);
        video.setStatus(videoReview.getStatus());
        updateById( video);
        escommpent.reviewVideo(videoReview);
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
