package com.pilpil.comment.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusType {
    NORMAL(0, "正常"),
    BAN(1, "封禁");
    @EnumValue
    private final Integer code;
    @JsonValue
    private final String message;
}
