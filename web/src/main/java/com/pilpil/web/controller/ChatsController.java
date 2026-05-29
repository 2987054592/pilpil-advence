package com.pilpil.web.controller;


import com.pilpil.common.entity.Result;
import com.pilpil.common.entity.po.Chats;
import com.pilpil.common.entity.po.ChatsDetailed;
import com.pilpil.common.enums.ChatType;
import com.pilpil.web.entity.vo.ChatsVo;
import com.pilpil.web.service.IChatsDetailedService;
import com.pilpil.web.service.IChatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 聊天表 前端控制器
 * </p>
 *
 * @author 
 * @since 2026-05-29
 */
@RestController
@RequestMapping("/chats")
@RequiredArgsConstructor
public class ChatsController {
    private final IChatsService chatsService;
    private final IChatsDetailedService chatsDetailedService;

    /**
     * 获取聊天历史记录
     * @return
     */
    @GetMapping("/detail")
    public Result<Map<ChatType,List<ChatsDetailed>>> getChatsRecord(Long targetId){
        return Result.success(chatsDetailedService.listRecord(targetId));
    }
    /**
     * 获取聊天栏
     */
    @GetMapping
    public Result<List<ChatsVo>> getChats(){
        return Result.success(chatsService.getChats());
    }
    @GetMapping("/delete")
    public Result deletes(Long targetId){
        chatsService.deletes(targetId);
        return Result.success();
    }
}
