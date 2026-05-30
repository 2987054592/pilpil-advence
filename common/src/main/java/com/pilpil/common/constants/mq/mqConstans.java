package com.pilpil.common.constants.mq;

public interface mqConstans {
    public interface Exchange{
        String VIDEO_EXCHANGE = "video.exchange";
        String VIDEO_FANS_EXCHANGE = "video.fans.exchange";
        String VIDEO_COMMENT_EXCHANGE = "video.comment.exchange";
        String COMMENT_REPLAY_EXCHANGE = "comment.replay.exchange";

    }
    public interface Queue{
        String VIDEO_QUEUE = "video.upload.queue";
        String VIDEO_GET_QUEUE = "video.get.queue";
        String VIDEO_FANS_QUEUE = "video.fans.queue";
        String VIDEO_COMMENT_QUEUE = "video.comment.queue";
        String COMMENT_REPLAY_QUEUE = "comment.replay.queue";
    }
    public interface Key{
        String VIDEO_KEY = "video.upload.key";
        String VIDEO_GET_KEY = "video.get.key";
        String VIDEO_FANS_KEY = "video.fans.key";
        String VIDEO_COMMENT_KEY = "video.comment.key";
        String COMMENT_REPLAY_KEY = "comment.replay.key";
    }
}
