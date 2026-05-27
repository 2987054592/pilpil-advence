package com.pilpil.web.entity.dto;

import com.pilpil.common.enums.CoinType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CoinDto {
    private Integer videoId;
    private CoinType number;
}
