package com.pilpil.common.entity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDate;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.pilpil.common.enums.LevelType;
import com.pilpil.common.enums.SexType;
import com.pilpil.common.enums.StatusType;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户信息
 * </p>
 *
 * @author 
 * @since 2026-05-21
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user")
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 密码
     */
    private String password;

    /**
     * 用户名称
     */
    private String nickName;

    /**
     * 用户头像的地址
     */
    private String avatar;

    /**
     * 拥有过的硬币数
     */
    private Integer totalCoin;

    /**
     * 当前硬币数量
     */
    private Integer currentCoin;

    /**
     * 创建时间
     */
    private LocalDate createTime;

    /**
     * 最后一次登录时间
     */
    private LocalDate lastTime;

    /**
     * 等级
     */
    private LevelType level;

    /**
     * 个人简介
     */
    private String introduction;

    /**
     * 0女1男2未知\n
     */
    private SexType sex;

    /**
     * 经验
     */
    private Integer experience;

    /**
     * 0正常1封禁
     */
    private StatusType status;

    /**
     * 主页背景图url
     */
    private String background;

    public User(String nickName,String avatar,LevelType level,Long id){
        this.nickName=nickName;
        this.avatar=avatar;
        this.level=level;
        this.id=id;
    }
    public User(String nickName,String avatar,LevelType level){
        this.nickName=nickName;
        this.avatar=avatar;
        this.level=level;
    }


}
