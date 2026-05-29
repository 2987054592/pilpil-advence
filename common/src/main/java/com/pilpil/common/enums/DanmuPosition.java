package com.pilpil.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
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

    @JsonCreator
    public static DanmuPosition getByCode(Object code) {
        if (code == null) {
            return NORMAL;
        }

        int value;
        try {
            // 兼容字符串 "0" 和数字 0
            value = Integer.parseInt(code.toString());
        } catch (Exception e) {
            return NORMAL;
        }

        for (DanmuPosition position : values()) {
            if (position.code == value) {
                return position;
            }
        }
        return NORMAL;
    }
}
