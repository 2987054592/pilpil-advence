package com.pilpil.web.controller;

import com.pilpil.common.entity.Result;
import com.pilpil.web.entity.vo.SignRecordVo;
import com.pilpil.web.entity.vo.SignVo;
import com.pilpil.web.service.IsignService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/sign")
public class SignController {
    private final IsignService signService;
    @PostMapping
    public Result<SignVo> saveSign(){
        return Result.success(signService.saveSign());
    }
    @GetMapping
    public Result<SignRecordVo> getRecord(){
        return Result.success(signService.getRecord());
    }

}
