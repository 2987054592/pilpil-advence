package com.pilpil.web.entity.dto;

import com.pilpil.comment.entity.vo.VideoDetailDto;
import com.pilpil.comment.enums.VideoStatus;
import com.pilpil.comment.enums.VideoType;
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
