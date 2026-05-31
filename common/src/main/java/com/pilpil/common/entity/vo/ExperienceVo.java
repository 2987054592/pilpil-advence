package com.pilpil.common.entity.vo;

import com.pilpil.common.enums.PointType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ExperienceVo {
    private Long userId;
    private Integer experience;
    private PointType pointType;
}
