package com.pilpil.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChatType {
    ME("我"),
    OTHER("对方");
    @JsonValue
    private final String name;
}
