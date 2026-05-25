package com.pilpil.web.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class queryDanmuDto {
    private Integer videoId;
    private Integer sectionId;
}
