package com.pilpil.common.entity.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDate;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.pilpil.common.enums.IsRead;
import com.pilpil.common.enums.NoticeType;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户通知表
 * </p>
 *
 * @author 
 * @since 2026-05-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_notice")
@Builder
public class UserNotice implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 要通知给谁
     */
    private Long userId;

    /**
     * 谁发送的
     */
    private Long fromUid;

    /**
     * 1关注发视频
2点赞
3评论
     */
    private NoticeType type;

    /**
     * 视频id
     */
    private Integer videoId;

    /**
     * 通知的内容
     */
    private String content;

    /**
     * 0未读
1读了
     */
    @TableField("`read`")
    private IsRead read;

    private LocalDate createTime;


}
