package com.pilpil.comment.service;

import com.pilpil.comment.constants.redis.redisContanst;
import com.pilpil.comment.utils.FileOperater;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UploadExpireListener extends KeyExpirationEventMessageListener {
    private final FileOperater fileOperater;
    private final StringRedisTemplate redisTemplate;

    public UploadExpireListener(RedisMessageListenerContainer listenerContainer,
                                FileOperater fileOperater,
                                StringRedisTemplate redisTemplate) {
        super(listenerContainer);
        this.fileOperater = fileOperater;
        this.redisTemplate = redisTemplate;
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

                log.info("清理过期上传文件 - fileId: {}, uploadId: {}, ossKey: {}", fileId, uploadId, ossKey);
                redisTemplate.delete(redisKey);
                fileOperater.abortUpload(uploadId, ossKey, fileId);
            }
            
        } catch (Exception e) {
            log.error("处理过期上传文件失败: {}", redisKey, e);
        }
    }
}
