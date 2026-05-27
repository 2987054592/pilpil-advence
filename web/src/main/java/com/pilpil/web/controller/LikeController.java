package com.pilpil.web.controller;


import com.pilpil.common.entity.Result;
import com.pilpil.common.enums.LikeBisType;
import com.pilpil.web.entity.dto.likeDto;
import com.pilpil.web.service.ILikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 点赞表 前端控制器
 * </p>
 *
 * @author 
 * @since 2026-05-27
 */
@RestController
@RequestMapping("/like")
@RequiredArgsConstructor
public class LikeController {
    private final ILikeService likeService;
    @PostMapping
    public Result savelike(@RequestBody likeDto likeDto){
        likeService.savelike(likeDto);
        return Result.success();
    }
    @GetMapping
    public Result< Map<LikeBisType, List<Integer>>> getlike(Integer videoId) {
        Map<LikeBisType, List<Integer>> result = likeService.getlike(videoId);
        return Result.success(result);
    }
}
