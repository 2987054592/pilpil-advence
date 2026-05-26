package com.pilpil.web.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@Data
@NoArgsConstructor
public class QueryCommentVo {
    private List<CommentVo> commentList;
    private Integer totalData;
    private Integer totalPage;
}
