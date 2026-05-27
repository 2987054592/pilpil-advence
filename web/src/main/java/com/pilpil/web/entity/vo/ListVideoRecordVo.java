package com.pilpil.web.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class ListVideoRecordVo {
    private Integer total;
    private Integer pageSize;
    private List<VideoRecordVo> list;
}
