package com.pilpil.web.entity.dto;

import com.pilpil.common.enums.FavoriteShowType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class FavoriteDto {
    private String cover;
    private String name;
    private String desc;
    private FavoriteShowType visible;
}
