package com.pilpil.common.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class queryVideo {
    private Integer pageSize;
    private Integer pageNum;
    private String name;
    private String categoryId;
    private String tags;
    private Long userId;
}
