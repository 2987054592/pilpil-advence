package com.pilpil.web.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.pilpil.common.entity.dto.VideoRecordDto;
import com.pilpil.common.entity.po.VideoRecord;
import com.pilpil.web.entity.dto.VideoRecordDtoWeb;
import com.pilpil.web.entity.vo.ListVideoRecordVo;
import com.pilpil.web.entity.vo.VideoRecordVo;

import java.util.List;

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

    ListVideoRecordVo getVideoRecordList(VideoRecordDtoWeb videoRecordDto);
}
