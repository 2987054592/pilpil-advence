package com.pilpil.web.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.pilpil.common.entity.po.Comment;
import com.pilpil.web.entity.dto.CommentDto;
import com.pilpil.web.entity.dto.QueryCommentDto;
import com.pilpil.web.entity.vo.QueryCommentVo;

/**
 * <p>
 * 评论表 服务类
 * </p>
 *
 * @author 
 * @since 2026-05-26
 */
public interface ICommentService extends IService<Comment> {

    void saveComment(CommentDto commentDto);


    QueryCommentVo getComment(QueryCommentDto queryCommentDto);
}
