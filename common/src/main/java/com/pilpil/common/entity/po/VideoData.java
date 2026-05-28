package com.pilpil.common.entity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDate;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 视频统计数据表

 * </p>
 *
 * @author 
 * @since 2026-05-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("video_data")
@AllArgsConstructor
public class VideoData implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 视频id
     */
    private Integer videoId;

    /**
     * 播放次数
     */
    private Integer viewCount;

    /**
     * 弹幕数量
     */
    private Integer danmuCount;

    /**
     * 点赞数量
     */
    private Integer likeCount;

    /**
     * 收藏数量
     */
    private Integer collectCount;

    /**
     * 硬币数量
     */
    private Integer coinCount;

    /**
     * 更新时间
     */
    private LocalDate updateTime;
    /**
     * 评论的数量
     */
    private Integer commentCount;

    public VideoData(Integer danmuCount,Integer viewCount){
        this.danmuCount=danmuCount;
        this.viewCount=viewCount;

    }
    public VideoData(){}


}
