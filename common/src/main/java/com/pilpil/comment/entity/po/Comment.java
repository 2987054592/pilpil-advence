package com.pilpil.comment.entity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDate;

import com.pilpil.comment.enums.CommentTopType;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 评论表
 * </p>
 *
 * @author 
 * @since 2026-05-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("comment")
@Builder
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 视频id，多分p的评论都放在主视频表下
     */
    private Integer videoId;

    /**
     * 发送评论人的id
     */
    private Long authorId;

    /**
     * 根id，为0则为根节点
     */
    private Integer rootId;

    /**
     * 被回复人的id
     */
    private Long targetId;

    /**
     * 回复的内容
     */
    private String content;

    /**
     * 该条评论被点赞的数量
     */
    private Integer likeCount;

    /**
     * 是否置顶
0不置顶，1置顶
     */
    private CommentTopType top;

    /**
     * 评论的数量
     */
    private Integer replyCount;
    /**
     * 被回复评论的评论的id
     */
    private Integer targetCommentId;
    /**
     * 评论时间
     */
    private LocalDate createTime;


}
