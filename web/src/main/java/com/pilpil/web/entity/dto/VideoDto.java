package com.pilpil.web.entity.dto;

import com.pilpil.common.entity.vo.VideoDetailDto;
import com.pilpil.common.enums.VideoType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoDto {
    private String name;
    private String cover;
    private String desc;
    private LocalDate createTime;
    private VideoType type;
    private Integer categoryId;
    private String tags;
    private List<VideoDetailDto> videoDetailDtos;
}
