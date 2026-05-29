package com.pilpil.web.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.pilpil.common.entity.UserInfo;
import com.pilpil.common.entity.po.Danmu;
import com.pilpil.common.entity.po.Video;
import com.pilpil.common.entity.po.VideoDetail;
import com.pilpil.common.exception.illegalException;
import com.pilpil.common.utils.UserHolder;
import com.pilpil.web.entity.dto.DanmuDto;
import com.pilpil.web.entity.dto.queryDanmuDto;
import com.pilpil.web.entity.vo.DanmuVo;
import com.pilpil.web.entity.vo.WebDanmuVo;
import com.pilpil.web.mapper.DanmuMapper;
import com.pilpil.web.mapper.VideoDetailMapper;
import com.pilpil.web.mapper.VideoMapper;
import com.pilpil.web.service.IDanmuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.pilpil.common.constants.Exception.exceptionConstants.Danmu.DAMU_NOT_OWNER;
import static com.pilpil.common.constants.Exception.exceptionConstants.Danmu.DANMU_NOT_EXIST;
import static com.pilpil.common.constants.Exception.exceptionConstants.Video.VIDEO_NOT_EXIST;
import static com.pilpil.common.constants.redis.redisContanst.Danmu.*;

/**
 * <p>
 * 弹幕表 服务实现类
 * </p>
 *
 * @author 
 * @since 2026-05-25
 */
@Service
@RequiredArgsConstructor
public class DanmuServiceImpl extends ServiceImpl<DanmuMapper, Danmu> implements IDanmuService {
    private final StringRedisTemplate redisTemplate;
    private final VideoMapper videoMapper;
    private final VideoDetailMapper videoDetailMapper;
    @Override
    public void saveDanmu(DanmuDto danmuDto) {
        Integer videoId = danmuDto.getVideoId();
        Integer sectionId = danmuDto.getSectionId();
        Check(videoId,sectionId);
        Danmu danmu = BeanUtil.toBean(danmuDto, Danmu.class);
        Long userId = UserHolder.get().getId();
        danmu.setCreateTime(LocalDate.now());
        danmu.setUserId(userId);
        String jsonStr = JSONUtil.toJsonStr(danmu);
        String key=DAMU_TEMPT_PREFIX+videoId+":"+sectionId;
        String key1=DANMU_RECORD_PREFIX+videoId+":"+sectionId;
        String count=DANMU_LIST_PREFIX+videoId;
        redisTemplate.opsForList().leftPush(key, jsonStr);
        redisTemplate.opsForList().leftPush(key1, jsonStr);
        redisTemplate.opsForHash().increment(count,sectionId.toString(),1);
        redisTemplate.expire(key1, 30, TimeUnit.MINUTES);
        redisTemplate.expire(key, 1, TimeUnit.DAYS);

    }

    @Override
    public void saveDanmuWebsock(WebDanmuVo bean1, UserInfo userInfo) {
        DanmuDto danmuDto = bean1.getDanmuDto();
        Integer videoId = danmuDto.getVideoId();
        Integer sectionId = danmuDto.getSectionId();
        Check(videoId,sectionId);
        Danmu danmu = BeanUtil.toBean(danmuDto, Danmu.class);
        Long userId = userInfo.getId();
        danmu.setCreateTime(LocalDate.now());
        danmu.setUserId(userId);
        String jsonStr = JSONUtil.toJsonStr(danmu);
        String key=DAMU_TEMPT_PREFIX+videoId+":"+sectionId;
        String key1=DANMU_RECORD_PREFIX+videoId+":"+sectionId;
        String count=DANMU_LIST_PREFIX+videoId;
        redisTemplate.opsForList().leftPush(key, jsonStr);
        redisTemplate.opsForList().leftPush(key1, jsonStr);
        redisTemplate.opsForHash().increment(count,sectionId.toString(),1);
        redisTemplate.expire(key1, 30, TimeUnit.MINUTES);
        redisTemplate.expire(key, 1, TimeUnit.DAYS);
    }

    @Override
    public List<DanmuVo> listDanmu(queryDanmuDto queryDanmuDto) {
        Integer sectionId = queryDanmuDto.getSectionId();
        Integer videoId = queryDanmuDto.getVideoId();
        Check(videoId,sectionId);
        String key=DANMU_RECORD_PREFIX+videoId+":"+sectionId;
        List<String> list = redisTemplate.opsForList().range(key, 0, -1);
        if(list.isEmpty()){
            //没有则查询数据库
            List<Danmu> list1 = lambdaQuery().eq(Danmu::getVideoId, videoId)
                    .eq(Danmu::getSectionId, sectionId)
                    .list();
            if(list1==null || list1.isEmpty()){
                return Collections.emptyList();
            }else{
                List<String> list2 = list1.stream().map(JSONUtil::toJsonStr).toList();

                redisTemplate.opsForList().leftPushAll(key, list2);
                redisTemplate.expire(key, 30, TimeUnit.MINUTES);
                return BeanUtil.copyToList(list1,DanmuVo.class);
            }
        }else{
            //有则直接返回
            List<DanmuVo> DanmuVos = list.stream().map(item -> {
                JSONObject jsonObject = JSONUtil.parseObj(item);
                Danmu bean1 = JSONUtil.toBean(jsonObject, Danmu.class);
                return BeanUtil.toBean(bean1, DanmuVo.class);
            }).toList();
            return DanmuVos;
        }
    }
    public void Check(Integer videoId,Integer sectionId){
        Video video = videoMapper.selectById(videoId);
        if(video==null){
            throw new illegalException(VIDEO_NOT_EXIST);
        }
        List<VideoDetail> videoDetails =videoDetailMapper.selectByVideoId(sectionId);
        if(videoDetails==null || videoDetails.isEmpty()){
            throw new illegalException(VIDEO_NOT_EXIST);
        }
    }

    @Override
    public void deleteDanmu(Integer id) {
        Danmu danmu = getById(id);
        Long userId = UserHolder.get().getId();
        if(danmu==null){
            throw new illegalException(DANMU_NOT_EXIST);
        }
        if(!danmu.getUserId().equals(userId)){
            throw new illegalException(DAMU_NOT_OWNER);
        }
        Integer sectionId = danmu.getSectionId();
        Integer videoId = danmu.getVideoId();
        lambdaUpdate().eq(Danmu::getId, id)
                .remove();
        String key=DANMU_RECORD_PREFIX+videoId+":"+sectionId;
        String key1=DAMU_TEMPT_PREFIX+videoId+":"+sectionId;
        String count=DANMU_LIST_PREFIX+videoId;
        redisTemplate.opsForList().remove(key,1,JSONUtil.toJsonStr(danmu));
        redisTemplate.opsForList().remove(key1,1,JSONUtil.toJsonStr(danmu));
        redisTemplate.opsForHash().increment(count,sectionId.toString(),-1);
    }
}
