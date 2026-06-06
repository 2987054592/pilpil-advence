package com.pilpil.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommentStatus {
    NORMAL(0,"正常"),
    DELETE(1,"删除");
    @EnumValue
    private final Integer code;
    @JsonValue
    private final String message;
}
