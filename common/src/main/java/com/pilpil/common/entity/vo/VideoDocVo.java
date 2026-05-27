package com.pilpil.common.entity.vo;

import com.pilpil.common.entity.po.VideoDoc;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VideoDocVo {
    private List<VideoDoc> videoDocs;
    private Integer total;

}
