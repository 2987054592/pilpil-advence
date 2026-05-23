package com.pilpil.comment.constants.Exception;

public interface exceptionConstants {
    String EXIST="当前数据已经存在";
    interface User{
        String USER_NOT_EXIST = "用户不存在";
        String USER_EXIST = "用户已存在";
        String USER_LOGIN_ERROR = "用户账号或密码错误";
        String USER_CODE_ERROR = "验证码错误";
        String EMAIL_EXIST = "邮箱已存在";
        String EMAIL_NULL = "邮箱不能为空";
    }
    interface Category{
        String CATEGORY_EXIST = "分类已存在";
        String CATEGORY_NOT_EXIST = "分类不存在";
        String CATEGORY_PARENT_EMPTY = "子分类不能再建分类";
    }
    interface Video{
        String VIDEO_NOT_EXIST = "视频不存在";
        String VIDEO_STATUS_ERROR = "视频未审核或封禁";
    }
}
