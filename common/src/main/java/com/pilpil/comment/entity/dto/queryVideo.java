package com.pilpil.comment.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
}
