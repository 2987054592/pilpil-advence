package com.pilpil.web.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.pilpil.common.entity.po.Favorite;
import com.pilpil.web.entity.dto.FavoriteDto;
import com.pilpil.web.entity.vo.FavoriteVo;

import java.util.List;

/**
 * <p>
 * 收藏夹 服务类
 * </p>
 *
 * @author 
 * @since 2026-05-27
 */
public interface IFavoriteService extends IService<Favorite> {

    void saveFavorite(FavoriteDto favoriteDto);

    List<FavoriteVo> getFavorite(Long userId);
}
