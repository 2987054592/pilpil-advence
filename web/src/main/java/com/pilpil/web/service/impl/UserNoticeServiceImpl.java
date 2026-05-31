package com.pilpil.web.service.impl;

import com.pilpil.common.entity.UserInfo;
import com.pilpil.common.entity.dto.VideoFansMq;
import com.pilpil.common.entity.po.Chats;
import com.pilpil.common.entity.po.User;
import com.pilpil.common.entity.po.UserNotice;
import com.pilpil.common.entity.po.Video;
import com.pilpil.common.enums.IsRead;
import com.pilpil.common.enums.LevelType;
import com.pilpil.common.enums.NoticeType;
import com.pilpil.common.utils.UserHolder;
import com.pilpil.web.entity.dto.FansVo;
import com.pilpil.web.entity.vo.ChatsVo;
import com.pilpil.web.mapper.UserNoticeMapper;
import com.pilpil.web.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.pilpil.common.constants.Exception.exceptionConstants.User.USER_STATUS_ERROR;
import static com.pilpil.common.constants.redis.redisContanst.Fans.FANS_FOLLOWER_PREFIX;

/**
 * <p>
 * 用户通知表 服务实现类
 * </p>
 *
 * @author 
 * @since 2026-05-30
 */
@Service
@RequiredArgsConstructor
public class UserNoticeServiceImpl extends ServiceImpl<UserNoticeMapper, UserNotice> implements IUserNoticeService {
    private final StringRedisTemplate redisTemplate;
    private final IUserService userService;
    private final IVideoService videoService;
    private final IChatsService chatsService;

    @Transactional
    @Override
    public void noticeFans(VideoFansMq fansMq) {
        Long authorId = fansMq.getAuthorId();
        String key=FANS_FOLLOWER_PREFIX+authorId;
        User author = userService.lambdaQuery().eq(User::getId, authorId).one();
        if(author==null){
            author = new User().setAvatar("").setNickName(USER_STATUS_ERROR)
                    .setLevel(LevelType.LV0);
        }


        Set<String> members = redisTemplate.opsForSet().members(key);
        if(members==null||members.isEmpty()){
            return;
        }
        List<Long> fansIds = members.stream().map(Long::valueOf).toList();
        List<UserNotice> vo = new ArrayList<>();
        for(Long fansId:fansIds){
            UserNotice notice = UserNotice.builder()
                    .userId(fansId)
                    .read(IsRead.UNREAD)
                    .content("你关注的博主" + author.getNickName() + "发布了新视频")
                    .createTime(LocalDate.now())
                    .fromUid(authorId)
                    .type(NoticeType.FOLLOW)
                    .videoId(fansMq.getVideoId())
                    .build();
            vo.add( notice);
        }
        try {
            saveBatch(vo);
        }catch (Exception e){
            log.error("保存通知失败:{}",e);
        }

    }

    @Override
    public List<UserNotice> listByFromId() {
        Long userId = UserHolder.get().getId();
        List<UserNotice> list = lambdaQuery()
                .eq(UserNotice::getUserId, userId).list();
        return list;
    }

    @Override
    public void noticeVideoComment(VideoFansMq fansMq) {

        Long authorId = fansMq.getAuthorId();
        Integer videoId = fansMq.getVideoId();
        Video video = videoService.lambdaQuery().eq(Video::getId, videoId).one();
        Long userId = fansMq.getUserId();
        UserNotice notice = UserNotice.builder()
                .videoId(videoId)
                .userId(authorId)
                .fromUid(userId)
                .read(IsRead.UNREAD)
                .content("你的视频"+"<<"+video.getName()+">>"+"收到一条新评论")
                .type(NoticeType.COMMENT)
                .createTime(LocalDate.now())
                .build();
        save(notice);
    }

    @Override
    public void receiveComment(VideoFansMq fansMq) {
        Integer videoId = fansMq.getVideoId();
        Long userId = fansMq.getUserId();
        Long authorId = fansMq.getAuthorId();
        UserNotice notice = UserNotice.builder()
                .videoId(videoId)
                .userId(authorId)
                .fromUid(userId)
                .read(IsRead.UNREAD)
                .content("你的评论收到一条新评论")
                .type(NoticeType.COMMENT)
                .createTime(LocalDate.now())
                .build();
        save(notice);
    }

    @Override
    public Long counts() {
        UserInfo userInfo = UserHolder.get();
        if(userInfo==null){
            return 0L;
        }
        Long count = lambdaQuery()
                .eq(UserNotice::getUserId,userInfo.getId() )
                .eq(UserNotice::getRead, IsRead.UNREAD)
                .count();
        List<ChatsVo> chats = chatsService.getChats();
        List<Integer> list = chats.stream().map(ChatsVo::getUnread).toList();
        int sum = list.stream().mapToInt(Integer::intValue).sum();
        return count+sum;
    }
}
