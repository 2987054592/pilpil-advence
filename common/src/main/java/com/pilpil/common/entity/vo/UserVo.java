package com.pilpil.common.entity.vo;


import com.pilpil.common.enums.LevelType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserVo {
    private String nickName;
    private String avatar;
    private Integer currentCoin;
    private LevelType level;
    private Integer experience;
    private String introduction;
    private Integer fans;
    private Integer follow;


}
