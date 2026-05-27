package com.pilpil.common.entity.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDate;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.pilpil.common.enums.FavoriteShowType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 收藏夹
 * </p>
 *
 * @author 
 * @since 2026-05-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("favorite")
public class Favorite implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 所属用户id
     */
    private Long userId;

    /**
     * 封面
     */
    private String cover;

    /**
     * 描述
     */
    @TableField("`desc`")
    private String desc;

    /**
     * 创建时间
     */
    private LocalDate createTime;

    /**
     * 收藏夹内视频的数量
     */
    private Integer count;

    /**
     * 0对外开放
1隐私
     */
    private FavoriteShowType visible;
    /**
     * 收藏夹名称
     */
    private String name;



}
