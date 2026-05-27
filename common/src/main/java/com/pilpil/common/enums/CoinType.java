package com.pilpil.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CoinType {
    ONE(1,"1枚"),
    TWO(2,"2枚");
    @EnumValue
    private final Integer code;
    @JsonValue
    private final String msg;
}
