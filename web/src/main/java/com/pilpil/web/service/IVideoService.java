package com.pilpil.web.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.pilpil.comment.entity.dto.queryVideo;
import com.pilpil.comment.entity.po.Video;
import com.pilpil.comment.entity.po.VideoDoc;
import com.pilpil.comment.entity.vo.VideoDocVo;
import com.pilpil.comment.entity.vo.VideoVo;
import com.pilpil.web.entity.dto.VideoDto;

/**
 * <p>
 * 视频表 服务类
 * </p>
 *
 * @author 
 * @since 2026-05-22
 */
public interface IVideoService extends IService<Video> {

    void saveVideo(VideoDto videoDto);

    VideoDocVo getVideo(queryVideo queryVideo);

    VideoVo getByIdc(Integer id);
}
