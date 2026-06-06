package com.pilpil.web.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.pilpil.common.entity.po.FavoriteVideo;
import com.pilpil.web.entity.dto.FavoriteVideoDto;

import com.pilpil.web.entity.vo.FavoriteVideoVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 
 * @since 2026-05-27
 */
public interface IFavoriteVideoService extends IService<FavoriteVideo> {

    void addFavoriteVideo(FavoriteVideoDto favoriteVideoDto);

    List<FavoriteVideoVo> getFavoriteVideoList(Integer favoriteId);


}
