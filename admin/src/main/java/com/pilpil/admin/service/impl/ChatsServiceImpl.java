package com.pilpil.admin.service.impl;


import com.pilpil.admin.mapper.ChatsMapper;
import com.pilpil.admin.service.IChatsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pilpil.common.entity.po.Chats;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 聊天表 服务实现类
 * </p>
 *
 * @author 
 * @since 2026-05-29
 */
@Service
public class ChatsServiceImpl extends ServiceImpl<ChatsMapper, Chats> implements IChatsService {

}
