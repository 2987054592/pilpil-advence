package com.pilpil.comment.constants.mq;

public interface mqConstans {
    public interface Exchange{
        String VIDEO_EXCHANGE = "video.exchange";

    }
    public interface Queue{
        String VIDEO_QUEUE = "video.upload.queue";
        String VIDEO_GET_QUEUE = "video.get.queue";
    }
    public interface Key{
        String VIDEO_KEY = "video.upload.key";
        String VIDEO_GET_KEY = "video.get.key";
    }
}
