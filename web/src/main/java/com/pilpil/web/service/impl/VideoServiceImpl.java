package com.pilpil.web.service.impl;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pilpil.common.constants.mq.mqConstans;
import com.pilpil.common.constants.redis.redisContanst;
import com.pilpil.common.entity.UserInfo;
import com.pilpil.common.entity.dto.queryVideo;
import com.pilpil.common.entity.po.*;
import com.pilpil.common.entity.vo.VideoDetailDto;
import com.pilpil.common.entity.vo.VideoDetails;
import com.pilpil.common.entity.vo.VideoDocVo;
import com.pilpil.common.entity.vo.VideoVo;
import com.pilpil.common.enums.StatusType;
import com.pilpil.common.enums.VideoStatus;
import com.pilpil.common.exception.illegalException;
import com.pilpil.common.utils.Escommpent;
import com.pilpil.common.utils.FileOperater;
import com.pilpil.common.utils.UserHolder;

import com.pilpil.web.entity.dto.VideoDto;
import com.pilpil.web.entity.vo.MyVideoList;
import com.pilpil.web.entity.vo.MyVideoVo;
import com.pilpil.web.mapper.VideoMapper;
import com.pilpil.web.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import com.pilpil.common.entity.vo.UserVo;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.pilpil.common.constants.Exception.exceptionConstants.Category.CATEGORY_NOT_EXIST;
import static com.pilpil.common.constants.Exception.exceptionConstants.User.USER_DELETE_ERROR;
import static com.pilpil.common.constants.Exception.exceptionConstants.User.USER_STATUS_ERROR;
import static com.pilpil.common.constants.Exception.exceptionConstants.Video.VIDEO_NOT_EXIST;
import static com.pilpil.common.constants.Exception.exceptionConstants.Video.VIDEO_STATUS_ERROR;

/**
 * <p>
 * 视频表 服务实现类
 * </p>
 *
 * @author 
 * @since 2026-05-22
 */
@Service
@RequiredArgsConstructor
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video> implements IVideoService {
    private final IVideoDetailService videoDetailService;
    private final FileOperater fileOperater;
    private final ICategoryService categoryService;
    private final StringRedisTemplate redisTemplate;
    private final IUserService userService;
    private final RabbitTemplate rabbitTemplate;
    private final Escommpent escommpent;
    private final IVideoDataService videoDataService;
    private final IFansService fansService;
    @Override
    public void saveVideo(VideoDto videoDto) {
        User user = userService.getBaseMapper().selectById(UserHolder.get().getId());
        if (user.getStatus().equals(StatusType.BAN)) {
            throw new illegalException(USER_STATUS_ERROR);
        }
        //String durationKey = redisContanst.Video.DURATION_PREFIX + fileId + ":" + uploadId + ":" + ossKey;
        if(videoDto.getCategoryId()!=null) {
            Category category = categoryService.getBaseMapper().selectById(videoDto.getCategoryId());
            if (category == null) {
                throw new illegalException(CATEGORY_NOT_EXIST);
            }
        }
        videoDto.setCreateTime(LocalDate.now());
        List<VideoDetailDto> videoDetailDtos = videoDto.getVideoDetailDtos();
        Video video = BeanUtil.toBean(videoDto, Video.class);
        video.setStatus(VideoStatus.AUDIT);
        System.out.println(UserHolder.get().getId());
        video.setAuthorId(UserHolder.get().getId());
        AtomicLong totalDuration  = new AtomicLong();
        save(video);
        VideoData videoData = new VideoData();
        videoData.setVideoId(video.getId());
        videoDataService.save(videoData);
        List<String> urls=new ArrayList<>();
        List<VideoDetail> newDate=new ArrayList<>();
        videoDetailDtos.forEach(videoDetailDto -> {
            videoDetailDto.setCreateTime(video.getCreateTime());
            
            String durationKey = redisContanst.Video.DURATION_PREFIX + videoDetailDto.getMd5();
            String s = redisTemplate.opsForValue().get(durationKey);
            
            System.out.println("获取时长 - Key: " + durationKey + ", Value: " + s);
            
            VideoDetail bean = BeanUtil.toBean(videoDetailDto, VideoDetail.class);
            bean.setStatus(VideoStatus.AUDIT);
            urls.add(bean.getVideoUrl());
            
            if (s != null && !s.isEmpty()) {
                try {
                    bean.setDuration(Long.parseLong(s));
                    totalDuration.addAndGet(bean.getDuration());
                    System.out.println("成功设置时长: " + bean.getDuration());
                } catch (NumberFormatException e) {
                    System.out.println("解析时长失败，使用默认值0: " + s);
                    bean.setDuration(0L);
                }finally {
                    redisTemplate.delete(durationKey);
                }
            } else {
                System.out.println("Redis中无时长数据，使用默认值0");
                bean.setDuration(0L);
            }
            
            bean.setVideoId(video.getId());
            newDate.add(bean);
            
            fileOperater.confirmFile(videoDetailDto.getMd5(),videoDetailDto.getUploadId(),videoDetailDto.getOssKey());
        });


        VideoDoc build = VideoDoc.builder()
                .cover(videoDto.getCover())
                .name(videoDto.getName())
                .authorName(user.getNickName())
                .categoryId(videoDto.getCategoryId())
                .tags(videoDto.getTags())
                .createTime(LocalDateTime.now())
                .videoId(video.getId())
                .danmakuCount(0L)
                .playCount(0L)
                .totalDuration(totalDuration.get())
                .videoUrls(urls)
                .status(video.getStatus().getCode())
                .build();
        rabbitTemplate.convertAndSend(
                mqConstans.Exchange.VIDEO_EXCHANGE,
                mqConstans.Key.VIDEO_KEY,
                build
        );
        video.setDurationTotal(totalDuration.get());
        updateById(video);
        videoDetailService.saveBatch(newDate);
    }

    @Override
    public VideoDocVo getVideo(queryVideo queryVideo) {
        Integer pageSize = queryVideo.getPageSize();
        Integer pageNum = queryVideo.getPageNum();
        VideoDocVo vo= escommpent.searchVideo(queryVideo,pageNum,pageSize);
        List<VideoDoc> videoDocs = vo.getVideoDocs();
        List<VideoDoc> list = videoDocs.stream().filter(videoDoc -> videoDoc.getStatus().equals(VideoStatus.NORMAL.getCode())).toList();
        Set<Integer> videoId = list.stream().map(VideoDoc::getVideoId).collect(Collectors.toSet());
        if(videoId.isEmpty()){
            return VideoDocVo.builder()
                    .videoDocs(new ArrayList<>())
                    .total(0).build();
        }
        List<VideoData> list5 = videoDataService.lambdaQuery().in(VideoData::getVideoId, videoId).list();
        Set<Integer> videoIds = list5.stream().map(VideoData::getVideoId).collect(Collectors.toSet());
        List<Video> list1 = lambdaQuery().in(Video::getId, videoIds).list();

        Map<Integer, Video> videoMap1 = list1.stream().collect(Collectors.toMap(Video::getId, video -> video));

        Set<Long> userIds = list1.stream().map(Video::getAuthorId).collect(Collectors.toSet());
        List<User> list2 = userService.lambdaQuery().in(User::getId, userIds).list();
        Map<Long, User> userMap = list2.stream().collect(Collectors.toMap(User::getId, user -> user));

        Map<Integer, VideoData> videoMap = list5.stream().collect(Collectors.toMap(VideoData::getVideoId, videoData -> videoData));
        for(VideoDoc videoDoc:list){
//            VideoData list1 = videoMap.get(videoDoc.getVideoId());
//            long DanmuCount = list1.getDanmuCount();
//            long PlayCount = list1.getViewCount();
            long DanmuCount = Optional.ofNullable(videoMap.get(videoDoc.getVideoId())).map(VideoData::getDanmuCount).orElse(0);
            long PlayCount = Optional.ofNullable(videoMap.get(videoDoc.getVideoId())).map(VideoData::getViewCount).orElse(0);
            videoDoc.setPlayCount(PlayCount);
            videoDoc.setDanmakuCount(DanmuCount);
            VideoData videoData = videoMap.get(videoDoc.getVideoId());
            if(videoData!=null){
                Video video = videoMap1.get(videoDoc.getVideoId());
                if(video!=null){
                    videoDoc.setAuthorName(Optional.ofNullable(userMap.get(video.getAuthorId())).map(User::getNickName).orElse(USER_DELETE_ERROR));
                }
            }
        }
        vo.setVideoDocs(list);
        return vo;
    }

    @Override
    public VideoVo getByIdc(Integer id) {
        Video video = lambdaQuery().eq(Video::getId, id).one();
        UserInfo userInfo = UserHolder.get();
        Long userId=0L;
        if(userInfo!=null){
            userId = userInfo.getId();
        }
        if(video==null){
            throw new illegalException(VIDEO_NOT_EXIST);
        }
        if (!video.getAuthorId().equals(userId)) {
            if(video.getStatus().equals(VideoStatus.AUDIT) || video.getStatus().equals(VideoStatus.BAN)){
                throw new illegalException(VIDEO_STATUS_ERROR);
            }
            Long authorId = video.getAuthorId();
            User author = userService.lambdaQuery().eq(User::getId, authorId).one();
           if(author!=null){
               if(author.getStatus().equals(StatusType.BAN)){
                   throw new illegalException(USER_STATUS_ERROR);
               }
           }else{
               throw new illegalException(USER_STATUS_ERROR);
           }
        }

        User user = userService.getBaseMapper().selectById(video.getAuthorId());
        if(user==null||user.getStatus().equals(StatusType.BAN)){
            throw new illegalException(USER_STATUS_ERROR);
        }

        List<VideoDetail> list = videoDetailService.lambdaQuery().eq(VideoDetail::getVideoId, id).list();
        List<VideoDetail> list1 = list.stream().filter(
                videoDetail -> videoDetail.getStatus().equals(VideoStatus.NORMAL)
        ).toList();

        List<VideoDetails> videoDetails = BeanUtil.copyToList(list1, VideoDetails.class);
        VideoVo bean = BeanUtil.toBean(video, VideoVo.class);
        UserVo uservo = BeanUtil.toBean(user, UserVo.class);
        VideoData data = videoDataService.lambdaQuery().eq(VideoData::getVideoId, id).one();
        long DanmuCount = Optional.ofNullable(data).map(VideoData::getDanmuCount).orElse(0);
        long PlayCount = Optional.ofNullable(data).map(VideoData::getViewCount).orElse(0);
        long LikeCount = Optional.ofNullable(data).map(VideoData::getLikeCount).orElse(0);
        long CoinCount = Optional.ofNullable(data).map(VideoData::getCoinCount).orElse(0);
        long collectCount = Optional.ofNullable(data).map(VideoData::getCollectCount).orElse(0);
        long commentCount = Optional.ofNullable(data).map(VideoData::getCommentCount).orElse(0);
        int fans=fansService.getFollowerCount(userId);
        int follow=fansService.getFansCount(userId);
        uservo.setFans(fans);
        uservo.setFollow(follow);
        bean.setAuthorName(user.getNickName());
        bean.setCoinCount((int) CoinCount);
        bean.setLikeCount((int) LikeCount);
        bean.setFavoriteCount((int) collectCount);
        bean.setPlayCountTotal((int) PlayCount);
        bean.setDanmakuCountTotal((int) DanmuCount);
        bean.setUserVo(uservo);
        bean.setVideoDetails(videoDetails);
        bean.setCommentCount((int) commentCount);
        return bean;

    }

    @Override
    public MyVideoList getMyVideo(queryVideo queryVideo) {
        Long userId=0L;
        UserInfo userInfo = UserHolder.get();
        if(userInfo!=null){
            userId = userInfo.getId();
        }
        Long id = queryVideo.getUserId();

        User author = userService.lambdaQuery().eq(User::getId, id).one();

        Page<Video> page = lambdaQuery().eq(Video::getAuthorId, id)
                .like(queryVideo.getName() != null, Video::getName, queryVideo.getName())
                .page(new Page<>(queryVideo.getPageNum(), queryVideo.getPageSize()));
        MyVideoList vo = new MyVideoList();
        if(page==null || page.getRecords().isEmpty()){
            vo.setList(Collections.emptyList());
            vo.setTotal(0);
            vo.setPageSize(0);
            return vo;
        }

        vo.setTotal((int) page.getTotal());
        vo.setPageSize(queryVideo.getPageSize());
        List<Video> records = page.getRecords();
        if(!id.equals(userId)){
            records = records.stream().filter(video -> video.getStatus().equals(VideoStatus.NORMAL)).toList();
        }
        Set<Integer> videoId = records.stream().map(Video::getId).collect(Collectors.toSet());
        List<VideoData> list = videoDataService.lambdaQuery()
                .in(VideoData::getVideoId, videoId).list();
        Map<Integer, VideoData> videoMap = list.stream().collect(Collectors.toMap(VideoData::getVideoId, videoData -> videoData));


        List<MyVideoVo> myVideoVos = BeanUtil.copyToList(records, MyVideoVo.class);
        myVideoVos.forEach(myVideoVo -> {
            myVideoVo.setAuthorName(author.getNickName());
            myVideoVo.setDanmuCount(Optional.ofNullable(videoMap.get(myVideoVo.getId())).map(VideoData::getDanmuCount).orElse(0));
            myVideoVo.setViewCount(Optional.ofNullable(videoMap.get(myVideoVo.getId())).map(VideoData::getViewCount).orElse(0));
        });
        vo.setList(myVideoVos);
        return vo;
    }
}
