package com.pilpil.comment.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter

public enum VideoStatus {
    NORMAL(0, "正常"),
    AUDIT(1, "审核中"),
    BAN(2, "封禁");
    @EnumValue
    private final Integer code;
    @JsonValue
    private final String message;
    VideoStatus(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
