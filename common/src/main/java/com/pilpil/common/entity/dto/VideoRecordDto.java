package com.pilpil.common.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class VideoRecordDto {
    private Integer videoId;
    private Integer moment;
    private Integer sectionId;
    private LocalDate commitTime;
}
