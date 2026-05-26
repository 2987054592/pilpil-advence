package com.pilpil.comment.entity.po;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDate;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.pilpil.comment.entity.vo.VideoDetailDto;
import com.pilpil.comment.enums.VideoStatus;
import com.pilpil.comment.enums.VideoType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 视频详情表
 * </p>
 *
 * @author 
 * @since 2026-05-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("video_detail")
public class VideoDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 主视频id
     */
    private Integer videoId;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 分p的标题
     */
    private String partTime;

    /**
     * 视频的url地址
     */
    private String videoUrl;

    /**
     * 视频持续时长（秒）
     */
    private Long duration;

    /**
     * 上传时间

     */
    private LocalDate createTime;

    /**
     * 0正常，1审核，2封禁
     */
    private VideoStatus status;
    /**
     * 0自制，1转载
     */
    private VideoType type;




}
