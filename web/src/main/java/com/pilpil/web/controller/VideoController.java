package com.pilpil.web.controller;


import com.pilpil.common.entity.Result;
import com.pilpil.common.entity.dto.queryVideo;
import com.pilpil.common.entity.vo.VideoDocVo;
import com.pilpil.common.entity.vo.VideoVo;
import com.pilpil.web.entity.dto.VideoDtoUpdate;
import com.pilpil.web.entity.vo.MyVideoList;
import com.pilpil.web.entity.vo.MyVideoVo;
import com.pilpil.web.entity.dto.VideoDto;
import com.pilpil.web.service.IVideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 视频表 前端控制器
 * </p>
 *
 * @author 
 * @since 2026-05-22
 */
@RestController
@RequestMapping("/video")
@RequiredArgsConstructor
public class VideoController {
    private final IVideoService videoService;

    @PostMapping("/save")
    public Result saveVideo(@RequestBody VideoDto videoDto){
        videoService.saveVideo(videoDto);
        return Result.success();
    }
    @PostMapping
    public Result<VideoDocVo> getVideo(@RequestBody queryVideo queryVideo){

        return Result.success(videoService.getVideo(queryVideo));
    }
    @GetMapping
    public Result<VideoVo> getVideo(Integer id){
        return Result.success(videoService.getByIdc(id));
    }
    @PostMapping("/me")
    public Result<MyVideoList> getMyVideo(@RequestBody queryVideo queryVideo){
        return Result.success(videoService.getMyVideo(queryVideo));
    }
    @PostMapping("/update")
    public Result updateVideo(@RequestBody VideoDtoUpdate videoDto){
        videoService.updateVideo(videoDto);
        return Result.success();
    }
    @GetMapping("/updateinfo")
    public Result<VideoDtoUpdate> updateInfo(Integer videoId){
        return Result.success(videoService.updateInfo(videoId));
    }
    @DeleteMapping
    public Result deleteVideo(Integer videoId){
        videoService.deleteVideo(videoId);
        return Result.success();
    }

}
