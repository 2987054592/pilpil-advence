package com.pilpil.web.entity.vo;

import com.pilpil.common.enums.DanmuPosition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DanmuVo {
    private Integer id;
    private String text;
    private Integer moment;
    private DanmuPosition position;
    private String color;
    private Long userId;
    private Integer likeCount;
}
