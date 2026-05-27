package com.pilpil.common.entity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDate;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.pilpil.common.enums.DanmuPosition;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 弹幕表
 * </p>
 *
 * @author 
 * @since 2026-05-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("danmu")
public class Danmu implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 视频的主id
     */
    private Integer videoId;

    /**
     * 小节id（副视频表的id）
     */
    private Integer sectionId;

    /**
     * 弹幕的位置
0正常滚动
1顶部
2底部

     */
    private DanmuPosition position;

    /**
     * 弹幕颜色

     */
    private String color;

    /**
     * 弹幕出现的位置（单位为秒）
     */
    private Integer moment;

    /**
     * 弹幕发送的时间
     */
    private LocalDate createTime;

    /**
     * 弹幕的内容
     */
    private String text;
    /**
     * 弹幕被点赞的数量
     */
    private Integer likeCount;

}
