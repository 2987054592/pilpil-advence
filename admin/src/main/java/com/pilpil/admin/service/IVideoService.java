package com.pilpil.admin.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.pilpil.comment.entity.dto.VideoReview;
import com.pilpil.comment.entity.dto.queryVideo;
import com.pilpil.comment.entity.po.Video;
import com.pilpil.comment.entity.vo.VideoDocVo;
import com.pilpil.comment.entity.vo.VideoVo;

/**
 * <p>
 * 视频表 服务类
 * </p>
 *
 * @author 
 * @since 2026-05-22
 */
public interface IVideoService extends IService<Video> {
    VideoDocVo getVideo(queryVideo queryVideo);

    VideoVo getByIdc(Integer id);

    void reviewVideo(VideoReview videoReview);

    void reviewVideos(VideoReview videoReview);
}
