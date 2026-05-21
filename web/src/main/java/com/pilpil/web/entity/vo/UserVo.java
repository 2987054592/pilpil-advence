package com.pilpil.web.entity.vo;


import com.pilpil.comment.enums.LevelType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserVo {
    private String nick_name;
    private String avatar;
    private Integer currentCoin;
    private LevelType level;
    private Integer experience;


}
