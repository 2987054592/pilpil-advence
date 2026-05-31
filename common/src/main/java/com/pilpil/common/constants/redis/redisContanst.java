package com.pilpil.common.constants.redis;

public interface redisContanst {
     interface User{
        String REGISTER_CODE_PREFIX = "REGISTER:CODE:";
        String LOGIN_TOKEN_PREFIX = "LOGIN:TOKEN:";
    }
    interface Category{
         String CATEGORY_LIST = "CATEGORY:LIST";
    }
    interface File{
         String FILE_UPLOAD_PREFIX = "FILE:UPLOAD:";
         String FILE_EXPIRE_PREFIX = "FILE:EXPIRE:";
    }
    interface Video{
         String DURATION_PREFIX = "VIDEO:DURATION:";
         String VIDEO_RECORD_PREFIX = "VIDEO:RECORD:";
         String VIDEO_PLAY_PREFIX = "VIDEO:PLAY:";
     }
     interface Danmu{
         String DANMU_RECORD_PREFIX = "DANMU:RECORD:";
         String DAMU_TEMPT_PREFIX = "DANMU:TEMPT:";
         String DANMU_LIST_PREFIX = "DANMU:LIST:";

     }
     interface Comment{
         String COMMENT_LIST_PREFIX = "COMMENT:LIST:";
         String COMMENT_TOTAL="COMMENT_TOTAL";
     }
     interface Like{
         String LIKE_COMMENT_PREFIX = "LIKE:COMMENT:";
         String LIKE_VIDEO_PREFIX = "LIKE:VIDEO:";
         String LIKE_DANMU_PREFIX = "LIKE:DANMU:";
     }
     interface Coin{
         String COIN_INCRE_PREFIX = "COIN:INCRE:";
     }
     interface Fans{
         String FANS_FOLLOW_PREFIX = "FANS:FOLLOW:";
         String FANS_FOLLOWER_PREFIX = "FANS:FOLLOWER:";
     }
     interface Chat{
         String CHAT_UNREAD_PREFIX = "CHAT:UNREAD:";
     }
     interface Sign{
         String SIGN_PREFIX = "SIGN:UID:";
     }


}
