package com.pilpil.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LikeType {
    LIKE(1,"点赞"),
    NO_LIKE(0,"取消点赞");
    @EnumValue
    private final Integer code;
    @JsonValue
    private final String msg;


}
