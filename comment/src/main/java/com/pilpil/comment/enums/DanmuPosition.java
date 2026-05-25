package com.pilpil.comment.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DanmuPosition {
    NORMAL(0, "normal"),
    TOP(1, "top"),
    BOTTOM(2, "bottom");
    @EnumValue
    private final int code;
    @JsonValue
    private final String name;
}
