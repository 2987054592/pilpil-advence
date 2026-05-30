package com.pilpil.common.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class VideoFansMq {
    private Integer videoId;
    private Long authorId;
    private Long userId;
}
