package com.pilpil.web.controller;


import com.pilpil.common.entity.Result;
import com.pilpil.web.entity.dto.FansVo;
import com.pilpil.web.service.IFansService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 关注，粉丝表 前端控制器
 * </p>
 *
 * @author 
 * @since 2026-05-28
 */
@RestController
@RequestMapping("/fans")
@RequiredArgsConstructor
public class FansController {
    private final IFansService fansService;
    @PostMapping
    public Result saveFans(Long targetId){
        fansService.saveFans(targetId);
        return Result.success();
    }
    @PostMapping("/cancel")
    public Result cancelFans(Long targetId){
        fansService.cancelFans(targetId);
        return Result.success();
    }
    @GetMapping("/is")
    public Result IsFans(Long targetId){
        boolean fans = fansService.IsFans(targetId);
        return Result.success(fans);
    }
    @GetMapping("/list/follower")
    public Result<List<FansVo>> getFansList(Long userId){
        return Result.success(fansService.getFansList(userId));
    }
    @GetMapping("/list")

    public Result<List<FansVo>> getFollowerList(Long userId){
        return Result.success(fansService.getFollowerList(userId));
    }

}
