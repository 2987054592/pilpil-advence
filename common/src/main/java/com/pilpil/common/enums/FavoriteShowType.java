package com.pilpil.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FavoriteShowType {
    SHOW(0,"显示"),
    HIDE(1,"隐藏");
    @EnumValue
    private final int code;
    @JsonValue
    private final String message;
}
