package com.pilpil.common.entity.dto;

import com.pilpil.common.enums.VideoStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VideoReview {
    private Integer id;
    private VideoStatus status;
}
