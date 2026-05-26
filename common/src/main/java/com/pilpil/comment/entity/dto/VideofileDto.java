package com.pilpil.comment.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideofileDto {
    private MultipartFile file;
    private String fileName;
    private Integer chunkIndex;
    private Integer chuckTotal;
    private String md5;
    private Integer videoId;
}
