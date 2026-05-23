package com.pilpil.comment.constants.redis;

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
    }
}
