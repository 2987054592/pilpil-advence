package com.pilpil.web.service.impl;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pilpil.common.entity.po.Comment;
import com.pilpil.common.entity.po.User;
import com.pilpil.common.enums.CommentTopType;
import com.pilpil.common.enums.LevelType;
import com.pilpil.common.utils.UserHolder;
import com.pilpil.web.entity.dto.CommentDto;
import com.pilpil.web.entity.dto.QueryCommentDto;
import com.pilpil.web.entity.vo.CommentVo;
import com.pilpil.web.entity.vo.QueryCommentVo;
import com.pilpil.web.mapper.CommentMapper;
import com.pilpil.web.service.ICommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pilpil.web.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.pilpil.common.constants.Exception.exceptionConstants.Comment.COMMENT_NOT_EXIST;
import static com.pilpil.common.constants.Exception.exceptionConstants.User.USER_DELETE_ERROR;
import static com.pilpil.common.constants.redis.redisContanst.Comment.COMMENT_LIST_PREFIX;
import static com.pilpil.common.constants.redis.redisContanst.Comment.COMMENT_TOTAL;

/**
 * <p>
 * 评论表 服务实现类
 * </p>
 *
 * @author 
 * @since 2026-05-26
 */
@Service
@RequiredArgsConstructor
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements ICommentService {
    private final IUserService userService;
    private final StringRedisTemplate redisTemplate;
    @Override
    public void saveComment(CommentDto commentDto) {
        Integer videoId = commentDto.getVideoId();
        Integer rootId = commentDto.getRootId();
        if(videoId!=null &&rootId==0){
            //给视频发评论，一级评论
            ToVideo(commentDto);
        }else{
            //给评论发评论，二级评论
            ToComment(commentDto);
        }
    }

    private void ToComment(CommentDto commentDto) {
        String key=COMMENT_LIST_PREFIX+commentDto.getVideoId();
        redisTemplate.opsForHash().increment(key,COMMENT_TOTAL,1);

        Integer targetCommentId = commentDto.getTargetCommentId();
        Comment one = lambdaQuery().eq(Comment::getId, targetCommentId).one();
        if(one==null){
            throw new RuntimeException(COMMENT_NOT_EXIST);
        }
        String keys=COMMENT_LIST_PREFIX+commentDto.getVideoId();
//        lambdaUpdate().eq(Comment::getId, targetCommentId)
//                .setSql("reply_count = reply_count + 1")
//                .update();
        redisTemplate.opsForHash().increment(keys,commentDto.getTargetCommentId().toString(),1);
        if(!Objects.equals(targetCommentId, commentDto.getRootId())) {
//            lambdaUpdate().eq(Comment::getId, commentDto.getRootId())
//                    .setSql("reply_count = reply_count + 1")
//                    .update();
            redisTemplate.opsForHash().increment(keys,commentDto.getRootId().toString(),1);
        }
        Long authorId = UserHolder.get().getId();
        Comment comment = Comment.builder()
                .videoId(commentDto.getVideoId())
                .authorId(authorId)
                .rootId(commentDto.getRootId())
                .targetId(commentDto.getTargetId())
                .content(commentDto.getContent())
                .targetCommentId(commentDto.getTargetCommentId())
                .likeCount(0)
                .createTime(LocalDate.now())
                .top(CommentTopType.NOT_TOP)
                .replyCount(0)
                .build();
        this.save(comment);
    }

    private void ToVideo(CommentDto commentDto) {
        String key=COMMENT_LIST_PREFIX+commentDto.getVideoId();
        redisTemplate.opsForHash().increment(key,COMMENT_TOTAL,1);
        Long authorId = UserHolder.get().getId();
        Comment comment = Comment.builder()
                .videoId(commentDto.getVideoId())
                .authorId(authorId)
                .rootId(commentDto.getRootId())
                .targetId(commentDto.getTargetId())
                .content(commentDto.getContent())
                .likeCount(0)
                .top(CommentTopType.NOT_TOP)
                .replyCount(0)
                .targetCommentId(0)
                .createTime(LocalDate.now())
                .build();
        this.save(comment);
    }

    @Override
    public QueryCommentVo getComment(QueryCommentDto queryCommentDto) {
        Integer videoId = queryCommentDto.getVideoId();
        Integer commentId = queryCommentDto.getCommentId();
        Integer pageNum = queryCommentDto.getPageNum();
        Integer pageSize = queryCommentDto.getPageSize();
        if(videoId!=null && commentId==null){
            //查询视频的评论
            return GetVideoComment(videoId,pageNum,pageSize);
        }else{
            //查询评论的评论
            return GetComentComent(videoId,commentId,pageNum,pageSize);
        }
    }

    private QueryCommentVo GetComentComent(Integer videoId,Integer commentId, Integer pageNum, Integer pageSize) {
        Page<Comment> page = lambdaQuery()
                .eq(Comment::getVideoId, videoId)
                .eq(Comment::getRootId, commentId)
                .page(new Page<>(pageNum, pageSize));
        QueryCommentVo vo = new QueryCommentVo();
        vo.setTotalData((int) page.getTotal());
        vo.setTotalPage((int) page.getPages());
        List<Comment> records = page.getRecords();
        if (records == null || records.isEmpty()) {
            vo.setCommentList(Collections.emptyList());
            return vo;
        }
        User user = new User();
        user.setAvatar("");
        user.setNickName(USER_DELETE_ERROR);
        user.setLevel(LevelType.LV0);
        Set<Long> userIds = records.stream().map(Comment::getAuthorId).collect(Collectors.toSet());
        List<User> users = userService.getBaseMapper().selectByIds(userIds);
        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(User::getId, u -> u));
        List<CommentVo> vos = records.stream().map(comment -> {
            Long authorId = comment.getAuthorId();
            User author = userMap.get(authorId);
            return CommentVo.builder()
                    .id(comment.getId())
                    .level(author==null ? user.getLevel() : author.getLevel())
                    .authorId(authorId)
                    .authorAvatar(author==null ? user.getAvatar() : author.getAvatar())
                    .authorName(author==null ? user.getNickName() : author.getNickName())
                    .targetCommentId(comment.getTargetCommentId())
                    .rootId(comment.getRootId())
                    .content(comment.getContent())
                    .likeCount(comment.getLikeCount())
                    .replyCount(comment.getReplyCount())
                    .top(comment.getTop())
                    .createTime(comment.getCreateTime())
                    .build();
        }).toList();
        vo.setCommentList(vos);
        return vo;
    }

    private QueryCommentVo GetVideoComment(Integer videoId, Integer pageNum, Integer pageSize) {
        Page<Comment> page = lambdaQuery()
                .eq(Comment::getVideoId, videoId)
                .eq(Comment::getRootId, 0)
                .page(new Page<>(pageNum, pageSize));
        QueryCommentVo vo = new QueryCommentVo();
        vo.setTotalData((int) page.getTotal());
        vo.setTotalPage((int) page.getPages());
        List<Comment> records = page.getRecords();
        if (records == null || records.isEmpty()) {
            vo.setCommentList(Collections.emptyList());
            return vo;
        }
        User user = new User();
            user.setAvatar("");
            user.setNickName(USER_DELETE_ERROR);
            user.setLevel(LevelType.LV0);
        Set<Long> userIds = records.stream().map(Comment::getAuthorId).collect(Collectors.toSet());
        List<User> users = userService.getBaseMapper().selectByIds(userIds);
        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(User::getId, u -> u));
        List<CommentVo> list = records.stream().map(comment -> {
            Long authorId = comment.getAuthorId();
            User author = userMap.get(authorId);
            CommentVo commentVo = new CommentVo();
            commentVo.setId(comment.getId());
            commentVo.setAuthorAvatar(author==null ? user.getAvatar() : author.getAvatar());
            commentVo.setAuthorId(authorId);
            commentVo.setAuthorName(author==null ? user.getNickName() : author.getNickName());
            commentVo.setLevel(author==null ? user.getLevel() : author.getLevel());
            commentVo.setContent(comment.getContent());
            commentVo.setTop(comment.getTop());
            commentVo.setLikeCount(comment.getLikeCount());
            commentVo.setReplyCount(comment.getReplyCount());
            commentVo.setRootId(comment.getRootId());
            commentVo.setCreateTime(comment.getCreateTime());
            commentVo.setTargetCommentId(comment.getTargetCommentId());
            return commentVo;
        }).toList();
        vo.setCommentList( list);
        return vo;
    }
}
