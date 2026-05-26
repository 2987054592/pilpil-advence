package com.pilpil.web.controller;


import com.pilpil.comment.entity.Result;
import com.pilpil.comment.entity.dto.queryVideo;
import com.pilpil.comment.entity.po.VideoDoc;
import com.pilpil.comment.entity.vo.VideoDocVo;
import com.pilpil.comment.entity.vo.VideoVo;
import com.pilpil.web.entity.dto.VideoDto;
import com.pilpil.web.service.IVideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
}
