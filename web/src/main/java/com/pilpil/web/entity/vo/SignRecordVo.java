package com.pilpil.web.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SignRecordVo {
    private Integer days;
    private List<Byte> signRecords;
}
