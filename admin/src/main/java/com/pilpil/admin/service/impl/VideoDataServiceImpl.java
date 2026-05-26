package com.pilpil.admin.service.impl;

import com.pilpil.admin.mapper.VideoDataMapper;
import com.pilpil.admin.service.IVideoDataService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pilpil.comment.entity.po.VideoData;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 视频统计数据表
 服务实现类
 * </p>
 *
 * @author 
 * @since 2026-05-25
 */
@Service
public class VideoDataServiceImpl extends ServiceImpl<VideoDataMapper, VideoData> implements IVideoDataService {

}
