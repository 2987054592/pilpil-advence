package com.pilpil.comment.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.*;
import com.pilpil.comment.constants.redis.redisContanst;
import com.pilpil.comment.entity.dto.VideofileDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
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
    public void init(){
        this.bucketName = aliyunpojo.getBucketName();
        String endpoint = aliyunpojo.getEndpoint();
        String accessKeyId = System.getenv("OSS_ACCESS_KEY_ID");
        String accessKeySecret = System.getenv("OSS_ACCESS_KEY_SECRET");
        
        this.ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }
    
    public String initUpload(String fileId, String ossKey){
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
    
    public PartETag uploadPart(String uploadId, String ossKey, int partNumber, MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();
        long size = file.getSize();
        UploadPartRequest request = new UploadPartRequest(bucketName, ossKey, uploadId, partNumber, inputStream, size);
        return ossClient.uploadPart(request).getPartETag();
    }
    
    public List<PartSummary> listParts(String uploadId, String ossKey){
        try {
            ListPartsRequest request = new ListPartsRequest(bucketName, ossKey, uploadId);
            return ossClient.listParts(request).getParts();
        } catch (Exception e) {
            log.warn("查询分片失败 - uploadId: {}, ossKey: {}, 错误: {}", uploadId, ossKey, e.getMessage());
            return java.util.Collections.emptyList();
        }
    }
    
    public void completeUpload(String uploadId, String ossKey, List<PartETag> partETags, String fileId){
        CompleteMultipartUploadRequest request = new CompleteMultipartUploadRequest(bucketName, ossKey, uploadId, partETags);
        ossClient.completeMultipartUpload(request);
        log.info("完成上传 - fileId: {}, uploadId: {}, ossKey: {}", fileId, uploadId, ossKey);
    }
    
    public String getFileUrl(String ossKey){
        return "https://" + bucketName + "." + aliyunpojo.getEndpoint().replace("https://", "") + "/" + ossKey;
    }
    
    public void confirmFile(String fileId, String uploadId, String ossKey){
        String fileKey = redisContanst.File.FILE_UPLOAD_PREFIX + fileId + ":" + uploadId + ":" + ossKey;
        String exiprereKey = redisContanst.File.FILE_EXPIRE_PREFIX + fileId + ":" + uploadId + ":" + ossKey;
        redisTemplate.delete(fileKey);
        redisTemplate.delete(exiprereKey);
        log.info("确认文件保留 - fileId: {}, uploadId: {}, ossKey: {}", fileId, uploadId, ossKey);
    }
    
    public void abortUpload(String uploadId, String ossKey, String fileId){
        try {
            AbortMultipartUploadRequest request = new AbortMultipartUploadRequest(bucketName, ossKey, uploadId);
            ossClient.abortMultipartUpload(request);
            
            String fileKey = redisContanst.File.FILE_UPLOAD_PREFIX + fileId + ":" + uploadId + ":" + ossKey;
            String exiprereKey = redisContanst.File.FILE_EXPIRE_PREFIX + fileId + ":" + uploadId + ":" + ossKey;
            redisTemplate.delete(fileKey);
            redisTemplate.delete(exiprereKey);
            
            log.info("取消上传 - fileId: {}, uploadId: {}, ossKey: {}", fileId, uploadId, ossKey);
        } catch (Exception e) {
            log.error("取消上传失败 - fileId: {}, uploadId: {}, ossKey: {}", fileId, uploadId, ossKey, e);
        }
    }
}
