package com.pilpil.common.entity.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoDoc implements Serializable {
    // 主键
    private Integer videoId;
    private String name;
    private String tags;
    private Integer categoryId;
    private String authorName;
    private Long playCount;
    private Long danmakuCount;
    private String cover;
    private List<String> videoUrls;
    private Long totalDuration;
    private LocalDateTime createTime;
    private Integer status;
}