package com.pilpil.web.entity.dto;

import com.pilpil.comment.enums.VideoStatus;
import com.pilpil.comment.enums.VideoType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoDto {
    private Integer id;
    private String name;
    private String cover;
    private Long authorId;
    private String desc;
    private LocalDate createTime;
    private VideoStatus status;
    private VideoType type;
    private Integer categoryId;
    private String tags;
    private Integer sort;
    private String partTime;
    private String videoUrl;
    private Integer duration;
}
