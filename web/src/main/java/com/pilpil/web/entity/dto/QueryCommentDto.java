package com.pilpil.web.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryCommentDto {
    private Integer pageSize;
    private Integer pageNum;
    private Integer videoId;
    private Integer commentId;
}
