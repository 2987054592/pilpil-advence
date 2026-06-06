package com.pilpil.web.controller;


import com.pilpil.common.entity.Result;
import com.pilpil.web.entity.dto.FavoriteDto;
import com.pilpil.web.entity.vo.FavoriteVo;
import com.pilpil.web.service.IFavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 收藏夹 前端控制器
 * </p>
 *
 * @author 
 * @since 2026-05-27
 */
@RestController
@RequestMapping("/favorite")
@RequiredArgsConstructor
public class FavoriteController {
    private final IFavoriteService favoriteService;

    /**
     * 创建收藏夹
     * @return
     */
    @PostMapping
    public Result saveFavorite(@RequestBody FavoriteDto favoriteDto){
        favoriteService.saveFavorite(favoriteDto);
        return Result.success();
    }
    @GetMapping("/list")
    public Result<List<FavoriteVo>> getFavorite(Long userid) {
        return Result.success(favoriteService.getFavorite(userid));
    }
    @PostMapping
    public Result updateFavorite(@RequestBody FavoriteDto favoriteDto){
        favoriteService.updateFavorite(favoriteDto);
        return Result.success();
    }
    @DeleteMapping
    public Result deleteFavorite(@RequestParam Integer id){
        favoriteService.deleteFavorite(id);
        return Result.success();
    }
}
