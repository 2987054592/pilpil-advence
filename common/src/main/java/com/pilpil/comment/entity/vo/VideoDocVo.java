package com.pilpil.comment.entity.vo;

import com.pilpil.comment.entity.po.VideoDoc;
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
