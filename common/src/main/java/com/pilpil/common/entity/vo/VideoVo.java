package com.pilpil.common.entity.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.pilpil.common.enums.VideoStatus;
import com.pilpil.common.enums.VideoType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class VideoVo {
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
     * 作者名称
     */
    private String authorName;

    /**
     * 视频简介
     */
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

    private List<VideoDetails> videoDetails;

    private Integer playCountTotal;

    private Integer danmakuCountTotal;

    private Integer likeCount;

    private Integer coinCount;

    private Integer favoriteCount;

    private UserVo userVo;

    private Integer commentCount;

}
