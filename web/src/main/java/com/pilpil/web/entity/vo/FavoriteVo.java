package com.pilpil.web.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Data
public class FavoriteVo {
    private String name;
    private Integer id;
    private Integer FavoriteCount;
}
