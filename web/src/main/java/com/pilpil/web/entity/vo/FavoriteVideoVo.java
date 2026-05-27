package com.pilpil.web.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Data
public class FavoriteVideoVo {
    private Integer videoId;
    private String cover;
    private Integer viewCount;
    private Integer danmuCount;
    private Long durationTotal;
    private String VideoName;
    private String authorName;
    private LocalDate createTime;

}
