package com.pilpil.comment.entity.vo;

import com.pilpil.comment.enums.VideoStatus;
import com.pilpil.comment.enums.VideoType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VideoDetails {
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
