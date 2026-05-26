package com.pilpil.comment.entity.vo;

import com.pilpil.comment.entity.po.VideoDetail;
import com.pilpil.comment.enums.VideoType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class VideoDetailDto {
    private String md5;
    private String uploadId;
    private String ossKey;
    private Integer sort;
    private String partTime;
    private String videoUrl;
    private LocalDate createTime;
    private VideoType type;

}
