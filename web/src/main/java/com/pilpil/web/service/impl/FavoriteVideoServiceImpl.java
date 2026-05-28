package com.pilpil.web.service.impl;


import cn.hutool.core.bean.BeanUtil;
import com.pilpil.common.entity.po.*;
import com.pilpil.common.enums.LevelType;
import com.pilpil.common.exception.illegalException;
import com.pilpil.common.utils.UserHolder;
import com.pilpil.web.entity.dto.FavoriteVideoDto;

import com.pilpil.web.entity.vo.FavoriteVideoVo;
import com.pilpil.web.mapper.FavoriteVideoMapper;
import com.pilpil.web.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.pilpil.common.constants.Exception.exceptionConstants.Favorite.FAVORITE_NOT_EXIST;
import static com.pilpil.common.constants.Exception.exceptionConstants.Favorite.FAVORITE_VIDEO_EXIST;
import static com.pilpil.common.constants.Exception.exceptionConstants.User.USER_DELETE_ERROR;
import static com.pilpil.common.constants.Exception.exceptionConstants.Video.VIDEO_DELETE_ERROR;
import static com.pilpil.common.constants.Exception.exceptionConstants.Video.VIDEO_NOT_EXIST;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 
 * @since 2026-05-27
 */
@Service
@RequiredArgsConstructor
public class FavoriteVideoServiceImpl extends ServiceImpl<FavoriteVideoMapper, FavoriteVideo> implements IFavoriteVideoService {
    private final IFavoriteService favoriteService;
    private final IVideoService videoService;
    private final IVideoDataService videoDataService;
    private final IUserService userService;
    private final User user=new User(USER_DELETE_ERROR,"",LevelType.LV0,0L);
    private final Video video=new Video("",VIDEO_DELETE_ERROR,0L,0L);
    private final VideoData videoData=new VideoData(0,0);

    @Override
    @Transactional
    public void addFavoriteVideo(FavoriteVideoDto favoriteVideoDto) {
        Integer videoId = favoriteVideoDto.getVideoId();
        Integer favoriteId = favoriteVideoDto.getFavoriteId();
        Video video = videoService.lambdaQuery()
                .eq(Video::getId, videoId).one();
        if(video == null){
            throw new illegalException(VIDEO_NOT_EXIST);
        }
        FavoriteVideo favoriteVideo1 = lambdaQuery()
                .eq(FavoriteVideo::getFavoriteId, favoriteId)
                .eq(FavoriteVideo::getVideoId, videoId)
                .one();
        if(favoriteVideo1 != null){
            throw new illegalException(FAVORITE_VIDEO_EXIST);
        }

        boolean result = favoriteService.lambdaUpdate()
                .eq(Favorite::getId, favoriteId)
                .setSql("count=count+1")
                .update();
        if(!result){
            throw new illegalException(FAVORITE_NOT_EXIST);
        }
        videoDataService.lambdaUpdate()
                .eq(VideoData::getVideoId, videoId)
                .setSql("collect_count = collect_count+1")
                .update();


        FavoriteVideo favoriteVideo = new FavoriteVideo();
        favoriteVideo.setFavoriteId(favoriteId);
        favoriteVideo.setVideoId(videoId);
        favoriteVideo.setCreateTime(LocalDate.now());
        save(favoriteVideo);
    }

    @Override
    public List<FavoriteVideoVo> getFavoriteVideoList(Integer favoriteId) {
        Favorite favorite = favoriteService.lambdaQuery()
                .eq(Favorite::getId, favoriteId).one();
        if(favorite == null){
            throw new illegalException(FAVORITE_NOT_EXIST);
        }
        List<FavoriteVideo> list = lambdaQuery()
                .eq(FavoriteVideo::getFavoriteId, favoriteId)
                .list();
        if(list.isEmpty()){
            return Collections.emptyList();
        }
        Set<Integer> videoIds = list.stream().map(FavoriteVideo::getVideoId).collect(Collectors.toSet());
        List<Video> videos = videoService.lambdaQuery()
                .in(Video::getId, videoIds)
                .list();
        if(videos == null || videos.isEmpty()){
            throw new illegalException(VIDEO_NOT_EXIST);
        }
        Map<Integer, Video> videoMap = videos.stream().collect(Collectors.toMap(Video::getId, video -> video));

        List<VideoData> data = videoDataService.lambdaQuery()
                .in(VideoData::getVideoId, videoIds).list();

        Map<Integer, VideoData> videoDataMap = data.stream().collect(Collectors.toMap(VideoData::getVideoId, videoData -> videoData));

        Set<Long> videoAuthorIds = videos.stream().map(Video::getAuthorId).collect(Collectors.toSet());

        List<User> users = userService.lambdaQuery()
                .in(User::getId, videoAuthorIds)
                .list();
        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(User::getId, user -> user));

        List<FavoriteVideoVo> vo=new ArrayList<>(list.size());
        for (FavoriteVideo favoriteVideo : list) {
            FavoriteVideoVo build = FavoriteVideoVo.builder()
                    .cover(videoMap.get(favoriteVideo.getVideoId())==null?video.getCover():videoMap.get(favoriteVideo.getVideoId()).getCover())
                    .createTime(favoriteVideo.getCreateTime())
                    .VideoName(videoMap.get(favoriteVideo.getVideoId())==null?video.getName():videoMap.get(favoriteVideo.getVideoId()).getName())
                    .authorName(userMap.getOrDefault(videoMap.getOrDefault(favoriteVideo.getVideoId(), video).getAuthorId(), user).getNickName())
                    .danmuCount(Optional.ofNullable(videoDataMap.get(favoriteVideo.getVideoId())).map(VideoData::getDanmuCount).orElse(0))
                    .durationTotal(videoMap.get(favoriteVideo.getVideoId())==null?video.getDurationTotal():videoMap.get(favoriteVideo.getVideoId()).getDurationTotal())
                    .viewCount(Optional.ofNullable(videoDataMap.get(favoriteVideo.getVideoId())).map(VideoData::getViewCount).orElse(0))
                    .videoId(favoriteVideo.getVideoId())
                    .build();
            vo.add(build);
        }
        return vo;
    }
}
