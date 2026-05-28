package com.pilpil.web.service.impl;


import cn.hutool.core.bean.BeanUtil;
import com.pilpil.common.entity.UserInfo;
import com.pilpil.common.entity.po.Favorite;
import com.pilpil.common.entity.po.FavoriteVideo;
import com.pilpil.common.enums.FavoriteShowType;
import com.pilpil.common.exception.illegalException;
import com.pilpil.common.utils.UserHolder;
import com.pilpil.web.entity.dto.FavoriteDto;
import com.pilpil.web.entity.vo.FavoriteVo;
import com.pilpil.web.mapper.FavoriteMapper;
import com.pilpil.web.service.IFavoriteService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.pilpil.common.constants.Exception.exceptionConstants.Favorite.FAVORITE_EXIST;

/**
 * <p>
 * 收藏夹 服务实现类
 * </p>
 *
 * @author 
 * @since 2026-05-27
 */
@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements IFavoriteService {
    @Override
    public void saveFavorite(FavoriteDto favoriteDto) {
        Favorite one = lambdaQuery().eq(Favorite::getUserId, UserHolder.get().getId())
                .eq(Favorite::getName, favoriteDto.getName()).one();
        if (one != null) {
            throw new illegalException(FAVORITE_EXIST);
        }
        Long UserId = UserHolder.get().getId();
        Favorite bean = BeanUtil.toBean(favoriteDto, Favorite.class);
        bean.setCreateTime(LocalDate.now());
        bean.setUserId(UserId);
        save(bean);
    }

    @Override
    public List<FavoriteVo> getFavorite(Long userId) {
        List<Favorite> list = lambdaQuery().eq(Favorite::getUserId, userId).list();
        if (list.isEmpty()) {
            return Collections.emptyList();
        }
        Long id=0L;
        UserInfo userInfo = UserHolder.get();
        if(userInfo!=null){
            id=userInfo.getId();
        }
        if(Objects.equals(userId, id)){
            List<FavoriteVo> vo = new ArrayList<>(list.size());
            for (Favorite favorite : list) {
                FavoriteVo build = FavoriteVo.builder()
                        .id(favorite.getId())
                        .FavoriteCount(favorite.getCount())
                        .name(favorite.getName())
                        .type(favorite.getVisible())
                        .build();
                vo.add(build);
            }
            return vo;
        }
        List<Favorite> list1 = list.stream().filter(favorite -> favorite.getVisible().equals(FavoriteShowType.SHOW)).toList();
        List<FavoriteVo> vo = new ArrayList<>(list1.size());
        for (Favorite favorite : list1) {
            FavoriteVo build = FavoriteVo.builder()
                    .id(favorite.getId())
                    .FavoriteCount(favorite.getCount())
                    .name(favorite.getName()).build();
            vo.add(build);
        }
        return vo;
    }
}
