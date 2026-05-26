package com.pilpil.comment.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

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
