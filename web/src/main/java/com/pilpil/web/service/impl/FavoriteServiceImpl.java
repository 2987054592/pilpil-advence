package com.pilpil.web.service.impl;


import cn.hutool.core.bean.BeanUtil;
import com.pilpil.common.entity.po.Favorite;
import com.pilpil.common.entity.po.FavoriteVideo;
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
    public List<FavoriteVo> getFavorite() {
        List<Favorite> list = lambdaQuery().eq(Favorite::getUserId, UserHolder.get().getId()).list();
        if (list.isEmpty()) {
            return Collections.emptyList();
        }
        List<FavoriteVo> vo = new ArrayList<>(list.size());
        for (Favorite favorite : list) {
            FavoriteVo build = FavoriteVo.builder()
                    .id(favorite.getId())
                    .FavoriteCount(favorite.getCount())
                    .name(favorite.getName()).build();
            vo.add(build);
        }
        return vo;
    }
}
