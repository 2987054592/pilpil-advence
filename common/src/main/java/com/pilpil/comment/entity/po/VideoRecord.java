package com.pilpil.comment.entity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDate;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 视频记录表
 * </p>
 *
 * @author 
 * @since 2026-05-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("video_record")

@Builder

public class VideoRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 视频id
     */
    private Integer videoId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 视频的当前观看时间点，单位秒
     */
    private Integer moment;

    /**
     * 小节id
     */
    private Integer sectionId;

    /**
     * 第一次观看时间
     */
    private LocalDate createTime;

    /**
     * 最近一次观看时间
     */
    private LocalDate updateTime;


}
