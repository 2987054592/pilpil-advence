package com.pilpil.web.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.pilpil.common.entity.dto.queryVideo;
import com.pilpil.common.entity.po.Video;
import com.pilpil.common.entity.vo.VideoDocVo;
import com.pilpil.common.entity.vo.VideoVo;
import com.pilpil.web.entity.dto.VideoDto;
import com.pilpil.web.entity.dto.VideoDtoUpdate;
import com.pilpil.web.entity.vo.MyVideoList;

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

    MyVideoList getMyVideo(queryVideo queryVideo);

    void updateVideo(VideoDtoUpdate videoDto);

    VideoDtoUpdate updateInfo(Integer videoId);

    void deleteVideo(Integer videoId);
}
