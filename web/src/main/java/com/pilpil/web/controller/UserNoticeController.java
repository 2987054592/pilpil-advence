package com.pilpil.web.controller;


import com.pilpil.common.entity.Result;
import com.pilpil.common.entity.po.UserNotice;
import com.pilpil.common.enums.IsRead;
import com.pilpil.common.utils.UserHolder;
import com.pilpil.web.service.IUserNoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 用户通知表 前端控制器
 * </p>
 *
 * @author 
 * @since 2026-05-30
 */
@RestController
@RequestMapping("/userNotice")
@RequiredArgsConstructor
public class UserNoticeController {
    private final IUserNoticeService userNoticeService;
    @GetMapping
    public Result<List<UserNotice>> getUserNoticeList(){
        return Result.success(userNoticeService.listByFromId());

    }
    @GetMapping("/read")
    public Result raed(Integer noticeId){
        userNoticeService.lambdaUpdate()
                .eq(UserNotice::getId,noticeId)
                .set(UserNotice::getRead, IsRead.READ)
                .update();
        return Result.success();
    }
    @GetMapping("/list")
    public Result<Long> listNotice(){
        return Result.success(userNoticeService.lambdaQuery()
                .eq(UserNotice::getUserId, UserHolder.get().getId())
                .eq(UserNotice::getRead, IsRead.UNREAD)
                .count());
    }
    @DeleteMapping
    public Result deleteNotice(){
        userNoticeService.lambdaUpdate()
                .eq(UserNotice::getUserId, UserHolder.get().getId())
                .remove();
        return Result.success();
    }

}
