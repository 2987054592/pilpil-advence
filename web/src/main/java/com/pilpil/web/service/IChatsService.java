package com.pilpil.web.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.pilpil.common.entity.po.Chats;
import com.pilpil.web.entity.dto.ChatDtoWeb;
import com.pilpil.web.entity.vo.ChatsVo;

import java.util.List;

/**
 * <p>
 * 聊天表 服务类
 * </p>
 *
 * @author 
 * @since 2026-05-29
 */
public interface IChatsService extends IService<Chats> {

    void saveChat(ChatDtoWeb bean,Long userId,boolean b);

    List<ChatsVo> getChats();

    void deletes(Long targetId);
}
