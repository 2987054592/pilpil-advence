package com.pilpil.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SexType {
    MALE(1,"男"),
    FEMALE(0,"女"),
    UNKNOWN(2,"未知");
    @EnumValue
    private final int sex;
    @JsonValue
    private final String desc;
}
