package com.pilpil.web.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.pilpil.comment.entity.dto.VideoRecordDto;
import com.pilpil.comment.entity.po.VideoRecord;

/**
 * <p>
 * 视频记录表 服务类
 * </p>
 *
 * @author 
 * @since 2026-05-24
 */
public interface IVideoRecordService extends IService<VideoRecord> {

    void saveRecord(VideoRecordDto videoRecordDto);


    VideoRecord getRecord(Integer sectionId,Integer videoId);

    void playVideo(Integer sectionId, Integer videoId);
}
