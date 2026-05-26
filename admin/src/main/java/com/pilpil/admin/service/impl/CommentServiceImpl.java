package com.pilpil.admin.service.impl;


import com.pilpil.admin.mapper.CommentMapper;
import com.pilpil.admin.service.ICommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pilpil.comment.entity.po.Comment;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 评论表 服务实现类
 * </p>
 *
 * @author 
 * @since 2026-05-26
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements ICommentService {

}
