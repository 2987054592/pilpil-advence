package com.pilpil.web.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.pilpil.common.entity.po.ChatsDetailed;
import com.pilpil.common.enums.ChatType;
import com.pilpil.web.entity.dto.ChatDtoWeb;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 聊天详情表 服务类
 * </p>
 *
 * @author 
 * @since 2026-05-29
 */
public interface IChatsDetailedService extends IService<ChatsDetailed> {

    void saveChat(ChatDtoWeb bean, Long id);

    Map<ChatType,List<ChatsDetailed>> listRecord(Long targetId);
}
