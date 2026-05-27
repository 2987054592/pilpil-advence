package com.pilpil.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LikeBisType {
    COMMENT(0,"comment"),
    VIDEO(1,"video"),
    DANMU(2,"danmu");
    @EnumValue
    private final Integer code;
    @JsonValue
    private final String msg;
}
