package com.pilpil.comment.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum VideoType {
    OWNER(1, "自制"),
    SHARE(2,"转载");
    @EnumValue
    private final Integer code;
    @JsonValue
    private final String message;
    VideoType(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
