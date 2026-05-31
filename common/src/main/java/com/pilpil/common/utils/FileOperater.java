package com.pilpil.common.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.*;
import com.pilpil.common.constants.redis.redisContanst;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class FileOperater {
    private final StringRedisTemplate redisTemplate;
    private final Aliyunpojo aliyunpojo;

    private OSS ossClient;
    private String bucketName;

    @PostConstruct
    public void init() {
        this.bucketName = aliyunpojo.getBucketName();
        String endpoint = aliyunpojo.getEndpoint();
        String accessKeyId = System.getenv("OSS_ACCESS_KEY_ID");
        String accessKeySecret = System.getenv("OSS_ACCESS_KEY_SECRET");

        this.ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }

    public String initUpload(String fileId, String ossKey) {
        InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(bucketName, ossKey);
        InitiateMultipartUploadResult result = ossClient.initiateMultipartUpload(request);
        String uploadId = result.getUploadId();

        String fileKey = redisContanst.File.FILE_UPLOAD_PREFIX + fileId + ":" + uploadId + ":" + ossKey;
        String expireKey = redisContanst.File.FILE_EXPIRE_PREFIX + fileId + ":" + uploadId + ":" + ossKey;

        redisTemplate.opsForValue().set(fileKey, "1");
        redisTemplate.opsForValue().set(expireKey, "1", 30, TimeUnit.MINUTES);

        log.info("初始化上传 - fileId: {}, uploadId: {}, ossKey: {}", fileId, uploadId, ossKey);

        return uploadId;
    }

    public PartETag uploadPart(String uploadId, String ossKey, int partNumber, MultipartFile file) throws Exception {
        try (InputStream inputStream = file.getInputStream()) {
            long size = file.getSize();
            UploadPartRequest request = new UploadPartRequest(bucketName, ossKey, uploadId, partNumber, inputStream, size);
            return ossClient.uploadPart(request).getPartETag();
        }
    }

    public List<PartSummary> listParts(String uploadId, String ossKey) {
        try {
            ListPartsRequest request = new ListPartsRequest(bucketName, ossKey, uploadId);
            return ossClient.listParts(request).getParts();
        } catch (Exception e) {
            log.warn("查询分片失败 - uploadId: {}, ossKey: {}, 错误: {}", uploadId, ossKey, e.getMessage());
            return java.util.Collections.emptyList();
        }
    }

    public void completeUpload(String uploadId, String ossKey, List<PartETag> partETags, String fileId) {
        CompleteMultipartUploadRequest request = new CompleteMultipartUploadRequest(bucketName, ossKey, uploadId, partETags);
        ossClient.completeMultipartUpload(request);
        String durationKey = redisContanst.Video.DURATION_PREFIX + fileId;
        long videoDurationSeconds = getVideoDurationSeconds(ossKey);
        redisTemplate.opsForValue().set(durationKey, String.valueOf(videoDurationSeconds));
        log.info("完成上传 - fileId: {}, uploadId: {}, ossKey: {}", fileId, uploadId, ossKey);
    }

    public String getFileUrl(String ossKey) {
        return "https://" + bucketName + "." + aliyunpojo.getEndpoint().replace("https://", "") + "/" + ossKey;
    }

    public void confirmFile(String fileId, String uploadId, String ossKey) {
        String fileKey = redisContanst.File.FILE_UPLOAD_PREFIX + fileId + ":" + uploadId + ":" + ossKey;
        String expireKey = redisContanst.File.FILE_EXPIRE_PREFIX + fileId + ":" + uploadId + ":" + ossKey;
        String durationKey = redisContanst.Video.DURATION_PREFIX + fileId;
        redisTemplate.delete(fileKey);
        redisTemplate.delete(expireKey);
        redisTemplate.delete(durationKey);

        log.info("确认文件保留 - fileId: {}, uploadId: {}, ossKey: {}", fileId, uploadId, ossKey);
    }

    /**
     * 取消上传
     */
    public void abortUpload(String uploadId, String ossKey, String fileId) {
        try {
            AbortMultipartUploadRequest request = new AbortMultipartUploadRequest(bucketName, ossKey, uploadId);
            ossClient.abortMultipartUpload(request);
            log.info("取消上传成功 - fileId: {}, uploadId: {}, ossKey: {}", fileId, uploadId, ossKey);
        } catch (com.aliyun.oss.OSSException e) {
            if ("NoSuchUpload".equals(e.getErrorCode())) {
                log.info("上传任务已完成/已取消，无需清理 - fileId: {}, uploadId: {}, ossKey: {}", fileId, uploadId, ossKey);
            } else {
                log.error("取消上传异常 - fileId: {}, uploadId: {}, ossKey: {}", fileId, uploadId, ossKey, e);
                throw e;
            }
        } finally {
            String fileKey = redisContanst.File.FILE_UPLOAD_PREFIX + fileId + ":" + uploadId + ":" + ossKey;
            String expireKey = redisContanst.File.FILE_EXPIRE_PREFIX + fileId + ":" + uploadId + ":" + ossKey;
            redisTemplate.delete(fileKey);
            redisTemplate.delete(expireKey);
        }
    }

    // ===================== 【重点：获取视频时长 - 直接返回 秒】 =====================
    public long getVideoDurationSeconds(String ossKey) {
        OSSObject ossObject = null;
        try {
            ossObject = ossClient.getObject(bucketName, ossKey);
            try (InputStream inputStream = ossObject.getObjectContent();
                 FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputStream)) {

                grabber.start();

                long durationSeconds = grabber.getLengthInTime() / 1000000;
                grabber.stop();
                return durationSeconds;
            }
        } catch (Exception e) {
            log.error("获取视频时长失败 ossKey:{}", ossKey, e);
            return 0L;
        } finally {
            if (ossObject != null) {
                try {
                    ossObject.close();
                } catch (Exception ignored) {}
            }
        }
    }
}