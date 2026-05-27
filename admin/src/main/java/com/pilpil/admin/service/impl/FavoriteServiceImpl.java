package com.pilpil.admin.service.impl;


import com.pilpil.admin.mapper.FavoriteMapper;
import com.pilpil.admin.service.IFavoriteService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pilpil.common.entity.po.Favorite;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 收藏夹 服务实现类
 * </p>
 *
 * @author 
 * @since 2026-05-27
 */
@Service
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements IFavoriteService {

}
