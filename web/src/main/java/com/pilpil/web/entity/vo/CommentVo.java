package com.pilpil.web.entity.vo;

import com.pilpil.comment.enums.CommentTopType;
import com.pilpil.comment.enums.LevelType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class CommentVo {
    private Integer id;
    private String content;
    private Long authorId;
    private String authorName;
    private String authorAvatar;
    private LevelType level;
    private Integer rootId;
    private Integer likeCount;
    private Integer replyCount;
    private CommentTopType top;
    private Integer targetCommentId;
    private LocalDate createTime;

}
