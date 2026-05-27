package com.pilpil.web.entity.dto;

import com.pilpil.common.enums.LikeBisType;
import com.pilpil.common.enums.LikeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class likeDto {
    private LikeBisType likeBizType;
    private LikeType likeType;
    private Integer bizId;
    private Integer videoId;
}
