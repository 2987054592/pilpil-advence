package com.pilpil.web.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CommentDto {
    private Integer videoId;
    private Integer rootId;
    private Long targetId;
    private String content;
    private Integer targetCommentId;

}
