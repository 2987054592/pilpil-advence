package com.pilpil.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum IsRead {
    UNREAD(0,"未读"),
    READ(1,"已读");
    @EnumValue
    private final Integer code;
    @JsonValue
    private final String message;
}
