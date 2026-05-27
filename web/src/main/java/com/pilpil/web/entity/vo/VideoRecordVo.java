package com.pilpil.web.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class VideoRecordVo {
    private Integer videoId;
    private LocalDate time;
    private String cover;
    private Integer moment;
    private Long durationTotal;
    private String VideoName;

}

