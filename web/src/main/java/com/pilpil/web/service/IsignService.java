package com.pilpil.web.service;

import com.pilpil.web.entity.vo.SignRecordVo;
import com.pilpil.web.entity.vo.SignVo;

public interface IsignService {
    SignVo saveSign();

    SignRecordVo getRecord();
}
