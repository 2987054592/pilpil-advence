package com.pilpil.web.entity.vo;

import com.pilpil.common.enums.VideoStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MyVideoVo {
    private Integer id;
    private String name;
    private String cover;
    private Long durationTotal;
    private VideoStatus status;
    private String authorName;
    private Integer viewCount;
    private Integer danmuCount;
}
