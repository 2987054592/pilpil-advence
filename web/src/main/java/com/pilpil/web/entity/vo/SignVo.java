package com.pilpil.web.entity.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SignVo {
    @Builder.Default
    private Integer point=1;
    private Integer rewardPoint;
    private Integer signDays;

    @JsonIgnore
    public Integer totoalPoint(){
        return point + rewardPoint;
    }
}
