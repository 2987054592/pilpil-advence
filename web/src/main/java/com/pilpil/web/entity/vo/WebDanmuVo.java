package com.pilpil.web.entity.vo;

import com.pilpil.web.entity.dto.DanmuDto;
import jdk.jfr.BooleanFlag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class WebDanmuVo {
    private String token;
    private DanmuDto danmuDto;
}
