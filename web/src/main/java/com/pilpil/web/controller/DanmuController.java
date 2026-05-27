package com.pilpil.web.controller;


import com.pilpil.common.entity.Result;
import com.pilpil.web.entity.dto.DanmuDto;
import com.pilpil.web.entity.dto.queryDanmuDto;
import com.pilpil.web.entity.vo.DanmuVo;
import com.pilpil.web.service.IDanmuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 弹幕表 前端控制器
 * </p>
 *
 * @author 
 * @since 2026-05-25
 */
@RestController
@RequestMapping("/danmu")
@RequiredArgsConstructor
public class DanmuController {
    private final IDanmuService danmuService;
    @PostMapping
    public Result saveDanmu(@RequestBody DanmuDto danmuDto){
        danmuService.saveDanmu(danmuDto);
        return Result.success();
    }
    @PostMapping("/list")
    public Result<List<DanmuVo>> listDanmu(@RequestBody queryDanmuDto queryDanmuDto){
        return Result.success(danmuService.listDanmu(queryDanmuDto));
    }
    @DeleteMapping
    public Result deleteDanmu(@RequestParam("id") Integer id){
        danmuService.deleteDanmu(id);
        return Result.success();
    }
}
