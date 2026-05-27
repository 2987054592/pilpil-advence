package com.pilpil.admin.controller;


import com.pilpil.common.entity.dto.VideoReview;
import com.pilpil.admin.service.IVideoService;
import com.pilpil.common.entity.Result;
import com.pilpil.common.entity.dto.queryVideo;
import com.pilpil.common.entity.vo.VideoDocVo;
import com.pilpil.common.entity.vo.VideoVo;
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
@RequestMapping("/admin/video")
@RequiredArgsConstructor
public class VideoController {
    private final IVideoService videoService;
    @PostMapping
    public Result<VideoDocVo> getVideo(@RequestBody queryVideo queryVideo){
        return Result.success(videoService.getVideo(queryVideo));
    }
    @GetMapping
    public Result<VideoVo> getVideo(Integer id){
        return Result.success(videoService.getByIdc(id));
    }
    @PostMapping("/review")
    public Result reviewVideo(@RequestBody VideoReview videoReview){
        videoService.reviewVideo(videoReview);
        return Result.success();
    }
    @PostMapping("/review/video")
    public Result reviewVideos(@RequestBody VideoReview videoReview){
        videoService.reviewVideos(videoReview);
        return Result.success();
    }
}
