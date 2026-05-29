package com.pilpil.web.entity.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ChatsVo {
    private Integer id;

    /**
     * 用户id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    /**
     * 目标id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long targetId;

    /**
     * 未读信息的数量
     */
    private Integer unread;

    /**
     * 对方姓名
     */
    private String targetName;
    /**
     * 对方头像
     */
    private String targetAvatar;
}
