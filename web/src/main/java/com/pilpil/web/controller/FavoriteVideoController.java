package com.pilpil.web.controller;


import com.pilpil.common.entity.Result;
import com.pilpil.common.entity.po.FavoriteVideo;
import com.pilpil.web.entity.dto.FavoriteVideoDto;
import com.pilpil.web.entity.vo.FavoriteVideoVo;
import com.pilpil.web.service.IFavoriteVideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 
 * @since 2026-05-27
 */
@RestController
@RequestMapping("/favorite-video")
@RequiredArgsConstructor
public class FavoriteVideoController {
    private final IFavoriteVideoService favoriteVideoService;
    @PostMapping
    public Result addFavoriteVideo(@RequestBody FavoriteVideoDto favoriteVideoDto){
        favoriteVideoService.addFavoriteVideo(favoriteVideoDto);
        return Result.success();
    }
    @GetMapping
    public Result<List<FavoriteVideoVo>> getFavoriteVideoList(Integer favoriteId){
        return Result.success(favoriteVideoService.getFavoriteVideoList(favoriteId));
    }
}
