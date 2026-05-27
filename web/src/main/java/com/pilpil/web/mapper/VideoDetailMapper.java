package com.pilpil.web.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pilpil.common.entity.po.VideoDetail;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 视频详情表 Mapper 接口
 * </p>
 *
 * @author 
 * @since 2026-05-22
 */
public interface VideoDetailMapper extends BaseMapper<VideoDetail> {


    @Select("select * from video_detail where id = #{id}")
    List<VideoDetail> selectByVideoId(Integer id);
}
