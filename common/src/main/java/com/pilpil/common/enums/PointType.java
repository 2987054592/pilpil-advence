package com.pilpil.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PointType {
    SIGN_IN(0, "签到",0),
    COMMENT(1, "评论",20),
    COIN(2, "投币",20);
    @EnumValue
    private final Integer code;
    @JsonValue
    private final String desc;

    private final Integer maxPoint;

}
