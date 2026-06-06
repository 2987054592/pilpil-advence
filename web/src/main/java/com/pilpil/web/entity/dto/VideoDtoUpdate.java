package com.pilpil.web.entity.dto;

import com.pilpil.common.entity.vo.VideoDetailDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class VideoDtoUpdate extends VideoDto {
    private Integer id;

}
