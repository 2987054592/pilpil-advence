package com.pilpil.web.controller;


import com.pilpil.common.entity.Result;
import com.pilpil.web.entity.dto.CoinDto;
import com.pilpil.web.service.ICoinService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 投币表 前端控制器
 * </p>
 *
 * @author 
 * @since 2026-05-27
 */
@RestController
@RequestMapping("/coin")
@RequiredArgsConstructor
public class CoinController {
    private final ICoinService coinService;
    @PostMapping
    public Result saveCoin(@RequestBody CoinDto coinDto){
        coinService.saveCoin(coinDto);
        return Result.success();
    }
}
