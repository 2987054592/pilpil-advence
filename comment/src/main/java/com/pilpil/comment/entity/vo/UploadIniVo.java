package com.pilpil.comment.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UploadIniVo {
    private String fileId;
    private String uploadId;
    private String ossKey;
}
