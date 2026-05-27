package com.pilpil.common.entity.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDate;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.pilpil.common.enums.VideoStatus;
import com.pilpil.common.enums.VideoType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 视频表
 * </p>
 *
 * @author 
 * @since 2026-05-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("video")
public class Video implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 视频名称
     */
    private String name;

    /**
     * 视频封面
     */
    private String cover;

    /**
     * 作者id
     */
    private Long authorId;

    /**
     * 视频简介
     */
    @TableField("`desc`")
    private String desc;

    private LocalDate createTime;

    /**
     * 0正常，1审核，2封禁
     */
    private VideoStatus status;

    /**
     * 0自制，1转载
     */
    private VideoType type;

    /**
     * 分类id
     */
    private Integer categoryId;

    /**
     * 标签，回车分割

     */
    private String tags;
    /**
     * 视频的总时长
     */
    private Long durationTotal;

}
