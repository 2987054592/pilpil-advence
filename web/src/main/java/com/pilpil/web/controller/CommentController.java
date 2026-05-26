package com.pilpil.web.controller;


import com.pilpil.comment.entity.Result;
import com.pilpil.web.entity.dto.CommentDto;
import com.pilpil.web.entity.dto.QueryCommentDto;
import com.pilpil.web.entity.vo.QueryCommentVo;
import com.pilpil.web.service.ICommentService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 评论表 前端控制器
 * </p>
 *
 * @author 
 * @since 2026-05-26
 */
@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {
    private final ICommentService commentService;
    @PostMapping("/save")
    public Result saveComment(@RequestBody CommentDto commentDto){
        commentService.saveComment(commentDto);
        return Result.success();
    }
    @PostMapping
    public Result<QueryCommentVo> getComment(@RequestBody QueryCommentDto queryCommentDto){
        return Result.success(commentService.getComment(queryCommentDto));
    }
}
