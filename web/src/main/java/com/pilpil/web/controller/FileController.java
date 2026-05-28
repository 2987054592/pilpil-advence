package com.pilpil.web.controller;

import cn.hutool.json.JSONUtil;
import com.aliyun.oss.model.PartETag;
import com.aliyun.oss.model.PartSummary;
import com.pilpil.common.entity.Result;
import com.pilpil.common.entity.po.VideoDetail;
import com.pilpil.common.entity.vo.UploadIniVo;
import com.pilpil.common.exception.illegalException;
import com.pilpil.common.utils.FileOperater;
import com.pilpil.web.mapper.VideoDetailMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

import static com.pilpil.common.constants.Exception.exceptionConstants.File.FILE_IS_EXIST;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {
    private final FileOperater fileOperater;
    private final VideoDetailMapper videoDetailMapper;
    @PostMapping("/init")
    public Result initUpload(@RequestParam String fileName,
                             @RequestParam String md5){

        LocalDate now = LocalDate.now();
        String datePath = String.format("%d/%02d/%02d", now.getYear(), now.getMonthValue(), now.getDayOfMonth());
        String ossKey = "videos/" + datePath + "/" + md5 + "_" + fileName;
        String uploadId = fileOperater.initUpload(md5, ossKey);
        UploadIniVo vo = UploadIniVo.builder()
                .fileId(md5)
                .ossKey(ossKey)
                .uploadId(uploadId).build();
        return Result.success(vo);
    }
    @PostMapping("/part")
    public Result uploadPart(
            @RequestParam String uploadId,
            @RequestParam String ossKey,
            @RequestParam int partNumber,
            @RequestParam MultipartFile file
    ){
        try {
            PartETag partETag = fileOperater.uploadPart(uploadId, ossKey, partNumber, file);
            return Result.success(partETag);
        } catch (Exception e) {
            return Result.error("上传失败");
        }
    }
    @GetMapping("/parts")
    public Result listParts(@RequestParam String uploadId,
                            @RequestParam String ossKey){
        List<PartSummary> partSummaries = fileOperater.listParts(uploadId, ossKey);
        return Result.success(partSummaries);
    }
    @PostMapping("/complete")
    public Result<UploadIniVo> completeUpload(
            @RequestParam String uploadId,
            @RequestParam String ossKey,
            @RequestParam String partETags,
            @RequestParam String fileId
    ){
        try {
            List<PartETag> list = JSONUtil.toList(JSONUtil.parseArray(partETags), PartETag.class);
            
            for (PartETag partETag : list) {
                if (partETag.getETag() == null || partETag.getETag().isEmpty()) {
                    return Result.error("PartETag 数据不完整，请检查上传的分片信息");
                }
            }

            fileOperater.completeUpload(uploadId, ossKey, list, fileId);
            UploadIniVo vo = UploadIniVo.builder()
                    .uploadId(uploadId)
                    .fileId(fileId)
                    .ossKey(ossKey)
                    .url(fileOperater.getFileUrl(ossKey))
                    .duration(fileOperater.getVideoDurationSeconds(ossKey))
                    .build();
            return Result.success(vo);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("完成上传失败: " + e.getMessage());
        }
    }

}
