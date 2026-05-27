package com.pilpil.web.controller;


import com.pilpil.common.entity.Result;
import com.pilpil.common.entity.dto.VideoRecordDto;
import com.pilpil.common.entity.po.VideoRecord;
import com.pilpil.web.entity.dto.VideoRecordDtoWeb;
import com.pilpil.web.entity.vo.ListVideoRecordVo;
import com.pilpil.web.entity.vo.VideoRecordVo;
import com.pilpil.web.service.IVideoRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 视频记录表 前端控制器
 * </p>
 *
 * @author 
 * @since 2026-05-24
 */
@RestController
@RequestMapping("/video/record")
@RequiredArgsConstructor
public class VideoRecordController {
    private final IVideoRecordService videoRecordService;
    @PostMapping
    public Result saveRecord(@RequestBody VideoRecordDto videoRecordDto){
        videoRecordService.saveRecord(videoRecordDto);
        return Result.success();
    }
    @GetMapping
    public Result<VideoRecord> getRecord(Integer sectionId,Integer videoId){
        return Result.success(videoRecordService.getRecord(sectionId, videoId));
    }
    @GetMapping("/play")
    public Result playVideo(@RequestParam("sectionId") Integer sectionId,@RequestParam("videoId") Integer videoId){
        videoRecordService.playVideo(sectionId,videoId);
        return Result.success();
    }
    @PostMapping("/list")
    public Result<ListVideoRecordVo> getVideoRecordList(@RequestBody VideoRecordDtoWeb videoRecordDto){
        return Result.success(videoRecordService.getVideoRecordList(videoRecordDto));
    }

}
