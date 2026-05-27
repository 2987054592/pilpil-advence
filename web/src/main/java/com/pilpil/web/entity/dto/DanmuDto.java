package com.pilpil.web.entity.dto;

import com.pilpil.common.enums.DanmuPosition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class DanmuDto {
    private Integer videoId;
    private Integer sectionId;
    private String color;
    private Integer moment;
    private DanmuPosition position;
    private String text;
}
