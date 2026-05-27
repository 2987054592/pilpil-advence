package com.pilpil.common.entity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.pilpil.common.enums.LikeBisType;
import com.pilpil.common.enums.LikeType;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 点赞表
 * </p>
 *
 * @author 
 * @since 2026-05-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("`like`")
@Builder
public class Like implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 点赞人的id
     */

    private Long userId;

    /**
     * 0评论，1视频，2弹幕
     */
    private LikeBisType likeBizType;

    /**
     * 被点赞类型的id
     */
    private Integer bizId;
    /**
     * 视频id
     */
    private Integer videoId;


}
