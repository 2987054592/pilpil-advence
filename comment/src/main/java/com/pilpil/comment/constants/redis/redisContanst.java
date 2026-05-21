package com.pilpil.comment.constants.redis;

public interface redisContanst {
     interface User{
        String REGISTER_CODE_PREFIX = "REGISTER:CODE:";
        String LOGIN_TOKEN_PREFIX = "LOGIN:TOKEN:";
    }
    interface Category{
         String CATEGORY_LIST = "CATEGORY:LIST";
    }
}
