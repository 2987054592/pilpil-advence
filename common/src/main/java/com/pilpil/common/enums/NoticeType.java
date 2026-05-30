package com.pilpil.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum NoticeType {
    /**
     * 1关注发视频\n2点赞\n3评论
     */
    FOLLOW(1, "关注的人发视频"), LIKE(2, "有人点赞"), COMMENT(3,"有人评论");

    @EnumValue
    private final int code;
    @JsonValue
    private final String message;

}

