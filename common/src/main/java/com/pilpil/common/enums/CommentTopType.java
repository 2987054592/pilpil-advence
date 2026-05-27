package com.pilpil.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommentTopType {
    TOP(1,"TOP"), NOT_TOP(0,"NOT_TOP");
    @EnumValue
    private final Integer code;
    @JsonValue
    private final String description;



}
