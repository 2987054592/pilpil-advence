package com.pilpil.common.entity.vo;

import com.pilpil.common.enums.SexType;
import com.pilpil.common.enums.StatusType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserVoDetail {
    private Long id;
    private String email;
    private String nickName;
    private String avatar;
    private Integer currentCoin;
    private Integer totalCoin;
    private LocalDate createTime;
    private Integer level;
    private String introduction;
    private SexType sex;
    private Integer experience;
    private StatusType status;
    private String background;
}
