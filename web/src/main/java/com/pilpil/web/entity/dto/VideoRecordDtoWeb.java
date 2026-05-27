package com.pilpil.web.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class VideoRecordDtoWeb {
    private Integer pageSize;
    private Integer pageNum;
    private String videoName;
}
