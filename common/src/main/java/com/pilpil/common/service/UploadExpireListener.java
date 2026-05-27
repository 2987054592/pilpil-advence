package com.pilpil.common.service;

import com.pilpil.common.constants.redis.redisContanst;
import com.pilpil.common.utils.Aliyunossdelte;
import com.pilpil.common.utils.Aliyunpojo;
import com.pilpil.common.utils.FileOperater;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j

public class UploadExpireListener extends KeyExpirationEventMessageListener {
    private final FileOperater fileOperater;
    private final StringRedisTemplate redisTemplate;
    Aliyunossdelte aliyunossdelte;
    private final Aliyunpojo aliyunpojo;

    public UploadExpireListener(RedisMessageListenerContainer listenerContainer,
                                FileOperater fileOperater,
                                StringRedisTemplate redisTemplate,
                                Aliyunossdelte aliyunossdelte,
                                Aliyunpojo aliyunpojo
                                ) {
        super(listenerContainer);
        this.fileOperater = fileOperater;
        this.redisTemplate = redisTemplate;
        this.aliyunossdelte = aliyunossdelte;
        this.aliyunpojo = aliyunpojo;
    }

    @Override
    public void onMessage(Message message, @Nullable byte[] pattern) {
        String redisKey = message.toString();
        
        if (!redisKey.startsWith(redisContanst.File.FILE_EXPIRE_PREFIX)) {
            return;
        }
        
        try {
            String[] parts = redisKey.split(":");
            if (parts.length < 5) {
                log.warn("Invalid redis key format: {}", redisKey);
                return;
            }
            
            String fileId = parts[2];
            String uploadId = parts[3];
            String ossKey = parts[4];
            
            String fileKey = redisContanst.File.FILE_UPLOAD_PREFIX + fileId + ":" + uploadId + ":" + ossKey;
            Boolean exists = redisTemplate.hasKey(fileKey);

            if(exists) {
                String fileUrl = fileOperater.getFileUrl(ossKey);

                List<String> urls = new ArrayList<>();
                urls.add(fileUrl);
                
                try {
                    aliyunossdelte.deleteimg(urls);
                    log.info("删除OSS文件成功 - URL: {}", fileUrl);
                } catch (Exception e) {
                    log.warn("删除OSS文件失败（可能文件不存在）- URL: {}, 错误: {}", fileUrl, e.getMessage());
                }
                
                fileOperater.abortUpload(uploadId, ossKey, fileId);
                
                log.info("清理过期上传文件完成 - fileId: {}, uploadId: {}, ossKey: {}", fileId, uploadId, ossKey);
                redisTemplate.delete(redisKey);
            } else {
                log.info("上传已确认或已完成，跳过清理 - fileId: {}, uploadId: {}, ossKey: {}", fileId, uploadId, ossKey);
            }

        } catch (Exception e) {
            log.error("处理过期上传文件失败: {}", redisKey, e);
        }
    }
}
