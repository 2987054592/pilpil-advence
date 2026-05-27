package com.pilpil.common.utils;


import com.pilpil.common.entity.UserInfo;

public class UserHolder {
    public static final ThreadLocal<UserInfo> userThreadLocal = new ThreadLocal<>();
    public static void save(UserInfo userInfo){
        userThreadLocal.set(userInfo);
    }
    public static UserInfo get(){
        return userThreadLocal.get();
    }
    public static void remove(){
        userThreadLocal.remove();
    }
}
