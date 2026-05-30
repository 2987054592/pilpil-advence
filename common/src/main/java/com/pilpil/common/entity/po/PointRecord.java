package com.pilpil.common.entity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 经验榜
 * </p>
 *
 * @author 
 * @since 2026-05-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("point_record")
public class PointRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户的id
     */
    private Long userId;

    /**
     * 0签到
1评论
2投币
     */
    private Integer type;

    /**
     * 具体多少分
     */
    private Integer points;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
