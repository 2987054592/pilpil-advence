package com.pilpil.comment.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

public enum LevelType {
    LV0(0,"LV0"),
    LV1(1,"LV1"),
    LV2(2,"LV2"),
    LV3(3,"LV3"),
    LV4(4,"LV4"),
    LV5(5,"LV5"),
    LV6(6,"LV6");

    @EnumValue
    private final int level;
    @JsonValue
    private final String desc;

    LevelType(int level,String desc){
        this.level = level;
        this.desc = desc;
    }
    public int getLevel(){
        return level;
    }
    public String getDesc(){
        return desc;
    }

}
